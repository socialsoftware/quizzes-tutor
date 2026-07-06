describe('Solve Available Quiz', () => {
  beforeEach(() => {
    cy.deleteQuestionsAndAnswers();

    // Create a quiz as a teacher first
    cy.demoTeacherLogin();
    cy.createQuestion(
      'SolveQuiz Question 1',
      'Content 1',
      'Option 1',
      'Option 2',
      'Option 3',
      'Option 4'
    );
    cy.createQuestion(
      'SolveQuiz Question 2',
      'Content 2',
      'Option 1',
      'Option 2',
      'Option 3',
      'Option 4'
    );
    cy.createQuizzWith2Questions(
      'SolveQuiz Title',
      'SolveQuiz Question 1',
      'SolveQuiz Question 2'
    );
    cy.contains('Logout').click();
  });

  afterEach(() => {
    cy.deleteQuestionsAndAnswers();
  });

  it('can solve an available quiz with navigation', () => {
    cy.demoStudentLogin();

    // 1. Aceder à vista AvailableQuizzesView.vue
    cy.get('[data-cy="quizzesStudentMenuButton"]').click();
    cy.contains('Available').click({ force: true });

    // 2. Clicar num quiz disponível e iniciar a resolução
    cy.contains('SolveQuiz Title').click();

    // 3. Navegar pelas perguntas usando a interface (avançar, retroceder), escolher opções e submeter o Quiz no final

    // Estamos na Pergunta 1: Vamos selecionar uma opção
    cy.get('[data-cy="optionList"]').children().eq(0).click();
    
    // Avançar para a Pergunta 2
    cy.get('[data-cy="nextQuestionButton"]').click();

    // Retroceder para a Pergunta 1 para validar navegação
    cy.get('[data-cy="previousQuestionButton"]').click();

    // Confirmar que a opção que escolhemos está selecionada e avançar de novo
    cy.get('[data-cy="nextQuestionButton"]').click();

    // Estamos na Pergunta 2: Escolher opção
    cy.get('[data-cy="optionList"]').children().eq(1).click();

    // Submeter o Quiz no final
    cy.get('[data-cy="endQuizButton"]').click();
    cy.get('[data-cy="confirmationButton"]').click();

    // 4. Verificar se os resultados aparecem corretamente na vista SolvedQuizzesView.vue
    cy.get('[data-cy="quizzesStudentMenuButton"]').click();
    cy.contains('Solved').click({ force: true });

    cy.get('[data-cy="solvedQuizzesList"]').should('contain', 'SolveQuiz Title');

    cy.contains('Logout').click();
  });
});
