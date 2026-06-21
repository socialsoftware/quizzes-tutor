describe('Manage Quizzes', () => {
  beforeEach(() => {
    cy.deleteQuestionsAndAnswers();
    cy.demoTeacherLogin();

    cy.createQuestion(
      'ManageQuizzes Question 1',
      'Question Content',
      'Option 1',
      'Option 2',
      'ChooseThisWrong',
      'Correct'
    );
    cy.createQuestion(
      'ManageQuizzes Question 2',
      'Question Content',
      'Option 1',
      'Option 2',
      'ChooseThisWrong',
      'Correct'
    );

    cy.createQuizzWith2Questions(
      'ManageQuizzes Title',
      'ManageQuizzes Question 1',
      'ManageQuizzes Question 2'
    );
  });

  afterEach(() => {
    cy.deleteQuestionsAndAnswers();
  });

  it('can edit a quiz', () => {
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="quizzesTeacherMenuButton"]').click();

    // Click edit on the created quiz
    cy.contains('ManageQuizzes Title')
      .closest('tr')
      .find('.action-button')
      .contains('edit')
      .click();

    // Edit title
    cy.get('[data-cy="quizTitleTextArea"]')
      .find('input, textarea')
      .first()
      .clear()
      .type('Edited ManageQuizzes Title');

    cy.get('[data-cy="saveQuizButton"]').click();

    // Verify it was edited
    cy.get('tbody').contains('Edited ManageQuizzes Title');

    cy.contains('Logout').click();
  });

  it('can view a quiz', () => {
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="quizzesTeacherMenuButton"]').click();

    // Click show on the created quiz
    cy.contains('ManageQuizzes Title')
      .closest('tr')
      .find('.action-button')
      .contains('visibility')
      .click();

    // Close dialog
    cy.contains('button', 'close', { matchCase: false }).scrollIntoView().click({ force: true });

    cy.contains('Logout').click();
  });

  it('can delete a quiz', () => {
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="quizzesTeacherMenuButton"]').click();

    // Click delete on the created quiz
    cy.contains('ManageQuizzes Title')
      .closest('tr')
      .find('.action-button')
      .contains('delete')
      .click();

    // Verify it disappeared
    cy.get('tbody').contains('ManageQuizzes Title').should('not.exist');

    cy.contains('Logout').click();
  });
});
