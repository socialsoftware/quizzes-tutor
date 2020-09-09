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
  cy.wait(1000);
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
    .should('have.length', 13)
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
      .should('have.length', 13)
      .find('[data-cy="createFromCourse"]')
      .click();
    cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
    cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add(
  'submitQuestion',
  (valid, title, content, opt1, opt2, opt3, opt4) => {
    cy.get('[data-cy="NewSubmission"]').click();
    cy.get('[data-cy="QuestionTitle"]').type(title, { force: true });
    cy.get('[data-cy="QuestionContent"]').type(content);
    cy.get('[data-cy="Switch1"]').click({ force: true });
    if (valid) {
      cy.get('[data-cy="Option1"]').type(opt1);
      cy.get('[data-cy="Option2"]').type(opt2);
      cy.get('[data-cy="Option3"]').type(opt3);
      cy.get('[data-cy="Option4"]').type(opt4);
      cy.get('[data-cy="SaveButton"]').click();
      cy.contains(title)
        .parent()
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 5);
    } else {
      cy.get('[data-cy="SaveButton"]').click();
    }
  }
);

Cypress.Commands.add(
  'editQuestionSubmission',
  (valid, title, content, opt1, opt2, opt3, opt4) => {
    cy.get('[data-cy="EditSubmission"]').click();
    cy.get('[data-cy="QuestionTitle"]').type(title, { force: true });
    cy.get('[data-cy="QuestionContent"]').type(content);
    if (valid) {
      cy.get('[data-cy="Option1"]').type(opt1);
      cy.get('[data-cy="Option2"]').type(opt2);
      cy.get('[data-cy="Option3"]').type(opt3);
      cy.get('[data-cy="Option4"]').type(opt4);
      cy.get('[data-cy="SaveButton"]').click();
    } else {
      cy.get('[data-cy="Option1"]').clear();
      cy.get('[data-cy="SaveButton"]').click();
    }
  }
);

Cypress.Commands.add('viewQuestion', (title, content, op1, op2, op3, op4, status=null) => {
  cy.contains(title)
    .parent()
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 5)
    .find('[data-cy="ViewSubmission"]')
    .click();
  cy.contains(title);
  cy.contains(content);
  cy.contains(op1);
  cy.contains(op2);
  cy.contains(op3);
  cy.contains(op4);
  if (status != null) {
    cy.contains(status);
  }
  cy.get('[data-cy="CloseButton"]').click();
});

Cypress.Commands.add('deleteQuestionSubmission', (title) => {
  cy.contains(title)
    .parent()
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 5)
    .find('[data-cy="DeleteSubmission"]')
    .click();
});

Cypress.Commands.add('reviewQuestionSubmission', (select, title, comment=null) => {
  cy.contains(title)
    .parent()
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 6)
    .find('[data-cy="ViewSubmission"]')
    .click();
  if (comment != null){
    cy.get('[data-cy="Comment"]').type(comment);
  }
  cy.log(select);
  let option = select === 'Request Further Review' ? '{downarrow}{downarrow}{downarrow}' : select;
  cy.get('[data-cy=SelectMenu]').type(option + '{enter}', {force: true})
  cy.get('[data-cy="SubmitButton"]').click();
});

Cypress.Commands.add('checkReviewComment', (title) => {
  cy.contains(title)
    .parent()
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 6)
    .find('[data-cy="ViewSubmission"]')
    .click();
  cy.contains(title);
  cy.get('[data-cy="CloseButton"]').click();
});

Cypress.Commands.add('checkSubmissionStatus', (title, status) => {
  cy.contains(title)
    .parent()
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 6)
    .contains(status)
});

Cypress.Commands.add('addUserThroughForm', (acronym, name, email, type) => {
  cy.contains(acronym)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 13)
    .find('[data-cy="addExternalUser"]')
    .click();

  cy.get('[data-cy="userNameInput"]').type(name);
  cy.get('[data-cy="userEmailInput"]').type(email);
  cy.get('[data-cy="userRoleSelect"]').parent().parent().click();
  cy.get('.v-menu__content .v-list').children().contains(type).first().click();
  cy.get('[data-cy="saveButton"]').click();
  cy.wait(3000);
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

Cypress.Commands.add('checkStudentCount', (acronym, count) => {
  cy.contains(acronym).parent().children().eq(9).contains(count);
});

Cypress.Commands.add('checkTeacherCount', (acronym, count) => {
  cy.contains(acronym).parent().children().eq(7).contains(count);
});

Cypress.Commands.add('closeUserCreationDialog', () => {
  cy.get('[data-cy="cancelButton"]').click();
});
