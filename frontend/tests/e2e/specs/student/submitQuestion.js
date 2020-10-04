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
      'Comment',
      'Test',
      'Test Question',
      'A',
      'B',
      'C',
      'D'
    );

    cy.viewQuestion('Test', 'Test Question', 'A', 'B', 'C', 'D');
    cy.removeQuestionSubmission(true);
  });

  it('login saves a question', () => {
    cy.saveQuestion(true, null, 'Test', 'Test Question', 'A', 'B', 'C', 'D');

    cy.viewQuestion('Test', 'Test Question', 'A', 'B', 'C', 'D');
    cy.deleteQuestionSubmission('Test');
  });

  it('login submits an invalid question', () => {
    cy.submitQuestion(false, 'Comment', 'Test', 'Test Question');

    cy.log('close dialog');
    cy.closeErrorMessage();
  });

  it('login submits a question with no comment', () => {
    cy.submitQuestion(true, null, 'Test', 'Test Question', 'A', 'B', 'C', 'D');

    cy.log('close dialog');
    cy.closeErrorMessage();

    cy.get('[data-cy="CancelButton"]').click();
  });
});
