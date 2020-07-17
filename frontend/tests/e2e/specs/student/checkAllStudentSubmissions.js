describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.addSubmission('Test 1', 'AVAILABLE', 678, true);
    cy.addSubmission('Test 2', 'DISABLED', 679, true);
    cy.addSubmission('Test 3', 'REJECTED', 676 , false);
    cy.addSubmission('Test 4', 'IN_REVIEW', 676, false);
    cy.addSubmission('Test 5', 'SUBMITTED', 676, false);
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
    cy.deleteSubmission(null, null, false);
    cy.contains('Logout').click();
  });

  it('login checks all submissions for course execution', () => {
    cy.checkAllStudentsSubmissions('Test 1', 'Test 2', 'Test 3', 'Test 4', 'Test 5');
  });
});
