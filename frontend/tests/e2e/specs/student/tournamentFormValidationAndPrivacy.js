describe('Tournament Form Validation and Privacy UI Walkthrough', () => {
  beforeEach(() => {
    cy.deleteQuestionsAndAnswers();
    cy.demoStudentLogin();
    cy.beforeEachTournament();
    
    // Navigate to Open Tournaments and open the creation dialog
    cy.get('[data-cy="Tournament"]').click();
    cy.get('[data-cy="Open"]').click();
    cy.get('[data-cy="createButton"]').should('be.visible').click();
  });

  afterEach(() => {
    // Cancel the dialog and cleanup
    cy.get('[data-cy="cancelButton"]').click({ force: true });
    cy.logout();
    cy.deleteQuestionsAndAnswers();
  });

  it('validates required fields and privacy password rules', () => {
    // 1. Leave all fields empty and attempt to save
    cy.get('[data-cy="saveButton"]').click();
    cy.get('.v-alert')
      .should('be.visible')
      .and('contain', 'Tournament must have Start Time, End Time, Number Of Questions and Topics');
    cy.closeErrorMessage();

    // 2. Set privacy switch to Private, but leave password empty and attempt to save
    cy.tournamentCreation('5');
    cy.get('[data-cy="SwitchPrivacy"] input').check({ force: true });
    cy.get('[data-cy="saveButton"]').click();
    cy.get('.v-alert')
      .should('be.visible')
      .and('contain', 'Tournament must have a password in order to be private');
    cy.closeErrorMessage();
  });

  it('toggles password field visibility using the eye icon', () => {
    // 1. Enable privacy to show password field
    cy.get('[data-cy="SwitchPrivacy"] input').check({ force: true });
    cy.get('[data-cy="Password"] input')
      .should('be.visible')
      .and('have.attr', 'type', 'password');

    // 2. Type a password
    cy.get('[data-cy="Password"] input').type('mySecurePassword123', { force: true });

    // 3. Click the append icon (eye icon) to show the password text
    cy.get('[data-cy="Password"]')
      .find('.v-field__append-inner, .v-icon')
      .first()
      .click({ force: true });

    // 4. Verify password field is now of type text
    cy.get('[data-cy="Password"] input').should('have.attr', 'type', 'text');

    // 5. Click the append icon again to hide it
    cy.get('[data-cy="Password"]')
      .find('.v-field__append-inner, .v-icon')
      .first()
      .click({ force: true });

    // 6. Verify password field is back to type password
    cy.get('[data-cy="Password"] input').should('have.attr', 'type', 'password');
  });
});
