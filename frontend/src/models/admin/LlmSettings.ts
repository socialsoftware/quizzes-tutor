// Which models the question generation service uses. API keys are never here: they stay in the
// service's .env, and `keys` only says whether each provider has one

export const PROVIDER_NAMES: Record<string, string> = {
  ollama: 'Ollama (local)',
  nvidia_nim: 'NVIDIA NIM',
  openai: 'OpenAI',
  anthropic: 'Anthropic',
};

export interface LlmModelChoice {
  provider: string;
  model: string;
}

export interface LlmSettings {
  primary: LlmModelChoice;
  // Tried in order when a call to the model before fails (outage, overload, timeout, model withdrawn)
  fallbacks: LlmModelChoice[];
  ollamaBaseUrl: string;
  // Tokens an Ollama model reads at once (its num_ctx); null keeps the server's default
  ollamaContextLength: number | null;
  // Ollama's "think": the model's default, off, or on with an effort
  ollamaThinking: 'default' | 'off' | 'low' | 'medium' | 'high';
  timeout: number;
  thinking: boolean;
  maxRetries: number;
  discussionSuggestions: boolean;
  contextChars: number;
}

export interface LlmSettingsView {
  settings: LlmSettings;
  keys: Record<string, boolean>;
  providers: string[];
  defaultModels: Record<string, string>;
}

export interface LlmModelTest {
  ok: boolean;
  seconds: number;
  reply: string | null;
  error: string | null;
}

export interface OllamaModels {
  models: string[];
  // Model -> "downloading", "done" or the error of its last download
  pulls: Record<string, string>;
  error: string | null;
}

export interface ProviderModels {
  provider: string;
  // The models that can write text, as the provider lists them
  models: string[];
  // Why the list could not be read (no key, provider down…)
  error: string | null;
}
