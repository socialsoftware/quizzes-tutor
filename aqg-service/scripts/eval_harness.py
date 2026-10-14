"""Compare models and prompts offline on a fixed set of cases.

Examples (from the aqg-service folder, with the service's virtual environment):

    python scripts/make_cases.py book.pdf --cases 30 --out cases.json
    python scripts/eval_harness.py cases.json --model ollama_chat/llama3.1:8b --out runs/llama
    python scripts/eval_harness.py cases.json \\
        --model ollama_chat/llama3.1:8b --model nvidia_nim/nvidia/nemotron-3-ultra-550b-a55b \\
        --system-file prompts/strict.txt --runs 2 --out runs/compare

Each --model and each --system-file (the default prompt is always included) is a variant; every
combination runs on every case. Keys come from the environment (OPENAI_API_KEY, ANTHROPIC_API_KEY,
NVIDIA_NIM_API_KEY), like in the service. The system file may contain {language}, which is replaced
by the case's language (or "Write in the language of the context."). Writes results.json (every
draft, every failure) and report.md (the comparison table) into --out and prints the table.
"""
import argparse
import json
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from app.evaluation.harness import load_cases, render_markdown, run_variant, summarize  # noqa: E402
from app.generation.prompts import system_prompt  # noqa: E402
from app.llm.provider import LiteLLMProvider  # noqa: E402
from app.schema import ModelConfig  # noqa: E402


def model_config(name: str, args) -> ModelConfig:
    base = args.ollama_base_url if name.startswith("ollama") else None
    options = {"num_ctx": args.num_ctx} if name.startswith("ollama") and args.num_ctx else None
    return ModelConfig(
        model=name, api_base=base, temperature=args.temperature, timeout=args.timeout, options=options,
        extra_body={"chat_template_kwargs": {"enable_thinking": False}} if name.startswith("nvidia_nim/") else None,
    )


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("cases", type=Path, help="JSON file made by make_cases.py")
    parser.add_argument("--model", action="append", required=True, help="LiteLLM model name, e.g. ollama_chat/llama3.1:8b")
    parser.add_argument("--system-file", action="append", default=[], help="a system prompt to compare with the default")
    parser.add_argument("--runs", type=int, default=1, help="drafts per case (the model is not deterministic)")
    parser.add_argument("--max-retries", type=int, default=2)
    parser.add_argument("--temperature", type=float, default=0.7, help="of the model that writes (checks always use 0)")
    parser.add_argument("--timeout", type=float, default=300)
    parser.add_argument("--ollama-base-url", default="http://localhost:11434")
    parser.add_argument("--num-ctx", type=int, default=None, help="Ollama context window")
    parser.add_argument("--limit", type=int, default=None, help="only the first N cases (a quick try)")
    parser.add_argument("--seed", type=int, default=0, help="seed of the option shuffling")
    parser.add_argument("--out", type=Path, default=Path("eval-run"))
    args = parser.parse_args()

    cases = load_cases(args.cases)[: args.limit]
    prompts = {"default": None, **{path: Path(path).read_text(encoding="utf-8") for path in args.system_file}}
    args.out.mkdir(parents=True, exist_ok=True)

    all_results, summaries = {}, {}
    for model in args.model:
        provider = LiteLLMProvider(model_config(model, args))
        for prompt_name, template in prompts.items():
            name = model if len(prompts) == 1 else f"{model} + {Path(prompt_name).name}"
            language = cases[0].language if cases else None
            system = template.replace("{language}", f"Write in {language}." if language else "Write in the language of the context.") if template else None
            print(f"== {name}: {len(cases)} cases x {args.runs} run(s)", flush=True)
            results = run_variant(
                provider, cases, args.runs, args.max_retries, system, args.seed,
                progress=lambda r: print(f"  {r.case_id} run {r.run}: {r.status} (retries {r.retries}, {r.seconds:.0f}s)", flush=True),
            )
            all_results[name] = [r.__dict__ for r in results]
            summaries[name] = summarize(results)

    (args.out / "results.json").write_text(json.dumps({"summaries": summaries, "results": all_results}, ensure_ascii=False, indent=2), encoding="utf-8")
    table = render_markdown(summaries)
    (args.out / "report.md").write_text(table, encoding="utf-8")
    print("\n" + table)
    print(f"Written to {args.out}/results.json and report.md")


if __name__ == "__main__":
    main()
