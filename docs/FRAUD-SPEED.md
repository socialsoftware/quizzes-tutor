# Deteção de respostas rápidas demais (sinal "Speed")

Na janela de fraude de um quiz (*Management › Quizzes › fraude*) há duas colunas novas: **Speed** (nível `LOW`, `MEDIUM` ou `HIGH`) e **Why** (as razões em palavras). Servem para o professor **olhar com atenção** para alguns alunos. **Não são prova**: um aluno que lê depressa, que já viu a pergunta ou que adivinhou pode ter o mesmo aspeto.

## O que mede

Usa só o que o **servidor** registou (o tempo de cada resposta, que o aluno não controla) e o tamanho da pergunta e das opções. Não recolhe nada novo no browser. Só analisa quizzes de aula de sentido único, como o resto da vista de fraude. Precisa de pelo menos 5 respostas de um aluno para o assinalar.

Um aluno ganha pontos por cada regra que cumpre:

| Regra | Pontos | Limiar |
|---|---|---|
| **Mais depressa do que se consegue ler** | 1 (ou 2 se for a maioria) | ≥ 30 % das respostas (≥ 60 % para 2 pontos) em menos tempo do que ler o enunciado e as opções a 40 caracteres por segundo (cerca de 450 palavras por minuto; mínimo 1,5 s) |
| **Tempo quase igual em tudo** | 1 | pelo menos 6 respostas, média até 30 s e variação (desvio padrão / média) abaixo de 25 %, quando o tempo de uma pessoa segue o tamanho do texto |
| **Rápidas e certas** | 1 | pelo menos metade de todas as respostas foram rápidas demais e certas |
| **Muito mais depressa do que a turma** | 1 | por carácter, pelo menos 3 vezes mais depressa do que o aluno mediano (precisa de 8 ou mais alunos com respostas suficientes) |

Nível: 1 ponto = `LOW`, 2 = `MEDIUM`, 3 ou mais = `HIGH`. A lista vem ordenada do mais suspeito para o menos.

Os valores estão no topo de `fraud-service/fraud_times/speed.py` (`READ_CHARS_PER_SECOND`, `TOO_FAST_SOME`, `UNIFORM_MAX_CV`, …) e **não foram ajustados com dados reais**: convém olhar para um quiz já corrigido e acertar os limiares.

## O que não deteta

- **Capturas de ecrã**: uma página web não recebe nenhum evento de captura, só software de vigilância instalado no computador a vê. Só se apanham as consequências (tempos).
- **Um aluno que usa uma ferramenta e espera o tempo certo** ou que lê a pergunta, a copia para uma LLM e responde com tempo "humano".
- Respostas em grupo: para isso existem as pontuações de produção e consumo que já havia.

## Como funciona (para quem mantém)

`GET /speed/quiz/{id}` no `fraud-service` → `GET /fraud/speed/quiz/{id}` no backend (professor com acesso ao quiz) → colunas novas no frontend. Se o `fraud-service` falhar neste pedido, as outras pontuações continuam a aparecer. A lógica pura e os seus testes estão em `fraud_times/speed.py` e `fraud-service/tests/test_speed.py` (`python -m pytest tests/test_speed.py`). A consulta à base de dados (`get_speed_rows` em `quiz_orm.py`) é SQL para PostgreSQL e **ainda não foi corrida contra uma base de dados real**.

## Privacidade

Não há dados novos: são tempos de resposta que o Quizzes Tutor já guardava. Mesmo assim, informa os alunos de que os tempos são analisados, e nunca penalizes só com este sinal.
