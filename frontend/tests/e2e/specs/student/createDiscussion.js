describe('Student Walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('solves a quizz', () => {
    cy.solveQuizz(5);
    cy.createDiscussion('DISCUSSAO');
  });

});
