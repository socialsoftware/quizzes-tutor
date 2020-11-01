describe('Student Walkthrough', () => {
  beforeEach(() => {
    //create quiz
    cy.demoTeacherLogin();
    cy.createQuestion(
      'Question Title',
      'Question',
      'Option',
      'Option',
      'Option',
      'Correct'
    );
    cy.createQuestion(
      'Question Title2',
      'Question',
      'Option',
      'Option',
      'Option',
      'Correct'
    );
    cy.createQuizzWith2Questions(
      'Quiz Title',
      'Question Title',
      'Question Title2'
    );
    cy.contains('Logout').click();
  });

  it('student creates discussion', () => {
    cy.demoStudentLogin();
    cy.solveQuizz('Quiz Title', 2);
    cy.createDiscussion('DISCUSSAO');
    cy.contains('Logout').click();
    Cypress.on('uncaught:exception', (err, runnable) => {
      // returning false here prevents Cypress from
      // failing the test
      return false;
    });
  });
});
