describe('Dashboard', () => {
  let date;

  beforeEach(() => {
    cy.deleteQuestionsAndAnswers();

    date = new Date();
    //create quiz
    cy.demoTeacherLogin();

    cy.addTopicAndAssessment();

    cy.createQuestion(
      'Dashboard Question 1 ' + date,
      'Question',
      'Option',
      'Option',
      'ChooseThisWrong',
      'Correct'
    );

    cy.get('[data-cy="Topics"]').click();
    cy.contains('Software Architecture').click();

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
    cy.deleteFailedAnswers();
    cy.deleteDifficultQuestions();
    cy.deleteQuestionsAndAnswers();
  });

  it('student accesses dashboard', () => {
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
    cy.intercept('PUT', '**/students/dashboards/*/difficultquestions').as(
      'updateDifficultQuestions'
    );
    cy.intercept('DELETE', '**/students/difficultquestions/*').as(
      'deleteDifficultQuestion'
    );

    cy.demoStudentLogin();
    cy.solveQuizz('Dashboard Title ' + date, 2, 'ChooseThisWrong');

    // weekly scores

    cy.get('[data-cy="dashboardMenuButton"]').click();
    cy.wait('@getDashboard');

    cy.createWeeklyScore();

    cy.get('[data-cy="weeklyScoresMenuButton"]').click();
    cy.wait('@updateWeeklyScores');

    cy.get('table tbody tr').should('have.length', 2);

    cy.get('[data-cy="failedAnswersMenuButton"]').click();
    cy.wait('@updateFailedAnswers');

    cy.get('[data-cy="showStudentViewDialog"]')
      .should('have.length', 2)
      .eq(0)
      .click();

    cy.get('[data-cy="closeButton"]').click();

    cy.get('[data-cy="deleteFailedAnswerButton"]')
      .should('have.length', 2)
      .eq(0)
      .click();
    cy.wait('@deleteFailedAnswer');

    cy.get('[data-cy="deleteFailedAnswerButton"]')
      .should('have.length', 1)
      .eq(0)
      .click();
    cy.wait('@deleteFailedAnswer');

    cy.get('[data-cy="deleteFailedAnswerButton"]').should('have.length', 0);

    // difficult  questions

    cy.get('[data-cy="difficultQuestionsMenuButton"]').click();
    cy.wait('@updateDifficultQuestions');

    cy.get('[data-cy="showStudentViewDialog"]')
      .should('have.length.at.least', 1)
      .eq(0)
      .click({ force: true });

    cy.get('[data-cy="closeButton"]').click();

    cy.get('[data-cy="deleteDifficultQuestionButton"]')
      .should('have.length', 1)
      .eq(0)
      .click();
    cy.wait('@deleteDifficultQuestion');

    cy.get('[data-cy="deleteDifficultQuestionButton"]').should(
      'have.length',
      0
    );

    cy.contains('Logout').click();

    Cypress.on('uncaught:exception', (err, runnable) => {
      // returning false here prevents Cypress from
      // failing the test
      return false;
    });
  });
});
