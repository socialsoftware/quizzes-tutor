describe('Weekly Scores', () => {
  let date;

  beforeEach(() => {
    cy.deleteQuestionsAndAnswers();

    date = new Date();
    //create quiz
    cy.demoTeacherLogin();

    cy.createQuestion(
      'Weekly Score Question 1 ' + date,
      'Question',
      'Option',
      'Option',
      'ChooseThisWrong',
      'Correct'
    );
    cy.createQuestion(
      'Weekly Score Question 2 ' + date,
      'Question',
      'Option',
      'Option',
      'ChooseThisWrong',
      'Correct'
    );
    cy.createQuizzWith2Questions(
      'Weekly Score Title ' + date,
      'Weekly Score Question 1 ' + date,
      'Weekly Score Question 2 ' + date
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

    cy.demoStudentLogin();

    cy.solveQuizz('Weekly Score Title ' + date, 2, 'ChooseThisWrong');

    cy.get('[data-cy="dashboardMenuButton"]').click();
    cy.wait('@getDashboard');

    cy.createWeeklyScore();

    cy.get('[data-cy="weeklyScoresMenuButton"]').click();
    cy.wait('@updateWeeklyScores');

    cy.contains('Logout').click();

    Cypress.on('uncaught:exception', (err, runnable) => {
      // returning false here prevents Cypress from
      // failing the test
      return false;
    });
  });
});
