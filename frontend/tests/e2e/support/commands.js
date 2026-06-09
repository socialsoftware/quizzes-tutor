// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
/// <reference types="Cypress" />

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click({ force: true });
  cy.get('[data-cy="courseExecutionNameInput"]').find('input, textarea').first().type(name);
  cy.get('[data-cy="courseExecutionAcronymInput"]').find('input, textarea').first().type(acronym);
  cy.get('[data-cy="courseExecutionAcademicTermInput"]').find('input, textarea').first().type(academicTerm);
  cy.get('[data-cy="saveButton"]').click({ force: true });
  cy.wait(1000);
});

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
  cy.get('.v-alert').find('button').click({ force: true });
});

Cypress.Commands.add('deleteCourseExecution', (acronym) => {
  cy.contains(acronym)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 13)
    .find('[data-cy="deleteCourse"]')
    .click({ force: true });
});

Cypress.Commands.add(
  'createFromCourseExecution',
  (name, acronym, academicTerm) => {
    cy.contains(name)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 13)
      .find('[data-cy="createFromCourse"]')
      .click({ force: true });
    cy.get('[data-cy="courseExecutionAcronymInput"]').find('input, textarea').first().type(acronym);
    cy.get('[data-cy="courseExecutionAcademicTermInput"]').find('input, textarea').first().type(academicTerm);
    cy.get('[data-cy="saveButton"]').click({ force: true });
  }
);

Cypress.Commands.add('seeTournamentsLists', (type) => {
  cy.intercept('GET', '**/tournaments/*/*Tournaments').as(
    'getTournaments'
  );
  cy.get('[data-cy="Tournament"]').click();
  cy.get(`[data-cy="${type}"]`).click();
  cy.wait('@getTournaments');
});

Cypress.Commands.add('createTournament', (numberOfQuestions) => {
  cy.get('[data-cy="createButton"]').should('be.visible').click();
  cy.tournamentCreation(numberOfQuestions);
  cy.get('[data-cy="saveButton"]').click();
  cy.wait(100);
});

Cypress.Commands.add('createPrivateTournament', (numberOfQuestions) => {
  cy.get('[data-cy="createButton"]')
    .should('be.visible')
    .click({ force: true });

  cy.get('[data-cy="SwitchPrivacy"] input').check({ force: true });
  cy.wait(500);
  cy.get('[data-cy="Password"] input').type('123', { force: true });
  cy.tournamentCreation(numberOfQuestions);
  cy.get('[data-cy="saveButton"]').click();
  cy.wait(100);
});

Cypress.Commands.add('tournamentCreation', (numberOfQuestions) => {
  cy.time('Start Time', 22, 0);
  cy.wait(100);
  cy.time('End Time', 25, 1);
  cy.get('[data-cy="NumberOfQuestions"] input').type(numberOfQuestions, {
    force: true,
  });
  cy.selectTopic('Software Architecture');
});

Cypress.Commands.add('createOpenTournament', (numberOfQuestions) => {
  cy.createTournament(numberOfQuestions);
  cy.updateTournamentStartTime();
});

Cypress.Commands.add('time', (date, day, type) => {
  let get = '';
  if (type === 0) {
    get = '#startTimeInput';
  } else {
    get = '#endTimeInput';
  }

  cy.get(get + ' input').click();
  
  cy.wait(1000);
  cy.get('.dp__cell_inner').eq(day).click();
  cy.get('.dp__action_select').click();
});

Cypress.Commands.add('selectTopic', (topic) => {
  cy.get('[data-cy="Topics"] tbody')
    .contains('tr', topic)
    .find('[data-cy="addTopic"]')
    .click({ force: true });
});

Cypress.Commands.add('joinTournament', (tournament) => {
  cy.selectTournamentWithAction(tournament, 'JoinTournament');
});

Cypress.Commands.add('joinPrivateTournament', (tournament) => {
  cy.joinTournament(tournament);
  cy.get('[data-cy="Password"] input').type('123');
  cy.get('[data-cy="joinPrivateTournament"]').click();
});

Cypress.Commands.add('solveTournament', (tournament) => {
  cy.selectTournamentWithAction(tournament, 'SolveQuiz');
});

Cypress.Commands.add('leaveTournament', (tournament) => {
  cy.selectTournamentWithAction(tournament, 'LeaveTournament');
});

Cypress.Commands.add('editTournament', (tournament) => {
  cy.selectTournamentWithAction(tournament, 'EditTournament');

  cy.time('End Time', 24, 1);
  cy.get('[data-cy="NumberOfQuestions"] input')
    .clear({
      force: true,
    })
    .type(5, {
      force: true,
    });
  cy.selectTopic('Web Application');
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('cancelTournament', (tournament) => {
  cy.selectTournamentWithAction(tournament, 'CancelTournament');
});

Cypress.Commands.add('removeTournament', (tournament) => {
  cy.selectTournamentWithAction(tournament, 'RemoveTournament');
});

Cypress.Commands.add('selectTournamentWithAction', (tournament, action) => {
  cy.get(
    `:nth-child(${tournament}) > :nth-child(1) > [data-cy="${action}"]`
  ).click({ force: true });
});

Cypress.Commands.add(
  'submitQuestion',
  (valid, comment, title, content, opt1, opt2, opt3, opt4) => {
    cy.get('[data-cy="NewSubmission"]').click();
    cy.get('[data-cy="QuestionTitle"]').find('input, textarea').first().type(title, { force: true });
    cy.get('[data-cy="QuestionContent"]').find('input, textarea').first().type(content);
    cy.get('[data-cy="Switch1"]').find('input').first().click({ force: true });
    if (valid) {
      cy.get('[data-cy="Option1"]').find('input, textarea').first().type(opt1);
      cy.get('[data-cy="Option2"]').find('input, textarea').first().type(opt2);
      cy.get('[data-cy="Option3"]').find('input, textarea').first().type(opt3);
      cy.get('[data-cy="Option4"]').find('input, textarea').first().type(opt4);
      if (comment != null) {
        cy.get('[data-cy="Comment"]').find('input, textarea').first().type(comment);
        cy.get('[data-cy="RequestReviewButton"]').click();
      } else {
        cy.get('[data-cy="RequestReviewButton"]').click();
        return;
      }
      cy.contains(title)
        .parent()
        .parent()
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 6);
    } else {
      cy.get('[data-cy="Comment"]').find('input, textarea').first().type(comment);
      cy.get('[data-cy="RequestReviewButton"]').click();
    }
  }
);

Cypress.Commands.add(
  'editQuestionSubmission',
  (valid, title, content, opt1, opt2, opt3, opt4, comment) => {
    cy.get('[data-cy="EditSubmission"]').first().click();
  }
);

Cypress.Commands.add(
  'saveQuestion',
  (valid, comment, title, content, opt1, opt2, opt3, opt4) => {
    cy.get('[data-cy="NewSubmission"]').click();
    cy.get('[data-cy="QuestionTitle"]').find('input, textarea').first().type(title, { force: true });
    cy.get('[data-cy="QuestionContent"]').find('input, textarea').first().type(content);
    cy.get('[data-cy="Switch1"]').find('input').first().click({ force: true });
    if (valid) {
      cy.get('[data-cy="Option1"]').find('input, textarea').first().type(opt1);
      cy.get('[data-cy="Option2"]').find('input, textarea').first().type(opt2);
      cy.get('[data-cy="Option3"]').find('input, textarea').first().type(opt3);
      cy.get('[data-cy="Option4"]').find('input, textarea').first().type(opt4);
      if (comment != null) {
        cy.get('[data-cy="Comment"]').find('input, textarea').first().type(comment);
        cy.get('[data-cy="SaveButton"]').click();
      } else {
        cy.get('[data-cy="SaveButton"]').click();
        return;
      }
      cy.contains(title)
        .parent()
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 5);
    } else {
      cy.get('[data-cy="Comment"]').find('input, textarea').first().type(comment);
      cy.get('[data-cy="SaveButton"]').click();
    }
  }
);

Cypress.Commands.add(
  'viewQuestion',
  (title, content, op1, op2, op3, op4, status = null) => {
    cy.contains(title)
      .closest('tr')
      .find('[data-cy="ViewSubmission"], .fa-comments, .fa-comment-dots, td#actions .action-button')
      .first()
      .click();
    cy.contains(title);
    cy.contains(content);
    cy.contains(op1);
    cy.contains(op2);
    cy.contains(op3);
    cy.contains(op4);
    if (status != null) {
      cy.contains(status);
    }
    cy.get('[data-cy="CloseButton"]').click();
  }
);

Cypress.Commands.add('deleteQuestionSubmission', (title) => {
  cy.contains(title)
    .closest('tr')
    .find('[data-cy="DeleteSubmission"], .mdi-delete, td#actions .action-button')
    .last()
    .click();
});

Cypress.Commands.add(
  'reviewQuestionSubmission',
  (select, title, comment = null) => {
    cy.contains(title)
      .closest('tr')
      .find('[data-cy="ViewSubmission"], .fa-comments, .fa-comment-dots, td#actions .action-button')
      .first()
      .click();
    if (comment != null) {
      cy.get('[data-cy="Comment"]').find('input, textarea').first().type(comment);
    }
    cy.get('[data-cy=SelectMenu]').click();
    cy.get('.v-overlay-container').contains(select).click({ force: true });
    cy.get('[data-cy="SubmitButton"]').click();
  }
);

Cypress.Commands.add(
  'checkReview',
  (title, comment, isComment, type = null) => {
    if (!isComment) {
      cy.contains(title)
        .closest('tr')
        .find('[data-cy="ViewSubmission"], .fa-comments, .fa-comment-dots, td#actions .action-button')
        .first()
        .click();
    }
    cy.get('[data-cy=ReviewLog]').click();
    cy.contains(comment);
    if (!isComment) {
      cy.contains(type);
    }
    cy.get('[data-cy="CloseButton"]').click();
  }
);

Cypress.Commands.add('checkSubmissionStatus', (title, status) => {
  cy.contains(title)
    .closest('tr')
    .contains(status);
});

Cypress.Commands.add(
  'addUserThroughForm',
  (acronym, name, username, email, type) => {
    cy.contains(acronym)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 13)
      .find('[data-cy="addExternalUser"]')
      .click();

    cy.get('[data-cy="userNameInput"]').find('input, textarea').first().type(name);
    cy.get('[data-cy="userUsernameInput"]').find('input, textarea').first().type(username);
    cy.get('[data-cy="userEmailInput"]').find('input, textarea').first().type(email);
    cy.get('[data-cy="userRoleSelect"]').click();
    cy.get('.v-overlay-container').contains(type).click({ force: true });
    cy.get('[data-cy="saveButton"]').click({ force: true });
    cy.wait(3000);
  }
);

Cypress.Commands.add('deleteUser', (mail, acronym) => {
  cy.contains(acronym)
    .parent()
    .children()
    .find('[data-cy="viewUsersButton"]')
    .click();

  cy.contains(mail).parent().find('input[type="checkbox"]').check({ force: true });
  cy.get('[data-cy="deleteSelectedUsersButton"]').click({ force: true });
  cy.contains('No data available');
  cy.get('[data-cy="cancelButton"]').click({ force: true });
});

Cypress.Commands.add('checkStudentCount', (acronym, count) => {
  cy.contains(acronym).parent().children().eq(9).contains(count);
});

Cypress.Commands.add('checkTeacherCount', (acronym, count) => {
  cy.contains(acronym).parent().children().eq(7).contains(count);
});

Cypress.Commands.add('closeUserCreationDialog', () => {
  cy.get('[data-cy="cancelButton"]').click({ force: true });
});

Cypress.Commands.add(
  'createQuestion',
  (title, question, option1, option2, option3, correct) => {
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="questionsTeacherMenuButton"]').click();

    //creates question1
    cy.get('[data-cy="newQuestionButton"]').click();
    cy.get('[data-cy="questionTitleTextArea"]').find('input, textarea').first().type(title);
    cy.get('[data-cy="questionQuestionTextArea"]').find('input, textarea').first().type(question);
    cy.get('[data-cy="Option1"]').find('input, textarea').first().type(option1);
    cy.get('[data-cy="Option2"]').find('input, textarea').first().type(option2);
    cy.get('[data-cy="Option3"]').find('input, textarea').first().type(option3);
    cy.get('[data-cy="Option4"]').find('input, textarea').first().type(correct);
    cy.get('[data-cy="Switch4"]').find('input').first().click({ force: true });
    cy.get('[data-cy="saveQuestionButton"]').click();
  }
);

Cypress.Commands.add(
  'createQuizzWith2Questions',
  (quizTitle, questionTitle, questionTitle2) => {
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="quizzesTeacherMenuButton"]').click();

    cy.get('[data-cy="newQuizButton"]').click();
    cy.get('[data-cy="submitQueryButton"]').click();
    cy.get('[data-cy="quizTitleTextArea"]').find('input, textarea').first().type(quizTitle);

    cy.get('#availableDateInput').click();
    cy.get('.dp__today').click();
    cy.get('.dp__action_select').click();

    cy.get('[data-cy="searchField"] input').type(questionTitle);
    cy.contains('div', questionTitle).closest('tr').contains('.action-button', 'add').first().click();

    cy.get('[data-cy="searchField"] input').clear();
    cy.get('[data-cy="searchField"] input').type(questionTitle2);
    cy.contains('div', questionTitle2).closest('tr').contains('.action-button', 'add').first().click();

    cy.get('[data-cy="saveQuizButton"]').click();
  }
);

Cypress.Commands.add(
  'solveQuizz',
  (quizTitle, numberOfQuizQuestions, option) => {
    cy.get('[data-cy="quizzesStudentMenuButton"]').click();
    cy.contains('Available').click({ force: true });

    cy.contains(quizTitle).click();

    for (let i = 1; i < numberOfQuizQuestions; i++) {
      if (option) {
        cy.get('[data-cy="optionList"]').children().contains(option).click();
      } else {
        cy.get('[data-cy="optionList"]').children().eq(1).click();
      }
      cy.get('[data-cy="nextQuestionButton"]').click();
    }

    if (option) {
      cy.get('[data-cy="optionList"]').children().contains(option).click();
    } else {
      cy.get('[data-cy="optionList"]').children().eq(0).click();
    }

    cy.get('[data-cy="endQuizButton"]').click();
    cy.get('[data-cy="confirmationButton"]').click();
  }
);

Cypress.Commands.add('createDiscussion', (discussionContent) => {
  cy.get('[data-cy="quizzesStudentMenuButton"]').click();
  cy.contains('Solved').click({ force: true });

  cy.contains('Quiz Title').click();
  cy.get('[data-cy="nextQuestionButton"]').click();
  cy.get('[data-cy="discussionTextArea"]').find('input, textarea').first().type(discussionContent);
  cy.intercept('POST', '**/discussions/create*').as('createDiscussion');
  cy.get('[data-cy="submitDiscussionButton"]').click();
  cy.wait('@createDiscussion');

  cy.get('[data-cy="quizzesStudentMenuButton"]').click();
  cy.get('[data-cy="discussionsStudentMenuButton"]').click();

  cy.contains(discussionContent)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 8);
});

Cypress.Commands.add('replyToDiscussion', (discussionContent, replyContent) => {
  cy.get('[data-cy="managementMenuButton"]').click();
  cy.get('[data-cy="discussionsTeacherButton"]').click();
  cy.contains(discussionContent)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 8)
    .find('[data-cy="showDiscussionButton"]')
    .should('be.visible')
    .click();

  cy.get('[data-cy="replyTextArea"]').scrollIntoView().should('be.visible').find('input, textarea').first().type(replyContent);
  cy.get('[data-cy="submitReplyButton"]').scrollIntoView().click({ force: true });
  cy.get('[data-cy="showDiscussionDialogCloseButton"]').click();

});

Cypress.Commands.add('deleteQuiz', (quizTitle) => {
  cy.get('[data-cy="managementMenuButton"]').click();
  cy.get('[data-cy="quizzesTeacherMenuButton"]').click();

  cy.contains(quizTitle)
    .parent()
    .should('have.length', 1)
    .parent()
    .children()
    .should('have.length', 10)
    .find('[data-cy="deleteQuizButton"]')
    .click();
});

Cypress.Commands.add('deleteQuestion', (questionTitle) => {
  cy.get('[data-cy="managementMenuButton"]').click();
  cy.get('[data-cy="questionsTeacherMenuButton"]').click();

  cy.contains(questionTitle)
    .parent()
    .should('have.length', 1)
    .parent()
    .children()
    .should('have.length', 10)
    .find('[data-cy="deleteQuestionButton"]')
    .click();
});
