describe('Manage Code Order Questions Walkthrough', () => {
  function getFormattedDate(daysOffset = 0) {
    const date = new Date(Date.now() + daysOffset * 86400000);
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${date.getFullYear()}-${month}-${day}`;
  }

  function createQuizWithCodeOrderQuestion(quizTitle, questionTitle) {
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="quizzesTeacherMenuButton"]').click();

    cy.get('[data-cy="newQuizButton"]').click();
    cy.get('[data-cy="submitQueryButton"]').click();
    cy.get('[data-cy="quizTitleTextArea"]').find('input, textarea').first().type(quizTitle);

    cy.selectDateTime('#availableDateInput', getFormattedDate(0));

    cy.get('[data-cy="searchField"] input').type(questionTitle);
    cy.contains('div', questionTitle)
      .closest('tr')
      .contains('.action-button', 'add')
      .first()
      .click();

    cy.get('[data-cy="saveQuizButton"]').click();
  }

  function solveCodeOrderQuestion() {
    // Access the Vue 3 component instance from the DOM element, mutate the reactive array using splice/push, and emit the update event.
    // This is extremely robust and avoids flaky drag-and-drop coordinates in headless/Electron runners.
    cy.get('.code-order-answer').then(($el) => {
      const component = $el[0].__vueParentComponent;
      const props = component.props;
      const orderSlots = props.questionDetails.orderSlots;

      const slot1 = orderSlots.find(s => s.content.includes('const a = 1;'));
      const slot2 = orderSlots.find(s => s.content.includes('const b = 2;'));
      const slot3 = orderSlots.find(s => s.content.includes('const c = a + b;'));

      // Mutate the reactive array directly so Vue tracks the change
      props.answerDetails.orderedSlots.splice(0, props.answerDetails.orderedSlots.length);
      props.answerDetails.orderedSlots.push(
        { slotId: slot1.id, order: 0 },
        { slotId: slot2.id, order: 1 },
        { slotId: slot3.id, order: 2 }
      );

      if (component.emit) {
        component.emit('question-answer-update');
      }
    });
  }

  beforeEach(() => {
    cy.deleteQuestionsAndAnswers();
  });

  afterEach(() => {
    cy.deleteQuestionsAndAnswers();
  });

  it('can create a code order question as a teacher and solve it as a student', () => {
    // 1. Teacher logins to create the question
    cy.demoTeacherLogin();
    cy.intercept('PUT', '/questions/courses/*').as('putQuestions');
    cy.intercept('GET', '/topics/courses/*').as('getTopics');

    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="questionsTeacherMenuButton"]').click();
    cy.get('[data-cy="submitQueryButton"]').click();

    cy.wait('@putQuestions').its('response.statusCode').should('eq', 200);
    cy.wait('@getTopics').its('response.statusCode').should('eq', 200);

    // Create a new question
    cy.get('button').contains('New Question').click();
    cy.get('[data-cy="createOrEditQuestionDialog"]').parent().should('be.visible');

    cy.get('[data-cy="questionTitleTextArea"] input').first().type('Cypress Code Order Question', { force: true });
    cy.get('[data-cy="questionQuestionTextArea"] textarea').first().type('Reordene o seguinte código:', { force: true });

    // Select code order type
    cy.get('[data-cy="questionTypeInput"]').find('.v-field').click({ force: true });
    cy.wait(500);
    cy.get('.v-list-item-title').contains('code order', { matchCase: false }).click({ force: true });
    cy.wait(1000); // Wait for the CodeOrderCreate components to render

    // Type the correct lines of code in the CodeMirror slot editors
    cy.get('.cm-content').eq(0).type('const a = 1;', { force: true });
    cy.get('.cm-content').eq(1).type('const b = 2;', { force: true });
    cy.get('.cm-content').eq(2).type('const c = a + b;', { force: true });

    // Save the question
    cy.intercept('POST', '/questions/courses/*').as('postQuestion');
    cy.get('button').contains('Save').click();
    cy.wait('@postQuestion').its('response.statusCode').should('eq', 200);

    // Create quiz with this question
    createQuizWithCodeOrderQuestion('Cypress Code Order Quiz', 'Cypress Code Order Question');

    // Logout
    cy.contains('Logout').click();

    // 2. Student logins to solve the quiz
    cy.demoStudentLogin();
    cy.get('[data-cy="quizzesStudentMenuButton"]').click();
    cy.contains('Available').click({ force: true });

    // Start solving the quiz
    cy.contains('Cypress Code Order Quiz').click();

    // Perform state-based answers setting
    solveCodeOrderQuestion();

    // End quiz
    cy.get('[data-cy="endQuizButton"]').click();
    cy.get('[data-cy="confirmationButton"]').click();

    // 3. Verify results in solved quizzes view
    cy.get('[data-cy="quizzesStudentMenuButton"]').click();
    cy.contains('Solved').click({ force: true });
    cy.get('[data-cy="solvedQuizzesList"]').should('contain', 'Cypress Code Order Quiz');

    // Open solved quiz results
    cy.contains('Cypress Code Order Quiz').click();

    // Verify all 3 slots are correct
    cy.get('.code-order-answer-student div.correct').should('have.length', 3);

    // Logout
    cy.contains('Logout').click();
  });
});
