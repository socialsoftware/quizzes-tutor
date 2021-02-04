Cypress.Commands.add('demoAdminLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoAdminLoginButton"]').click();
});

Cypress.Commands.add('demoTeacherLogin', () => {
  cy.visit('/');
  cy.server();
  cy.route('GET', '/auth/demo/teacher').as('authTeacher');
  cy.get('[data-cy="demoTeacherLoginButton"]').click();
  cy.wait('@authTeacher')
    .its('status')
    .should('eq', 200);
});

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoStudentLoginButton"]').click();
});

Cypress.Commands.add('logout', () => {
  cy.get('[data-cy="logoutButton"]').click({ force: true });
});
