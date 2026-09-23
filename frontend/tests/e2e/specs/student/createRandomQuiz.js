describe('Create Random Quiz', () => {
  beforeEach(() => {
    cy.deleteQuestionsAndAnswers();
  });

  afterEach(() => {
    cy.deleteQuestionsAndAnswers();
  });

  it('shows no assessment available when there are none', () => {
    cy.demoStudentLogin();
    cy.get('[data-cy="quizzesStudentMenuButton"]').click();
    cy.contains('Create').click({ force: true });

    cy.contains('No assessment available').should('be.visible');

    cy.contains('Logout').click();
  });

  it('creates a random quiz successfully when an assessment exists', () => {
    // 1. Create Assessment and Questions as Teacher
    cy.demoTeacherLogin();
    cy.addTopicAndAssessment();
    
    for (let i = 1; i <= 5; i++) {
        cy.createQuestion(
          `Random Quiz Question ${i}`,
          `Content ${i}`,
          'Option 1',
          'Option 2',
          'Option 3',
          'Option 4'
        );
        cy.intercept('PUT', '/questions/*/topics').as('saveTopics');
        cy.get('[data-cy="Topics"]').eq(0).click();
        // The option lives in the menu Vuetify teleports to the end of <body>.
        // A bare `cy.contains` finds the chip of a question tagged in an
        // earlier iteration first, and that chip is collapsed to zero width.
        cy.get('.v-overlay-container .v-list-item')
          .contains('Software Architecture')
          .click();
        cy.wait('@saveTopics').its('response.statusCode').should('eq', 200);
        cy.get('body').type('{esc}');
    }
    
    cy.contains('Logout').click();

    // 2. Student creates random quiz
    cy.demoStudentLogin();
    cy.get('[data-cy="quizzesStudentMenuButton"]').click();
    cy.contains('Create').click({ force: true });

    // Select the Assessment
    cy.contains('assessment one').click();

    // Select the number of questions (5)
    cy.get('.button-group').contains('button', '5').click();

    // Click Create quiz
    cy.contains('button', 'Create quiz').click();

    // Verify redirection to solve quiz
    cy.get('.quiz-container').should('be.visible');
    cy.get('.end-quiz').should('be.visible');

    cy.contains('Logout').click();
  });
});
