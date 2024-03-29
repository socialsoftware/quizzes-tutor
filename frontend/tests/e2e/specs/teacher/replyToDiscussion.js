describe('Teacher Walkthrough', () => {
  beforeEach(() => {
    cy.deleteQuestionsAndAnswers();
    //create quiz
    cy.demoTeacherLogin();
    cy.createQuestion(
      'Question Title Reply Test',
      'Question',
      'Option',
      'Option',
      'Option',
      'Correct'
    );
    cy.createQuestion(
      'Question Title Reply Test 2',
      'Question 2',
      'Option',
      'Option',
      'Option',
      'Correct'
    );
    cy.createQuizzWith2Questions(
      'Quiz Title Reply Test',
      'Question Title Reply Test',
      'Question Title Reply Test 2'
    );
    cy.get('[data-cy="logoutButton"]').click();

    //Creates discussion
    cy.demoStudentLogin();
    cy.solveQuizz('Quiz Title Reply Test', 2);
    cy.createDiscussion('DISCUSSAO REPLY TEST');
    cy.get('[data-cy="logoutButton"]').click();
    Cypress.on('uncaught:exception', (err, runnable) => {
      console.log(err);
      // returning false here prevents Cypress from
      // failing the test
      return false;
    });
  });

  it('reply to discussion', () => {
    cy.demoTeacherLogin();
    cy.replyToDiscussion('DISCUSSAO REPLY TEST', 'Resposta');
    cy.get('[data-cy="logoutButton"]').click();
  });
});
