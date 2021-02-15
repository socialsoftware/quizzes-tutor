describe('Teacher walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.log('student submits a question');
    cy.addQuestionSubmission('Test', 'IN_REVIEW');
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="submissionTeacherMenuButton"]').click();
  });

  afterEach(() => {
    cy.removeQuestionSubmission(true);
    cy.contains('Logout').click();
  });

  it('login comments on a submission', () => {
    cy.reviewQuestionSubmission('Comment', 'Test', 'Comment');
    cy.checkReview('Test', 'Comment', true);
  });

  it('login approves a submission', () => {
    cy.reviewQuestionSubmission('Approve', 'Test', 'Comment');
    cy.checkSubmissionStatus('Test', 'APPROVED');
    cy.checkReview('Test', 'Comment', false, 'APPROVED');
  });

  it('login rejects a submission', () => {
    cy.reviewQuestionSubmission('Reject', 'Test', 'Comment');
    cy.checkSubmissionStatus('Test', 'REJECTED');
    cy.checkReview('Test', 'Comment', false, 'REJECTED');
  });

  it('login requests changes for a submission', () => {
    cy.reviewQuestionSubmission('Request Changes', 'Test', 'Comment');
    cy.checkSubmissionStatus('Test', 'IN REVISION');
    cy.checkReview('Test', 'Comment', false, 'CHANGES REQUESTED');
  });
});
