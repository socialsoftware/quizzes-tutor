describe('Manage Assessments', () => {
  beforeEach(() => {
    cy.deleteQuestionsAndAnswers();
    // We add a topic so we have at least one topic to choose from
    cy.addTopicAndAssessment();
    cy.demoTeacherLogin();
  });

  afterEach(() => {
    cy.deleteQuestionsAndAnswers();
  });

  it('can create, edit and delete an assessment', () => {
    // Navigate to Assessments
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="assessmentsTeacherMenuButton"]').click();

    // 1. Create Assessment
    cy.get('[data-cy="newAssessmentButton"]').click();
    cy.get('[data-cy="assessmentTitle"]').type('E2E Assessment Title');
    cy.get('[data-cy="assessmentSequence"]').type('10');

    // Add topic "Software Architecture"
    // Since we called cy.addTopicAndAssessment(), we have topic "Software Architecture" in available topics.
    cy.get('[data-cy="addTopicConjunctionButton"]').first().click();

    // Save Assessment
    cy.get('[data-cy="saveAssessmentButton"]').click();

    // Verify it exists in the list
    cy.contains('E2E Assessment Title').should('be.visible');

    // 2. Edit Assessment
    cy.contains('E2E Assessment Title')
      .closest('tr')
      .find('[data-cy="editAssessmentButton"]')
      .click();

    cy.get('[data-cy="assessmentTitle"]').clear().type('Edited E2E Assessment Title');
    cy.get('[data-cy="saveAssessmentButton"]').click();

    // Verify it was edited
    cy.contains('Edited E2E Assessment Title').should('be.visible');

    // 3. Delete Assessment
    cy.contains('Edited E2E Assessment Title')
      .closest('tr')
      .find('[data-cy="deleteAssessmentButton"]')
      .click();

    // Verify it was deleted
    cy.get('tbody').contains('Edited E2E Assessment Title').should('not.exist');

    cy.contains('Logout').click();
  });
});
