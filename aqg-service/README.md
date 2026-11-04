# Gerar perguntas com a IA

*Quizzes Tutor · Guia para professores*

> O `aqg-service` é o serviço que escreve os rascunhos de perguntas e as sugestões de resposta às dúvidas dos alunos. Este guia mostra como o professor o usa a partir do Tutor. Para o arrancar, ver o README da raiz do repositório.

A IA analisa os documentos da disciplina e elabora **rascunhos** de perguntas de escolha múltipla. **Nenhum conteúdo é disponibilizado aos alunos sem aprovação prévia.** Este guia apresenta o procedimento completo na interface do sistema, estruturado em 5 passos, além do processo de solicitação de rascunho de resposta para dúvidas de alunos.

### O caminho em resumo

1.  **Carregamento** do documento (PDF, PowerPoint, Word ou Markdown).

2.  **Associação** de cada secção ao respetivo tópico (procedimento executado uma única vez por documento).

3.  **Solicitação** de perguntas: definição de tópico, quantidade e nível de dificuldade.

4.  **Processamento**: o sistema gera os rascunhos em segundo plano durante alguns minutos.

5.  **Revisão** na secção *Submissions*: aprovação, rejeição ou pedido de alterações.

As funcionalidades encontram-se acessíveis em **Management › Generate** (passos 1 a 4) e em **Management › Submissions** (passo 5).

![Menu Management aberto com a opção Generate](readme-images/guia-01.jpg)

1. Aceder ao menu **Management** e selecionar a opção **Generate**.

## 1. Carregar o documento

Em **Management › Generate**, selecionar **Upload material** e escolher o ficheiro pretendido (formatos PDF, PowerPoint, Word ou Markdown, até 100 MB). O sistema processa o texto e divide-o em segmentos. O processamento de um documento extenso pode demorar alguns minutos.

![Página Generate com o painel de documentos e o formulário](readme-images/guia-02.jpg)

1. **Upload material**: selecionar o ficheiro a carregar a partir do computador.
2. **Estado**: PROCESSING durante a leitura, READY quando concluído e FAILED em caso de erro (passar o cursor sobre o estado para verificar o motivo).
3. **Under topics**: indica a quantidade de segmentos já associados a um tópico (no exemplo, 244 de 246).
4. **Botão de tópicos** (ícone de árvore): dá acesso ao passo 2.
5. **Ler outra vez**: reprocessa a segmentação do documento. Esta ação remove a associação prévia dos segmentos aos tópicos, sendo necessária nova distribuição.

> [!TIP]
> **Dica**
>
> PDFs com texto selecionável (não digitalizados) e com índice dão melhores perguntas.
>
> PDFs digitalizados (páginas que são só imagens) **não são suportados**: o serviço não faz OCR, e o documento fica em `FAILED` com a indicação de que não tem texto para ler.

## 2. Pôr cada parte do documento no seu tópico

A IA gera perguntas com base em **um tópico específico**, utilizando exclusivamente os segmentos de documento associados a esse tópico (e respetivos subtópicos). Após o carregamento do documento, deve ser definida a associação entre cada secção e o respetivo tópico. Para tal, selecionar o botão de tópicos (item 4 da figura anterior) para abrir a janela de configuração.

![Janela para pôr o documento nos tópicos](readme-images/guia-03.jpg)

1. **Suggest from headings**: gera propostas automáticas de tópicos com base nos títulos do documento (método recomendado).
2. **Tópico associado a cada título**: indicado pela etiqueta azul à direita. A indicação *No topic* (cinzento) assinala a ausência de tópico associado. Clicar na etiqueta para alterar.
3. **Topics**: lista de tópicos da disciplina e quantidade de segmentos associados a cada um. A opção *New topic* permite criar um novo tópico.
4. **Save**: guarda a distribuição efetuada.
5. **Show all pieces**: exibe o conteúdo textual de cada segmento para consulta detalhada.

### Procedimento simplificado: sugestão automática do sistema

Selecionar **Suggest from headings** e definir o número de níveis de cabeçalho a converter em tópicos. Recomenda-se selecionar o nível mais adequado à estrutura pretendida e efetuar os ajustes necessários posteriormente.

![Menu com as quatro opções de sugestão](readme-images/guia-04.jpg)

1. Opções disponíveis: *One topic per chapter* (apenas capítulos principais), *Chapters and their sections* (dois níveis hierárquicos), *Three levels of headings* e *Every heading* (conversão de todos os títulos em tópicos).

### Alteração de tópico de um segmento

Clicar na etiqueta do título correspondente e selecionar um tópico existente ou criar um novo.

![Janela para escolher o tópico de um título](readme-images/guia-05.jpg)

1. **Topic**: seleção de um tópico da lista ou de *No topic* caso a secção não deva ser utilizada.
2. **Criação de um novo tópico**: introdução da designação e clique em *Create*. O novo tópico fica subordinado ao tópico selecionado superiormente.
3. **Use topic**: aplica a seleção efetuada.

- O tópico atribuído a um título principal aplica-se automaticamente às subsecções dependentes.

- Os segmentos assinalados *sem tópico* (*No topic*) **são excluídos** da geração de perguntas.

- A distribuição de tópicos é guardada para utilizações futuras do documento.

## 3. Pedir as perguntas

Na mesma página, localiza-se o formulário **Generate questions**. Inicialmente, deve selecionar-se o tópico pretendido na lista, que indica o número de segmentos associados.

![Lista de tópicos com o número de pedaços de cada um](readme-images/guia-06.jpg)

1. **Tópicos da disciplina**, apresentados em estrutura hierárquica com os respetivos subtópicos.
2. **Quantidade de segmentos** por tópico (incluindo subtópicos). Tópicos sem segmentos associados não permitem a geração de perguntas.

Após o preenchimento dos parâmetros solicitados, selecionar **Generate**.

![Formulário de pedido de perguntas preenchido](readme-images/guia-07.jpg)

1. **Topic**: seleção do tópico. É exibida a contagem de segmentos a analisar (no exemplo, 27 segmentos de 1 documento).
2. **Focus** (opcional): especificação do âmbito dentro do tópico (ex.: *rules of addition and multiplication*). Limite máximo de 500 caracteres.
3. **Language**: seleção de *Same as the materials* ou indicação de outro idioma.
4. **Difficulty**: *Easy* (memorização de factos ou definições), *Medium* (relação entre conceitos) ou *Hard* (aplicação a novos cenários).
5. **Number of questions**: definição da quantidade pretendida (entre 1 e 10 por pedido).
6. **Based on**: *Only the topic's documents* (restrição estrita da resposta aos documentos) ou *Documents plus general knowledge* (integração de conhecimento geral, mantendo a resposta fundamentada no material base).
7. **Generate**: submissão do pedido.

> [!TIP]
> **Para perguntas mais certeiras**
>
> Tópicos delimitados e específicos originam questões de maior precisão comparativamente a tópicos abrangentes. Recomenda-se a utilização do campo **Focus** para refinar o pedido. Caso seja exibida a indicação de matéria insuficiente, deve adicionar-se mais segmentos ao tópico em causa.

## 4. Esperar

A secção **Generation requests** apresenta o estado do pedido. Durante a geração das perguntas, o estado é exibido como GENERATING. A navegação no sistema pode prosseguir normalmente durante este processo.

![Lista de pedidos com o pedido novo a ser gerado](readme-images/guia-08.jpg)

1. Pedido em estado de processamento.

Após a conclusão do processamento, o estado altera-se para READY FOR REVIEW, indicando que as perguntas estão disponíveis para validação.

![Lista de pedidos com o pedido pronto para revisão](readme-images/guia-09.jpg)

1. READY FOR REVIEW: as questões encontram-se prontas para consulta na secção *Submissions*.

Caso surja o estado FAILED, deve passar-se o cursor sobre a indicação para verificar o motivo da falha (ex.: indisponibilidade do modelo), podendo ser efetuada nova submissão.

## 5. Rever e aprovar

Aceder a **Management › Submissions**. As perguntas geradas pela IA são identificadas pelo prefixo AI-… e pela indicação do modelo responsável pela sua criação.

![Lista de Submissions com as perguntas geradas](readme-images/guia-10.jpg)

1. **Perguntas geradas**: os identificadores AI-20, AI-21 e AI-22 correspondem ao pedido efetuado neste exemplo.
2. Abertura do detalhe da pergunta.
3. **Autoria**: indicação do modelo de IA utilizado.

### Análise detalhada da pergunta

![Janela de uma pergunta gerada, com aviso das verificações automáticas](readme-images/guia-11.jpg)

1. **Aviso de verificação** (exibido apenas quando aplicável): assinala que a IA efetuou tentativas de correção, mas as validações automáticas ainda detetam inconsistências. **Recomenda-se uma verificação rigorosa do conteúdo** antes da respetiva aprovação.
2. **A pergunta e as opções.** A opção correta vem marcada com \[★\].
3. **Why the answer is correct**: justificação teórica gerada pelo sistema.
4. **Written by**: indicação do modelo e quantidade de secções analisadas no documento de origem.

> [!WARNING]
> **Obrigatoriedade de revisão técnica**
>
> Embora as validações automáticas identifiquem falhas de formatação ou de fundamentação, estas **não garantem** a qualidade pedagógica nem a precisão técnica da questão. A decisão final de aprovação é da inteira responsabilidade do docente.

### Processo de decisão

Na secção **Reviews** da mesma janela, deve selecionar-se a ação pretendida e clicar em *Submit*.

![Menu Review Type com as quatro opções](readme-images/guia-12.jpg)

1. **Comment**: campo para inserção de observações.
2. **Review Type**: seleção da decisão a aplicar (descrita na tabela seguinte).
3. **Submit**: submissão da avaliação.

| **Escolha** | **O que acontece** |
|----|----|
| **Approve** | A pergunta é aprovada e integrada na base de dados para utilização em quizzes e torneios. |
| **Reject** | A pergunta é rejeitada. A inclusão de comentário é opcional. |
| **Request Changes** | A IA **reformula a pergunta** com base no comentário inserido (preenchimento obrigatório). |
| **Comment** | Regista apenas a observação inserida, mantendo a pergunta pendente de decisão. |

### Solicitação de reformulação à IA

Para proceder a ajustamentos na questão, deve selecionar-se **Request Changes** e detalhar as alterações pretendidas. O botão de submissão passará a exibir a designação *Regenerate with review*.

![Pedido de alterações com o botão Regenerate with review](readme-images/guia-13.jpg)

1. **Introdução das instruções de alteração** (ex.: *Make the wrong options closer to the right one*).
2. **Request Changes** (a etiqueta amarela).
3. **Regenerate with review**: aciona a reescrita da questão pela IA. A lista de submissões é atualizada automaticamente assim que a nova versão estiver concluída.

## Elaboração de respostas a dúvidas de alunos com apoio da IA

Nas secções de discussão, a IA pode gerar um **rascunho** de resposta para validação, edição e envio pelo docente: **o sistema não efetua envios automáticos.**

### 1. Acesso à discussão

As mensagens enviadas pelos alunos encontram-se listadas em **Management › Discussions**.

![Lista de discussões](readme-images/guia-14.jpg)

1. **A dúvida do aluno**, com a pergunta a que se refere.
2. Abertura da discussão pretendida.

### 2. Geração do rascunho

A interface exibe a questão de origem, a dúvida do aluno e a caixa de resposta. Selecionar **Suggest reply** para iniciar o processamento.

![Discussão aberta com a caixa de resposta e o botão Suggest reply](readme-images/guia-15.jpg)

1. **A caixa de resposta.**
2. **Suggest reply**: solicita a criação de um rascunho de resposta (processo com duração de alguns segundos).
3. **Submit**: transmite a resposta final ao aluno.

### 3. Validação, edição e submissão

O rascunho é inserido na caixa de resposta. Após realizar as edições necessárias, selecionar **Submit**.

![Rascunho de resposta escrito pela IA na caixa de resposta](readme-images/guia-16.jpg)

1. **Rascunho gerado**: passível de edição direta na caixa de texto.
2. **Nova solicitação** (*Suggest reply*): gera uma proposta alternativa. Caso exista texto inserido na caixa, o sistema solicitará confirmação antes de proceder à substituição.
3. **Submit**: envia a versão validada ao aluno.

- O rascunho utiliza como base a pergunta, a opção selecionada pelo aluno, o texto da dúvida e o conteúdo dos documentos associados ao tópico.

- **Nenhum dado pessoal ou identificador do aluno** é transmitido ao modelo de IA.

- Na ausência de documentos associados ao tópico, o rascunho é elaborado exclusivamente com base no enunciado da pergunta e na dúvida apresentada.

> [!WARNING]
> **Validação obrigatória do rascunho**
>
> A IA pode gerar respostas imprecisas ou genéricas. Deve verificar-se a exatidão da informação e ajustar o tom do texto antes de efetuar o envio.

## Mensagens de sistema e notificações

De forma a otimizar a utilização dos recursos do modelo, existem limites definidos. Apresentam-se abaixo as notificações mais frequentes:

| **Mensagem** | **Descrição** |
|----|----|
| *Wait N more second(s) before asking for more questions* | Existe um intervalo mínimo obrigatório entre pedidos consecutivos (por omissão, 10 segundos). |
| *You already have N request(s) being written…* | Existem pedidos em processamento. Deve aguardar-se a sua conclusão e consultar a lista respetiva. |
| *The question generation queue is full* | A fila de processamento encontra-se lotada. Recomenda-se tentar novamente após alguns minutos. |
| *This course has already requested X of its Y questions for today* | Foi atingido o limite diário de perguntas para a disciplina. O limite é reiniciado às 00:00 (UTC). |
| *The model is busy right now* | O servidor do modelo atingiu a capacidade máxima por minuto. Recomenda-se aguardar um minuto antes de tentar novamente. |
| *Wait N more second(s) before asking for another suggested reply* | Aplica-se um intervalo mínimo entre solicitações consecutivas de rascunho de resposta (por omissão, 5 segundos). |
| *the model did not answer* | O serviço encontra-se indisponível ou com resposta lenta. Caso o problema persista, deve contactar-se o administrador do sistema. |
| *reply suggestions are turned off* | A funcionalidade de sugestão de respostas foi desativada pelo administrador. |

### Boas práticas de utilização

- **Validação prévia**: verificar sempre a pergunta e a opção correta antes de efetuar a aprovação.

- **Qualidade dos ficheiros**: utilizar preferencialmente ficheiros PDF com texto pesquisável e índice estruturado.

- **Granularidade**: definir tópicos específicos e utilizar o campo **Focus** para afinar a geração.

- **Lotes reduzidos**: solicitar pequenos volumes de perguntas (3 a 5 por pedido) e proceder à sua revisão iterativa.

- **Ajustes**: utilizar a opção **Request Changes** em vez de rejeitar questões com potencial de reaproveitamento.

- **Reprocessamento**: ao reler um documento, deve reatribuir-se os segmentos aos respetivos tópicos.

> *As capturas de ecrã apresentadas pertencem ao curso de demonstração; na interface do utilizador são exibidos os ficheiros e tópicos próprios do respetivo curso.*
