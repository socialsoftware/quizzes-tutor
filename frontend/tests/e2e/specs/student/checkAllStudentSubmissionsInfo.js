describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.addSubmission('Test 1', 'AVAILABLE', 676);
    cy.addSubmission('Test 2', 'DISABLED', 676);
    cy.addSubmission('Test 3', 'REJECTED', 676);
    cy.addSubmission('Test 4', 'IN_REVIEW', 676);
    cy.addSubmission('Test 5', 'IN_REVISION', 676);
    cy.get('[data-cy="submissionMenuButton"]').click();
    cy.get('[data-cy="allSubmissionsMenuButton"]').click();
  });

  afterEach(() => {
    cy.get('[data-cy="Search"]').click();
    cy.deleteSubmission(null, null, false);
    cy.deleteSubmission(null, null, false);
    cy.deleteSubmission(null, null, false);
    cy.deleteSubmission(null, null, false);
    cy.deleteSubmission(null, null, false);
    cy.contains('Logout').click();
  });

  it('login checks all submissions for course execution', () => {
    cy.checkUserSubmissionInfo('Demo-Student', 5);
  });
});
