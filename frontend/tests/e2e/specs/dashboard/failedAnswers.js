describe('Failed Answers', () => {
  let date;

  beforeEach(() => {
    cy.deleteQuestionsAndAnswers();

    date = new Date();
    //create quiz
    cy.demoTeacherLogin();
    cy.createQuestion(
      'Question Failed Answer 1 ' + date,
      'Question',
      'Option',
      'Option',
      'ChooseThisWrong',
      'Correct'
    );
    cy.createQuestion(
      'Question Failed Answer 2 ' + date,
      'Question',
      'Option',
      'Option',
      'ChooseThisWrong',
      'Correct'
    );
    cy.createQuizzWith2Questions(
      'Failed Answers Title ' + date,
      'Question Failed Answer 1 ' + date,
      'Question Failed Answer 2 ' + date
    );
    cy.contains('Logout').click();
  });

  afterEach(() => {
    cy.deleteFailedAnswers();
    cy.deleteQuestionsAndAnswers();
  });

  it('student accesses failed Answerf', () => {
    cy.intercept('GET', '**/students/dashboards/executions/*').as(
      'getDashboard'
    );
    cy.intercept('GET', '**/students/dashboards/*/failedanswers').as(
      'getFailedAnswers'
    );
    cy.intercept('PUT', '**/students/dashboards/*/failedanswers').as(
      'updateFailedAnswers'
    );
    cy.intercept('DELETE', '**/students/failedanswers/*').as(
      'deleteFailedAnswer'
    );

    cy.demoStudentLogin();
    cy.solveQuizz('Failed Answers Title ' + date, 2, 'ChooseThisWrong');

    cy.get('[data-cy="dashboardMenuButton"]').click();
    cy.wait('@getDashboard');

    cy.get('[data-cy="failedAnswersMenuButton"]').click();
    cy.wait('@getFailedAnswers');

    cy.get('[data-cy="refreshFailedAnswersMenuButton"]').click();
    cy.wait('@updateFailedAnswers');

    cy.get('[data-cy="showStudentViewDialog"]')
      .should('have.length.at.least', 1)
      .eq(0)
      .click();

    cy.get('[data-cy="closeButton"]').click();

    cy.get('[data-cy="deleteFailedAnswerButton"]')
      .should('have.length.at.least', 1)
      .eq(0)
      .click();
    cy.wait('@deleteFailedAnswer');

    cy.closeErrorMessage();

    cy.setFailedAnswersAsOld();

    cy.get('[data-cy="refreshFailedAnswersMenuButton"]').click();
    cy.wait('@updateFailedAnswers');

    cy.get('[data-cy="deleteFailedAnswerButton"]')
      .should('have.length.at.least', 1)
      .eq(0)
      .click();
    cy.wait('@deleteFailedAnswer');

    cy.contains('Logout').click();
    Cypress.on('uncaught:exception', (err, runnable) => {
      // returning false here prevents Cypress from
      // failing the test
      return false;
    });
  });
});
