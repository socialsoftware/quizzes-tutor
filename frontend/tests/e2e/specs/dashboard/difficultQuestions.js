describe('Difficult Questions', () => {
  let date;

  beforeEach(() => {
    cy.deleteQuestionsAndAnswers();

    date = new Date();
    //create quiz
    cy.demoTeacherLogin();
    cy.createQuestion(
      'Question Difficult Question 1 ' + date,
      'Question',
      'Option',
      'Option',
      'ChooseThisWrong',
      'Correct'
    );
    cy.createQuestion(
      'Question Difficult Question 2 ' + date,
      'Question',
      'Option',
      'Option',
      'ChooseThisWrong',
      'Correct'
    );
    cy.createQuizzWith2Questions(
      'Difficult Question Title ' + date,
      'Question Difficult Question 1 ' + date,
      'Question Difficult Question 2 ' + date
    );
    cy.contains('Logout').click();
  });

  afterEach(() => {
    cy.deleteDifficultQuestions();
    cy.deleteQuestionsAndAnswers();
  });

  it('student accesses difficult questions', () => {
    cy.intercept('GET', '**/students/dashboards/executions/*').as(
      'getDashboard'
    );
    cy.intercept('GET', '**/students/dashboards/*/difficultquestions').as(
      'getDifficultQuestions'
    );
    cy.intercept('PUT', '**/students/dashboards/*/difficultquestions').as(
      'updateDifficultQuestions'
    );
    cy.intercept('DELETE', '**/students/difficultquestions/*').as(
      'deleteDifficultQuestion'
    );

    cy.demoStudentLogin();
    cy.solveQuizz('Difficult Question Title ' + date, 2, 'ChooseThisWrong');

    cy.get('[data-cy="dashboardMenuButton"]').click();
    cy.wait('@getDashboard');

    cy.get('[data-cy="difficultQuestionsMenuButton"]').click();
    cy.wait('@getDifficultQuestions');

    cy.get('[data-cy="refreshDifficultQuestionsMenuButton"]').click();
    cy.wait('@updateDifficultQuestions');

    cy.get('[data-cy="showStudentViewDialog"]')
      .should('have.length.at.least', 1)
      .eq(0)
      .click({ force: true });

    cy.get('[data-cy="closeButton"]').click();

    cy.get('[data-cy="deleteDifficultQuestionButton"]')
      .should('have.length.at.least', 1)
      .eq(0)
      .click();
    cy.wait('@deleteDifficultQuestion');

    cy.get('[data-cy="deleteDifficultQuestionButton"]')
      .should('have.length.at.least', 1)
      .eq(0)
      .click();
    cy.wait('@deleteDifficultQuestion');

    cy.contains('Logout').click();
    Cypress.on('uncaught:exception', (err, runnable) => {
      // returning false here prevents Cypress from
      // failing the test
      return false;
    });
  });
});
