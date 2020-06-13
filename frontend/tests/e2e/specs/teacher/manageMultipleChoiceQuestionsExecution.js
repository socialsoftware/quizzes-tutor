const { validateExtension } = require('showdown');

describe('Manage Multiple Choice Questions Walk-through', () => {
  function validateQuestion(
    title,
    content,
    optionPrefix = 'Option ',
    correctIndex = 2
  ) {
    cy.get('[data-cy="showQuestionDialog"]')
      .should('be.visible')
      .within($ls => {
        cy.get('.headline').should('contain', title);
        cy.get('span > p').should('contain', content);
        cy.get('li').each(($el, index, $list) => {
          cy.get($el).should('contain', optionPrefix + index);
          if (index === correctIndex) {
            cy.get($el).should('contain', '[★]');
          } else {
            cy.get($el).should('not.contain', '[★]');
          }
        });
      });
  }

  function validateQuestionFull(
    title,
    content,
    optionPrefix = 'Option ',
    correctIndex = 2
  ) {
    cy.log("Validate question with show dialog.");
    
    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .click();

    validateQuestion(title, content, optionPrefix, correctIndex);

    cy.get('button')
      .contains('close')
      .click();
  }

  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.server();
    cy.route('GET', '/courses/*/questions').as('getQuestions');
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="manageQuestionsMenuButton"]').click();

    cy.wait('@getQuestions')
      .its('status')
      .should('eq', 200);
  });

  afterEach(() => {
    cy.logout();
  });

  it('Creates a new multiple choice question', function() {
    cy.get('button')
      .contains('New Question')
      .click();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get(
      '[data-cy="questionTitleInput"]'
    ).type('Cypress Question Example - 01', { force: true });
    cy.get(
      '[data-cy="questionContentInput"]'
    ).type('Cypress Question Example - Content - 01', { force: true });

    cy.get('[data-cy="questionOptionsInput"')
      .should('have.length', 4)
      .each(($el, index, $list) => {
        cy.get($el).within($ls => {
          if (index === 2) {
            cy.get('[type="checkbox"]').check({ force: true });
          }
          cy.get('textarea').type('Option ' + index);
        });
      });

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('button')
      .contains('Save')
      .click();

    cy.wait('@postQuestion')
      .its('status')
      .should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress Question Example - 01');

    validateQuestionFull(
      'Cypress Question Example - 01',
      'Cypress Question Example - Content - 01'
    );
  });

  it('Can view question (with button)', function() {
    cy.get('tbody tr')
      .first()
      .within($list => {
        cy.get('button')
          .contains('visibility')
          .click();
      });

    validateQuestion(
      'Cypress Question Example - 01',
      'Cypress Question Example - Content - 01'
    );

    cy.get('button')
      .contains('close')
      .click();
    cy.get('[data-cy="showQuestionDialog"]').should('not.be.visible');
  });

  it('Can view question (with click)', function() {
    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .click();

    validateQuestion(
      'Cypress Question Example - 01',
      'Cypress Question Example - Content - 01'
    );

    cy.get('button')
      .contains('close')
      .click();
    cy.get('[data-cy="showQuestionDialog"]').should('not.be.visible');
  });

  it('Can update title (with right-click)', function() {
    cy.route('PUT', '/questions/*').as('updateQuestion');

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .rightclick();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible')
      .within($list => {
        cy.get('span.headline').should('contain', 'Edit Question');

        cy.get('[data-cy="questionTitleInput"]')
          .clear({ force: true })
          .type('Cypress Question Example - 01 - Edited', { force: true });

        cy.get('button')
          .contains('Save')
          .click();
      });

    cy.wait('@updateQuestion')
      .its('status')
      .should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress Question Example - 01 - Edited');

    validateQuestionFull(
      (title = 'Cypress Question Example - 01 - Edited'),
      (content = 'Cypress Question Example - Content - 01')
    );
  });

  it('Can update content (with button)', function() {
    cy.route('PUT', '/questions/*').as('updateQuestion');

    cy.get('tbody tr')
      .first()
      .within($list => {
        cy.get('button')
          .contains('edit')
          .click();
      });

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible')
      .within($list => {
        cy.get('span.headline').should('contain', 'Edit Question');

        cy.get('[data-cy="questionContentInput"]')
          .clear({ force: true })
          .type('Cypress New Content For Question!', { force: true });

        cy.get('button')
          .contains('Save')
          .click();
      });

    cy.wait('@updateQuestion')
      .its('status')
      .should('eq', 200);

    validateQuestionFull(
      (title = 'Cypress Question Example - 01 - Edited'),
      (content = 'Cypress New Content For Question!')
    );
  });

  // missing update all with questions as well and change data. Should also be tested for errors :D

  it('Can duplicate question', function() {
    cy.get('tbody tr')
    .first()
    .within($list => {
      cy.get('button')
        .contains('cached')
        .click();
    });

    cy.get('[data-cy="createOrEditQuestionDialog"]')
        .parent()
        .should('be.visible');

      cy.get('span.headline').should('contain', 'New Question');

      cy.get('[data-cy="questionTitleInput"]')
        .should('have.value','Cypress Question Example - 01 - Edited')
        .type('{end} - DUP', {force: true});
      cy.get(
        '[data-cy="questionContentInput"]'
      )
      .should('have.value', 'Cypress New Content For Question!');

      cy.get('[data-cy="questionOptionsInput"')
        .should('have.length', 4)
        .each(($el, index, $list) => {
          cy.get($el).within($ls => {
            cy.get('textarea').should('have.value','Option ' + index);
          });
        });

      cy.route('POST', '/courses/*/questions/').as('postQuestion');

      cy.get('button')
        .contains('Save')
        .click();

      cy.wait('@postQuestion')
        .its('status')
        .should('eq', 200);

      cy.get('[data-cy="questionTitleGrid"]')
        .first()
        .should('contain', 'Cypress Question Example - 01 - Edited - DUP');

      validateQuestionFull(
        'Cypress Question Example - 01 - Edited - DUP',
        'Cypress New Content For Question!'
      );
    });

    it('Can delete created question', function() {
      cy.route('DELETE', '/questions/*').as('deleteQuestion');
      cy.get('tbody tr')
        .first()
        .within($list => {
          cy.get('button')
            .contains('delete')
            .click();
        });

      cy.wait('@deleteQuestion')
        .its('status')
        .should('eq', 200);
    });
});
