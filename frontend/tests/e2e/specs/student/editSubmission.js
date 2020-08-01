describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();

    cy.get('[data-cy="submissionMenuButton"]').click();
    cy.get('[data-cy="mySubmissionsMenuButton"]').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login edits a question', () => {
    cy.submitQuestion(
      true,
      'Test',
      'Test Question',
      'A',
      'B',
      'C',
      'D'
    );

    cy.editSubmission(
      true,
      ' 2',
      ' 2',
      '1',
      '2',
      '3',
      '4'
    );

    cy.viewQuestion(
      'Test 2',
      'Test Question 2',
      'A1',
      'B2',
      'C3',
      'D4'
    );
    cy.deleteSubmission();
  });

  it('login edits an invalid question', () => {
    cy.submitQuestion(
      true,
      'Test',
      'Test Question',
      'A',
      'B',
      'C',
      'D'
    );

    cy.editSubmission(
      false,
      ' 2',
      ' 2'
    );

    cy.closeErrorMessage('Error: Invalid content for option');
    cy.log('close dialog');
    cy.get('[data-cy="CancelButton"]').click();
  
    cy.deleteSubmission('Test', 5);
  });
  
});
