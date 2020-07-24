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
  (valid, title, content, opt1, opt2, opt3, opt4) => {
    cy.get('[data-cy="SubmitQuestion"]').click();
    cy.get('[data-cy="QuestionTitle"]').type(title, { force: true });
    cy.get('[data-cy="QuestionContent"]').type(content);
    cy.get('[data-cy="Switch1"]').click({ force: true });
    if(valid) {
      cy.get('[data-cy="Option1"]').type(opt1);
      cy.get('[data-cy="Option2"]').type(opt2);
      cy.get('[data-cy="Option3"]').type(opt3);
      cy.get('[data-cy="Option4"]').type(opt4);
      cy.get('[data-cy="SubmitButton"]').click();
      cy.contains(title)
        .parent()
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 5);
    } else {
      cy.get('[data-cy="SubmitButton"]').click();
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
    .find('[data-cy="viewQuestion"]')
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
  cy.get('[data-cy="close"]').click();
});

Cypress.Commands.add('deleteSubmission', (title=null, size=null, reviews=true) => {
  if(title != null) {
    cy.contains(title)
      .parent()
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', size)
      .find('[data-cy="deleteSubmission"]')
      .click();
  } else if (reviews) {
    cy.exec(
      'PGPASSWORD=' +
      Cypress.env('PASS') +
      ' psql -d ' +
      Cypress.env('DBNAME') +
      ' -U ' +
      Cypress.env('USER') +
      ' -h localhost -c "WITH rev AS (DELETE FROM reviews WHERE id IN (SELECT max(id) FROM reviews) RETURNING submission_id), sub AS (DELETE FROM submissions WHERE id IN (SELECT * FROM rev) RETURNING question_id), opt AS (DELETE FROM options WHERE question_id IN (SELECT * FROM sub) RETURNING question_id) DELETE FROM questions WHERE id IN (SELECT * FROM opt);" '
     );
  }else {
    cy.exec(
      'PGPASSWORD=' +
      Cypress.env('PASS') +
      ' psql -d ' +
      Cypress.env('DBNAME') +
      ' -U ' +
      Cypress.env('USER') +
      ' -h localhost -c "WITH sub AS (DELETE FROM submissions WHERE id IN (SELECT max(id) FROM submissions) RETURNING question_id), opt AS (DELETE FROM options WHERE question_id IN (SELECT * FROM sub) RETURNING question_id) DELETE FROM questions WHERE id IN (SELECT * FROM opt);" '
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
    '\') insert into reviews(creation_date, comment, status, submission_id, user_id) values (current_timestamp, \'test\', \'AVAILABLE\', (select id from sub), 677);"'
  );
});

Cypress.Commands.add('reviewSubmission', (select, title, comment=null) => {
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
  cy.get('[data-cy="SelectMenu"]').select(select);
  cy.get('[data-cy="SubmitButton"]').click();
});

Cypress.Commands.add('checkSubmissionStatus', (title, status) => {
  cy.contains(title)
    .parent()
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 6)
    .find('[data-cy="ViewSubmission"]')
    .click();
  cy.contains(title);
  cy.contains(status);
  cy.get('[data-cy="CloseButton"]').click();
});

Cypress.Commands.add('addSubmission', (title, questionStatus, userId) => {
  cy.exec(
    'PGPASSWORD=' +
    Cypress.env('PASS') +
    ' psql -d ' +
    Cypress.env('DBNAME') +
    ' -U ' +
    Cypress.env('USER') +
    ' -h localhost -c "WITH quest AS (INSERT INTO questions (title, content, status, course_id, creation_date) VALUES (\'' +
    title +
    '\', \'Question?\', \'' +
    questionStatus +
    '\', 2, current_timestamp) RETURNING id) INSERT INTO submissions (question_id, user_id, course_execution_id) VALUES ((SELECT id from quest), ' +
    userId +
    ', 11);" '
  );
  //add options
  for (let content in [0, 1, 2, 3]) {
    let correct = content === 'A' ? 't' : 'f';
    cy.exec(
      'PGPASSWORD=' +
      Cypress.env('PASS') +
      ' psql -d ' +
      Cypress.env('DBNAME') +
      ' -U ' +
      Cypress.env('USER') +
      ' -h localhost -c "WITH quest AS (SELECT * FROM questions WHERE title=\'' +
      title +
      '\') INSERT INTO options(content, correct, question_id, sequence) VALUES (\'' + content + '\', \'' + correct + '\', (SELECT id FROM quest),'+ content +');" '
    );
  }

  Cypress.Commands.add('checkUserSubmissionInfo', (username, num) => {
    cy.contains(username);
    cy.contains(num);
  });
});

