# Geração automática de perguntas — guia de utilização

Este documento tem três partes: **como usar** (professores), **como pôr e manter em produção** (administradores) e **segurança e limites** (o que está protegido, o que não está). A arquitetura está em `arquitetura-aqg.md` (fora do repositório, na pasta da tese).

## 1. Para professores

### O que a ferramenta faz

Escreve **rascunhos** de perguntas de escolha múltipla a partir dos documentos da disciplina (PDF, PowerPoint, Word, Markdown). Nenhuma pergunta chega aos alunos sem a aprovares: ficam à espera de revisão, como as perguntas que os alunos propõem.

### Passo a passo

1. **Carregar documentos** — em *Management › Generate*, envia os ficheiros (até 100 MB cada). Cada um passa por `PROCESSING` e fica `READY` (um livro pode demorar alguns minutos) ou `FAILED` (o motivo aparece ao passar o rato).
2. **Pôr o texto em tópicos** — quando um documento fica pronto abre-se a janela de distribuição: mostra os pedaços do documento agrupados pelos títulos, já associados a tópicos a partir desses títulos. Corrige o que for preciso (escolher outro tópico, criar tópicos novos) e confirma. **Os pedaços sem tópico não são usados** para gerar perguntas. Os tópicos formam uma árvore: gerar sobre um tópico usa também o texto dos seus subtópicos.
3. **Pedir perguntas** — escolhe o tópico, quantas perguntas (**1 a 10**), a dificuldade (fácil: lembrar; média: relacionar dois conceitos; difícil: aplicar a um caso novo), o modo (`STRICT`: só o que está no texto; `ENRICHED`: pode juntar conhecimento geral mas a resposta correta continua apoiada no texto), a língua e, se quiseres, um **foco** (ex.: "regras de sinais do produto") para as perguntas se concentrarem numa parte do tópico.
4. **Esperar** — a lista de pedidos mostra o estado. Quando acabar, as perguntas aparecem na aba **Submissions** com o prefixo `AI-`.
5. **Rever** — cada pergunta mostra o modelo que a escreveu, a explicação e, se for o caso, um aviso **"precisa de atenção humana"** com o que as verificações automáticas não conseguiram resolver. Podes **aprovar**, **rejeitar** (comentário opcional), **comentar**, ou usar **Regenerate with review**: escreves o que queres mudado (comentário obrigatório) e a mesma pergunta é reescrita.
6. **Usar** — aprovada, a pergunta entra nos quizzes e torneios como qualquer outra.

### Boas práticas

- **Lê sempre** a pergunta e a opção correta. As verificações automáticas filtram erros de formato, repetições e respostas sem apoio no texto, mas **não garantem** que a pergunta é boa ou certa.
- Documentos limpos geram melhores perguntas: PDFs com texto selecionável (não digitalizados) e com índice.
- Tópicos pequenos e bem definidos dão perguntas mais precisas do que um tópico que cobre o livro todo; usa o **foco** para afinar.
- Se o modelo diz que não há contexto suficiente, o tópico tem pouco texto: junta-lhe mais pedaços.
- Ao ler de novo um documento (reprocessar) os pedaços são refeitos e **saem dos tópicos**: voltas a distribuí-los.

### Responder a dúvidas dos alunos com ajuda

Nas discussões, o botão **Suggest reply** (só para professores) preenche a caixa de resposta com um **rascunho**. Lê, corrige e só depois carrega em *Submit*: o sistema **nunca envia nada sozinho**. O rascunho usa a pergunta, a opção que o aluno escolheu, a dúvida e o texto dos documentos ligados aos tópicos da pergunta. Não são enviados nomes nem identificadores do aluno.

### Limites (e o que significam as mensagens)

| Mensagem | Porquê |
|---|---|
| "Wait N more second(s) before asking for more questions" | Entre dois pedidos teus tem de passar um intervalo (10 s por omissão). |
| "You already have N request(s) being written…" | Tens pedidos à espera de serem recolhidos; deixa-os acabar (e abre a lista para os importar). |
| "The question generation queue is full" | Há muito trabalho pendente no serviço; tenta daqui a uns minutos. |
| "This course has already requested X of its Y questions for today" | Quota diária da disciplina; recomeça à meia-noite UTC. |
| "The model is busy right now" | O fornecedor do modelo está no limite de pedidos por minuto; tenta daqui a um minuto. |
| "Wait N more second(s) before asking for another suggested reply" | Um intervalo (5 s) entre sugestões de resposta. |

## 2. Para administradores

### Escolher o modelo

No menu, **Question Generation Model** (só aparece aos administradores do sistema):

- **Modelo principal** e até 5 **alternativas por ordem**: se o principal falha (avaria, sobrecarga, timeout), passa ao seguinte. O pedido regista que modelo(s) escreveram cada pergunta.
- Os fornecedores são `nvidia_nim`, `openai`, `anthropic` e `ollama` (local). A página lista os modelos de cada um e **testa** o modelo antes de gravar.
- **As chaves de API não se escrevem na página**: ficam no `.env` do `aqg-service` (`NVIDIA_NIM_API_KEY`, `OPENAI_API_KEY`, `ANTHROPIC_API_KEY`). A página só mostra se cada uma está definida.
- Outras opções: endereço do Ollama, janela de contexto (`num_ctx`), *thinking*, timeout, retries de verificação, caracteres de texto por prompt, e o **interruptor das sugestões de resposta**.

### Variáveis de ambiente do `aqg-service`

Os valores iniciais vêm do `.env` (`data/env/aqg-service.dev.env.example` tem a lista comentada). As de proteção:

| Variável | Omissão | Efeito |
|---|---|---|
| `AQG_API_KEY` | vazia | Chave que o backend envia em cada chamada (`X-API-Key`). **Obrigatória em produção.** |
| `LLM_REQUESTS_PER_MINUTE` | 30 | Chamadas por minuto a cada fornecedor cloud (o Ollama não tem limite). Escrever uma pergunta custa 3 chamadas ou mais; o plano gratuito do NVIDIA NIM ronda os 40 por minuto. Os pedidos esperam por vaga; um botão (sugerir resposta) desiste ao fim de 15 s. `0` desliga. |
| `DAILY_QUESTIONS_PER_COURSE` | 50 | Perguntas que cada disciplina pode pedir por dia UTC (pedidos falhados contam, porque gastaram o modelo). `0` desliga. |
| `MAX_QUEUED_QUESTIONS` / `MAX_QUEUED_JOBS` | 100 / 20 | Acima disto novos pedidos são recusados com 429 (nada é apagado da fila). |
| `MAX_CONCURRENT_JOBS` | 2 | Pedidos a escrever ao mesmo tempo; os outros esperam. |
| `ALLOW_REQUEST_MODEL` | false | Se um pedido pode trazer o seu próprio modelo, `api_base` e `api_key`. Deixar `false`. |
| `DISCUSSION_SUGGESTIONS` | true | `false` impede que texto de alunos chegue ao fornecedor do modelo. |
| `LOG_PROMPTS` | false | Regista os prompts e respostas das gerações (para depurar). Os logs passam a conter texto das aulas; as sugestões de resposta nunca se registam. |

Contagens: a **quota diária** e a **fila** são calculadas a partir da base de dados (sobrevivem a reinícios, não precisam de tarefas agendadas). Os jobs que ficaram a meio quando o serviço parou são marcados como falhados no arranque. O **ritmo por minuto** e o **intervalo por professor** (backend) estão em memória, por processo.

Do lado do backend (`application-prod.properties`): `aqg.service.apikey` (igual a `AQG_API_KEY`), `generation.min-seconds-between-requests` (10), `generation.min-seconds-between-suggestions` (5), `generation.max-pending-jobs-per-teacher` (2).

### Pôr em produção — lista de verificação

1. Gera duas chaves longas e aleatórias (`openssl rand -hex 32`): `AQG_API_KEY` (e `aqg.service.apikey`/`AQG.SERVICE.APIKEY` no backend) e `FRAUD_API_KEY` (e `fraud.service.apikey`). Sem elas os serviços arrancam abertos e avisam no log.
2. Põe a chave do NVIDIA NIM só em `NVIDIA_NIM_API_KEY` no `.env` do `aqg-service` (não no repositório). Define `LLM_REQUESTS_PER_MINUTE` abaixo do limite da tua conta.
3. **Rede**: o `aqg-service` e o `fraud-service` não devem ter porta publicada (no `docker-compose.yml` usam `expose`); a base de dados só em `127.0.0.1` ou em rede interna; só o frontend (e o backend, atrás do mesmo proxy) ficam visíveis. Fora de Docker, arranca o serviço com `--host 127.0.0.1`.
4. Põe HTTPS no proxy à frente do frontend/backend. O JWT dura 24 h.
5. Confirma com `curl` que o serviço recusa pedidos sem chave: `curl -i http://aqg-service:8000/courses/1/materials` deve dar `401`; `/health` fica aberto.
6. Faz cópias de segurança da base de dados (os documentos e os pedaços estão no schema `aqg`) e do volume `aqg-materials` (ficheiros originais).
7. Atualiza as dependências Python do `aqg-service` regularmente: lê ficheiros enviados por utilizadores.
8. Define quem é administrador: só eles mudam o modelo.

### Resolução de problemas

- **Todos os pedidos dão 401** — a chave do backend não é igual à do serviço.
- **Pedidos ficam `FAILED` com `RateLimited`** — o pedido esperou mais de 10 minutos por uma vaga no limite por minuto: há mais trabalho do que o limite deixa passar. Pede menos perguntas de cada vez, ou sobe `LLM_REQUESTS_PER_MINUTE` só se a tua conta do fornecedor o permitir.
- **`FAILED` depois de reiniciar o serviço** — é esperado: os pedidos a meio morrem com o processo e são marcados como falhados.
- **Um fornecedor "sem chave" não se deixa escolher** — define a chave no `.env` e reinicia o serviço.
- **Ver o que vai para o modelo** — `LOG_PROMPTS=true`, reiniciar, e ler o log (`docker compose logs aqg-service`).

## 3. Segurança

### O que foi considerado

| Risco | Estado |
|---|---|
| **Alguém chamar o serviço diretamente** | Mitigado: chave partilhada (`X-API-Key`, comparada em tempo constante) em todas as rotas exceto `/health`; sem porta publicada em Docker; documentação interativa desligada quando há chave. O Tutor continua a ser quem autentica e autoriza os utilizadores (cada endpoint exige professor da execução). |
| **Gastar o plano do fornecedor (negação de carteira)** | Mitigado: máximo 10 perguntas por pedido; intervalo e pedidos pendentes por professor; quota diária por disciplina; fila limitada (recusa em vez de acumular); limite por minuto por fornecedor; no máximo 2 pedidos a escrever. |
| **Injeção de instruções (prompt injection) em documentos ou em mensagens de alunos** | **Reduzido, não eliminado.** Os prompts mandam tratar o texto como dados; o modelo não tem ferramentas nem acesso a nada (só devolve texto); toda a pergunta passa por revisão do professor; o rascunho de resposta nunca é enviado sozinho. Um texto malicioso pode, no máximo, produzir uma pergunta ou um rascunho estranhos. |
| **HTML/JavaScript na saída do modelo (XSS)** | O Markdown mostrado no frontend passa por `sanitize-html` com lista de etiquetas permitidas. |
| **O modelo pedir o que não deve (SSRF, ficheiros)** | O modelo não faz pedidos. O endereço do Ollama (configurável por administradores) recusa credenciais e endereços de metadados/link-local (`169.254.x.x`, `0.0.0.0`, `metadata.google.internal`). Um pedido não pode escolher o seu próprio `api_base`/`api_key` (`ALLOW_REQUEST_MODEL=false`). |
| **Chaves de API expostas** | Só no ambiente do serviço; nunca em respostas, base de dados ou logs; a página só mostra "definida/não definida". |
| **Dados pessoais enviados ao fornecedor** | Nas sugestões de resposta vão a pergunta, a dúvida e o histórico **sem nomes nem utilizadores**. O texto das aulas vai sempre ao fornecedor escolhido: com o NVIDIA NIM isso é um tratamento de dados por terceiros — confirma que está coberto pela política da escola. `DISCUSSION_SUGGESTIONS=false` evita enviar texto de alunos; um modelo local (Ollama) mantém tudo na máquina. |
| **Ficheiros maliciosos** | Só professores carregam; extensões permitidas fixas; 100 MB; contentor sem privilégios de root. |
| **Serviço de fraude** | Chave partilhada, sem porta publicada; corrigidas as expressões de autorização dos endpoints `/fraud/execution/**` do backend (referiam `#quizId` em rotas que só têm `executionId`). |

### O que continua por tratar (riscos residuais)

- **Leitores de documentos**: não há limite de tempo nem de memória ao ler um ficheiro (PDF/Office "bomba"), nem limite de documentos por disciplina. Um professor mal-intencionado ou uma conta comprometida pode sobrecarregar o serviço. Mitigações possíveis: limites de recursos do contentor (`mem_limit`, `cpus`) e um *timeout* por documento.
- **Contas de professor comprometidas** têm acesso a tudo o que um professor tem (incluindo gastar a quota da sua disciplina).
- **Limites em memória** (ritmo por minuto no serviço, intervalo por professor no backend): reiniciam com o processo e não se partilham entre várias réplicas. Com mais de uma réplica é preciso um limitador partilhado (ex.: Redis).
- **Sem *rate limiting* nos endpoints do Tutor em geral** (login, quizzes…): fora do âmbito desta funcionalidade; deve ser feito no proxy.
- **Qualidade**: as verificações automáticas são heurísticas. A revisão do professor é a garantia.
- **Sem registo de auditoria** de quem pediu o quê, além do que fica nos pedidos e revisões.
