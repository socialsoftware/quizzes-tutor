describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();

    cy.get('[data-cy="submissionStudentMenuButton"]').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login submits a question', () => {
    cy.submitQuestion(
      true,
      'Test',
      'Test Question',
      'A',
      'B',
      'C',
      'D'
    );

    cy.viewQuestion(
      'Test',
      'Test Question',
      'A',
      'B',
      'C',
      'D'
    );
    cy.deleteQuestionSubmission('Test');
  });

  it('login submits an invalid question', () => {
    cy.submitQuestion(
      false,
      'Test',
      'Test Question'
    );

    cy.closeErrorMessage('Question with invalid option');

    cy.log('close dialog');

    cy.get('[data-cy="CancelButton"]').click();
  });
});
