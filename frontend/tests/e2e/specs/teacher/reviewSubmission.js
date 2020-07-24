describe('Teacher walkthrough', () => {
  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.log('student submits a question');
    cy.addSubmission('Test', 'IN_REVISION', 676);
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="submissionTeacherMenuButton"]').click();
  });

  afterEach(() => {
    cy.deleteSubmission('Test', 6);
    cy.contains('Logout').click();
  });

  it('login approves a submission (AVAILABLE)', () => {
    cy.reviewSubmission('Approve (AVAILABLE)', 'Test', 'Comment');
    cy.checkSubmissionStatus('Test', 'APPROVED');
  });

  it('login approves a submission (DISABLED)', () => {
    cy.reviewSubmission('Approve (DISABLED)', 'Test', 'Comment');
    cy.checkSubmissionStatus('Test', 'APPROVED');
  });

  it('login rejects a submission', () => {
    cy.reviewSubmission('Reject', 'Test', 'Comment');
    cy.checkSubmissionStatus('Test', 'REJECTED');
  });

  it('login requests changes for a submission', () => {
      cy.reviewSubmission('Request Changes', 'Test', 'Comment');
      cy.checkSubmissionStatus('Test', 'CHANGES REQUESTED');
  });

  it('login requests further review', () => {
      cy.reviewSubmission('Request Further Review', 'Test', 'Comment');
      cy.checkSubmissionStatus('Test', 'FURTHER REVIEW REQUESTED');
  });

  it('login reviews submisssion without comment', () => {
    cy.reviewSubmission('Approve (AVAILABLE)', 'Test');

    cy.closeErrorMessage('Review must have comment');

    cy.log('close dialog');
    cy.get('[data-cy="CloseButton"]').click();
  });
});
