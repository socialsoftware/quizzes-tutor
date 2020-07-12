describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Tournaments')
      .should('be.visible')
      .click();
    cy.contains('All')
      .should('be.visible')
      .click();
    cy.wait(100);
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login creates a tournament', () => {
    cy.createTournament('3');
  });

  it('login creates private tournament', () => {
    cy.createPrivateTournament('3');
  });
});
