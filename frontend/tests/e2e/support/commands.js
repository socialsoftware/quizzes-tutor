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

Cypress.Commands.add('seeTournamentsLists', type => {
  cy.get('[data-cy="Tournament"]').click();
  cy.get('[data-cy="' + type + '"]').click();
  cy.wait(100);
});

Cypress.Commands.add('createTournament', numberOfQuestions => {
  cy.get('[data-cy="createButton"]')
    .should('be.visible')
    .click({ force: true });
  cy.time('Start Time', 16, 0);
  cy.wait(100);
  cy.time('End Time', 18, 1);
  cy.get('[data-cy="NumberOfQuestions"]').type(numberOfQuestions, {
    force: true
  });
  cy.selectTopic('Architectural Style');
  cy.selectTopic('Case Studies');
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('createPrivateTournament', numberOfQuestions => {
  cy.createTournament(numberOfQuestions);
  cy.get('[data-cy="SwitchPrivacy"]').click({ force: true });
  cy.wait(500);
  cy.get('[data-cy="Password"]').type('123', { force: true });
  cy.selectTopic('Architectural Style');
  cy.selectTopic('Case Studies');
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('time', (date, day, type) => {
  let get1 = '';
  let get2 = '';
  if (type === 0) {
    get1 = '#startTimeInput-picker-container-DatePicker';
    get2 = '#startTimeInput-wrapper';
  } else {
    get1 = '#endTimeInput-picker-container-DatePicker';
    get2 = '#endTimeInput-wrapper';
  }

  cy.get('label')
    .contains(date)
    .click({force: true});

  cy.get(get1)
    .should('have.length', 1)
    .children()
    .should('have.length', 1)
    .children()
    .should('have.length', 3)
    .eq(0)
    .children()
    .should('have.length', 3)
    .eq(2)
    .click({force: true})

  cy.wait(500);

  cy.get(get1)
    .should('have.length', 1)
    .children()
    .should('have.length', 1)
    .children()
    .should('have.length', 3)
    .eq(2)
    .children()
    .contains(day)
    .click()
    .get(get2)
    .children()
    .should('have.length', 2)
    .eq(1)
    .click({force: true});
});

Cypress.Commands.add('selectTopic', topic => {
  cy.get('[data-cy="Topics"]')
    .should('have.length', 1)
    .children()
    .should('have.length', 4)
    .contains(topic)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 2)
    .find('[data-cy="addTopic"]')
    .click();
});

Cypress.Commands.add('joinTournament', tournament => {
  cy.get('tbody')
    .children()
    .eq(tournament)
    .children()
    .should('have.length', 9)
    .eq(0)
    .find('[data-cy="JoinTournament"]')
    .click({ force: true });
});

Cypress.Commands.add('joinPrivateTournament', tournament => {
  cy.get('tbody')
    .children()
    .eq(tournament)
    .children()
    .should('have.length', 9)
    .eq(0)
    .find('[data-cy="JoinTournament"]')
    .click({ force: true });

  cy.get('[data-cy="Password"]').type('123');
});

Cypress.Commands.add('solveTournament', tournament => {
  cy.get('tbody')
    .children()
    .eq(tournament)
    .children()
    .should('have.length', 9)
    .eq(0)
    .find('[data-cy="SolveQuiz"]')
    .click({ force: true });
});
