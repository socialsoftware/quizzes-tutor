describe('Teacher walkthrough', () => {
  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.log('student submits a question');
    cy.exec(
      'PGPASSWORD=' +
      Cypress.env('PASS') +
      ' psql -d ' +
      Cypress.env('DBNAME') +
      ' -U ' +
      Cypress.env('USER') +
      ' -h localhost -c "WITH quest AS (insert into questions (creation_date, content, title, status, course_id, key) VALUES (current_timestamp, \'Test Question\', \'Test\', \'SUBMITTED\', 2, 200) returning id) insert into submissions (question_id, user_id, course_execution_id) VALUES ((SELECT id from quest), 676, 11);" '
    );
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="submissionTeacherMenuButton"]').click();
  });

  afterEach(() => {
    cy.deleteSubmission('Test', 7);
    cy.contains('Logout').click();
  });

  it('login approves a submission (AVAILABLE)', () => {
    cy.reviewSubmission('Available', 'Test', 'Justification');
    cy.checkSubmissionStatus('Test', 'APPROVED');
  });

  it('login approves a submission (DISABLED)', () => {
    cy.reviewSubmission('Disabled', 'Test', 'Justification');
    cy.checkSubmissionStatus('Test', 'APPROVED');
  });

  it('login rejects a submission', () => {
    cy.reviewSubmission('Rejected', 'Test', 'Justification');
    cy.checkSubmissionStatus('Test', 'REJECTED');
  });

  it('login requests changes for a submission', () => {
      cy.reviewSubmission('Submitted', 'Test', 'Justification');
      cy.checkSubmissionStatus('Test', 'CHANGES REQUESTED');
  });

  it('login requests further review', () => {
      cy.reviewSubmission('InReview', 'Test', 'Justification');
      cy.checkSubmissionStatus('Test', 'FURTHER REVIEW REQUESTED');
  });

  it('login reviews submisssion without justification', () => {
    cy.reviewSubmission('Available', 'Test');

    cy.closeErrorMessage('Review must have justification');

    cy.log('close dialog');
    cy.get('[data-cy="CloseButton"]').click();
  });
});
