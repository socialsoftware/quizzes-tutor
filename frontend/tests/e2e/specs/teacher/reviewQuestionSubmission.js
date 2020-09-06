describe('Teacher walkthrough', () => {
  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.log('student submits a question');
    cy.addQuestionSubmission('Test', 'IN_REVISION', 2);
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="submissionTeacherMenuButton"]').click();
  });

  afterEach(() => {
    cy.removeQuestionSubmission(true);
    cy.contains('Logout').click();
  });

  it('login approves a submission', () => {
    cy.reviewQuestionSubmission('Approve', 'Test', 'Comment');
    cy.checkSubmissionStatus('Test', 'APPROVED');
    cy.checkReviewComment('Test');
  });

  it('login rejects a submission', () => {
    cy.reviewQuestionSubmission('Reject', 'Test', 'Comment');
    cy.checkSubmissionStatus('Test', 'REJECTED');
    cy.checkReviewComment('Test');
  });

  it('login requests changes for a submission', () => {
    cy.reviewQuestionSubmission('Request Changes', 'Test', 'Comment');
    cy.checkSubmissionStatus('Test', 'IN REVISION');
    cy.checkReviewComment('Test');
  });

  it('login requests further review', () => {
    cy.reviewQuestionSubmission('Request Further Review', 'Test', 'Comment');
    cy.checkSubmissionStatus('Test', 'IN REVIEW');
    cy.checkReviewComment('Test');
  });
});
