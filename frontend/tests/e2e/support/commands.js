// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
/// <reference types="Cypress" />

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click({force: true});
  cy.get('[data-cy="courseExecutionNameInput"]').type(name);
  cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
  cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
  cy.contains('Error')
    .parent()
    .find('button')
    .click();
});

Cypress.Commands.add('deleteCourseExecution', acronym => {
  cy.contains(acronym)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 11)
    .find('[data-cy="deleteCourse"]')
    .click();
});

Cypress.Commands.add(
  'createFromCourseExecution',
  (name, acronym, academicTerm) => {
    cy.contains(name)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 11)
      .find('[data-cy="createFromCourse"]')
      .click();
    cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
    cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add('addTeacherThroughForm', (acronym, name, email) => {
  cy.contains(acronym)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 11)
    .find('[data-cy="addExternalUser"]')
    .click();

  cy.get('[data-cy="userNameInput"]').type(name);
  cy.get('[data-cy="userEmailInput"]').type(email);
  cy.get('[data-cy="userRoleSelect"]').parent().parent().click();
  cy.get('.v-menu__content .v-list').children().contains("TEACHER").first().click();
  cy.get('[data-cy="saveButton"]').click();
  cy.get('[data-cy="cancelButton"]').click();

  cy.get('[data-cy="homeLink"]').click();
  cy.wait(2000);
  cy.get('[data-cy="administrationMenuButton"]').click();
  cy.get('[data-cy="manageCoursesMenuButton"]').click();

  cy.contains(acronym).parent().children().eq(6).contains("1");
  
});

Cypress.Commands.add('addStudentThroughForm', (acronym, name, email) => {
  cy.contains(acronym)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 11)
    .find('[data-cy="addExternalUser"]')
    .click();

  cy.get('[data-cy="userNameInput"]').type(name);
  cy.get('[data-cy="userEmailInput"]').type(email);
  cy.get('[data-cy="userRoleSelect"]').parent().parent().click();
  cy.get('.v-menu__content .v-list').children().contains("STUDENT").first().click();
  cy.get('[data-cy="saveButton"]').click();
  cy.get('[data-cy="cancelButton"]').click();

  cy.get('[data-cy="homeLink"]').click();
  cy.wait(2000);
  cy.get('[data-cy="administrationMenuButton"]').click();
  cy.get('[data-cy="manageCoursesMenuButton"]').click();

  
});

Cypress.Commands.add('deleteUser', (mail, acronym) => {
  cy.contains(acronym)
    .parent()
    .children()
    .find('[data-cy="viewUsersButton"]')
    .click();

  cy.contains(mail).parent().children().eq(0).click();
  cy.get('[data-cy="deleteSelectedUsersButton"').click();
  cy.contains('No data available');
  cy.get('[data-cy="cancelButton"').click()
});
