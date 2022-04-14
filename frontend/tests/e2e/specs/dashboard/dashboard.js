describe('Dashboard', () => {
  let date;

  beforeEach(() => {
    cy.deleteQuestionsAndAnswers();

    date = new Date();
    //create quiz
    cy.demoTeacherLogin();

    cy.createQuestion(
      'Dashboard Question 1 ' + date,
      'Question',
      'Option',
      'Option',
      'ChooseThisWrong',
      'Correct'
    );
    cy.createQuestion(
      'Dashboard Question 2 ' + date,
      'Question',
      'Option',
      'Option',
      'ChooseThisWrong',
      'Correct'
    );
    cy.createQuizzWith2Questions(
      'Dashboard Title ' + date,
      'Dashboard Question 1 ' + date,
      'Dashboard Question 2 ' + date
    );
    cy.contains('Logout').click();
  });

  afterEach(() => {
    cy.deleteWeeklyScores();
    cy.deleteQuestionsAndAnswers();
  });

  it('student accesses weekly scores', () => {
    cy.intercept('GET', '**/students/dashboards/executions/*').as(
      'getDashboard'
    );
    cy.intercept('PUT', '**/students/dashboards/*/weeklyscores').as(
      'updateWeeklyScores'
    );
    cy.intercept('PUT', '**/students/dashboards/*/failedanswers').as(
      'updateFailedAnswers'
    );
    cy.intercept('DELETE', '**/students/failedanswers/*').as(
      'deleteFailedAnswer'
    );

    cy.demoStudentLogin();
    cy.solveQuizz('Dashboard Title ' + date, 2, 'ChooseThisWrong');

    cy.get('[data-cy="dashboardMenuButton"]').click();
    cy.wait('@getDashboard');

    cy.createWeeklyScore();

    cy.get('[data-cy="weeklyScoresMenuButton"]').click();
    cy.wait('@updateWeeklyScores');

    cy.get('[data-cy="failedAnswersMenuButton"]').click();
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
