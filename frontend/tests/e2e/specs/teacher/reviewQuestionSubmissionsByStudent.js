describe('Teacher Submissions By Student Walkthrough', () => {
  beforeEach(() => {
    cy.deleteQuestionsAndAnswers();
    // Pre-populate with a question submission from Demo Student
    cy.demoStudentLogin();
    cy.get('[data-cy="submissionStudentMenuButton"]').click();
    cy.submitQuestion(
      true,
      'Test Comment',
      'Cypress Submission Title',
      'Cypress Submission Content',
      'A',
      'B',
      'C',
      'D'
    );
    cy.get('[data-cy="logoutButton"]').click({ force: true });
  });

  afterEach(() => {
    cy.deleteQuestionsAndAnswers();
  });

  it('can view, filter and expand submissions grouped by student', () => {
    // 1. Login as Demo Teacher
    cy.demoTeacherLogin();

    // 2. Navigate to Submissions
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="submissionTeacherMenuButton"]').click();

    // 3. Click "Sort by Students" to switch views
    cy.contains('.v-btn', 'Sort by Students', { matchCase: false }).click();
    cy.url().should('include', '/management/submissions/students');

    // 4. Search for Demo Student
    cy.get('[data-cy="Search"]').type('Demo Student');

    // 5. Expand the student row to see submissions
    cy.contains('Demo Student').closest('tr').click();

    // 6. Verify that the expanded student submissions table shows our test submission
    cy.get('.studentSubmissions')
      .should('be.visible')
      .within(() => {
        cy.contains('Cypress Submission Title');
        cy.contains('IN REVIEW');
        
        // Open the submission dialog
        cy.get('.fa-comments').first().click();
      });

    // 7. Verify the dialog with submission details is shown
    cy.get('.v-dialog')
      .should('be.visible')
      .within(() => {
        cy.contains('Cypress Submission Title');
        cy.contains('Cypress Submission Content');
        cy.get('[data-cy="CloseButton"]').click();
      });

    // 8. Logout
    cy.get('[data-cy="logoutButton"]').click({ force: true });
  });
});
