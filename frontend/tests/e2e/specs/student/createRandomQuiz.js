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
        cy.get('[data-cy="Topics"]').eq(0).click();
        cy.contains('Software Architecture').click();
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
