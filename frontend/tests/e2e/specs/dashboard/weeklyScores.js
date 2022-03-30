describe('Difficult Questions', () => {
  let date;

  beforeEach(() => {
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

  it('student accesses difficult questions', () => {
    cy.intercept('GET', '**/students/dashboards/executions/*').as(
      'getDashboard'
    );
    cy.intercept('GET', '**/students/dashboards/*/weeklyscores').as(
      'getWeeklyScores'
    );
    cy.intercept('PUT', '**/students/dashboards/*/weeklyscores').as(
      'updateWeeklyScores'
    );
    cy.intercept('DELETE', '**/students/weeklyscores/*').as(
      'deleteWeeklyScore'
    );

    cy.demoStudentLogin();
    cy.solveQuizz('Difficult Question Title ' + date, 2, 'ChooseThisWrong');

    cy.get('[data-cy="dashboardMenuButton"]').click();
    cy.wait('@getDashboard');

    cy.get('[data-cy="weeklyScoresMenuButton"]').click();
    cy.wait('@getWeeklyScores');

    cy.get('[data-cy="refreshWeeklyScoresMenuButton"]').click();
    cy.wait('@updateWeeklyScores');

    cy.get('[data-cy="deleteWeeklyScoreButton"]')
      .should('have.length', 1)
      .eq(0)
      .click();

    cy.closeErrorMessage();

    cy.get('[data-cy="deleteWeeklyScoreButton"]').should('have.length', 1);

    //cy.wait('@deleteWeeklyScore');

    cy.contains('Logout').click();
    Cypress.on('uncaught:exception', (err, runnable) => {
      // returning false here prevents Cypress from
      // failing the test
      return false;
    });
  });
});
