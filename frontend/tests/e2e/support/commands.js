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
  cy.get('[data-cy="createButton"]').click();
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

Cypress.Commands.add(
  'submitQuestion',
  (valid, title, content, opt1, opt2, opt3, opt4, argument=null, anonymous=false) => {
    cy.get('[data-cy="SubmitQuestion"]').click();
    cy.get('[data-cy="QuestionTitle"]').type(title, { force: true });
    cy.get('[data-cy="QuestionContent"]').type(content);
    cy.get('[data-cy="Switch1"]').click({ force: true });
    if(valid) {
      cy.get('[data-cy="Option1"]').type(opt1);
      cy.get('[data-cy="Option2"]').type(opt2);
      cy.get('[data-cy="Option3"]').type(opt3);
      cy.get('[data-cy="Option4"]').type(opt4);
      if (argument != null){
        cy.get('[data-cy="Argument"]').type(argument);
      }
      if (anonymous) {
        cy.get('[data-cy="Anonymous"]').click({ force: true });
      }
      cy.get('[data-cy="SubmitButton"]').click();
      cy.contains(title)
        .parent()
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 6);
    } else {
      cy.get('[data-cy="SubmitButton"]').click();
    }
  }
);

Cypress.Commands.add('viewQuestion', (title, content, op1, op2, op3, op4, argument=null, status=null) => {
  cy.contains(title)
    .parent()
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 6)
    .find('[data-cy="viewQuestion"]')
    .click();
  cy.contains(title);
  cy.contains(content);
  cy.contains(op1);
  cy.contains(op2);
  cy.contains(op3);
  cy.contains(op4);
  if (argument != null){
    cy.contains(argument);
  }
  if (status != null) {
    cy.contains(status);
  }
  cy.get('[data-cy="close"]').click();
});

Cypress.Commands.add('deleteSubmission', (title=null) => {
  if(title != null) {
    cy.contains(title)
      .parent()
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 6)
      .find('[data-cy="deleteSubmission"]')
      .click();
  } else {
    cy.exec(
      'PGPASSWORD=' +
      Cypress.env('PASS') +
      ' psql -d ' +
      Cypress.env('DBNAME') +
      ' -U ' +
      Cypress.env('USER') +
      ' -h localhost -c "WITH rev AS (DELETE FROM reviews WHERE id IN (SELECT max(id) FROM reviews) RETURNING submission_id), sub AS (DELETE FROM submissions WHERE id IN (SELECT * FROM rev) RETURNING question_id), opt AS (DELETE FROM options WHERE question_id IN (SELECT * FROM sub) RETURNING question_id) DELETE FROM questions WHERE id IN (SELECT * FROM opt);" '
    );
  }
});

Cypress.Commands.add('addReview', title => {
  cy.exec(
    'PGPASSWORD=' +
    Cypress.env('PASS') +
    ' psql -d ' +
    Cypress.env('DBNAME') +
    ' -U ' +
    Cypress.env('USER') +
    ' -h localhost -c "with sub as (select s.id from submissions s join questions q on s.question_id=q.id where q.title=\'' +
    title +
    '\') insert into reviews(creation_date, justification, status, submission_id, user_id) values (current_timestamp, \'test\', \'AVAILABLE\', (select id from sub), 677);"'
  );
});
