describe('Teacher Walkthrough', () => {

  it('reply to discussion', () => {
    cy.demoStudentLogin();
    cy.solveQuizz(5);
    cy.createDiscussion('DISCUSSAO');
    cy.contains('Logout').click();

    cy.demoTeacherLogin();
    cy.replyToDiscussion('DISCUSSAO', 'Resposta');
    cy.contains('Logout').click();
  });

});
