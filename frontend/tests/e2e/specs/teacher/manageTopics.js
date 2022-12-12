describe('Manage Topics Walk-through', () => {
  function createTopicWithName(topicName) {
    cy.get('[data-cy="topicsNewTopicBtn"]', { force: true }).click();

    cy.get('[data-cy="topicsCreateOrEditDialog"]').should('be.visible');

    cy.get('[data-cy="topicsFormTopicNameInput"]')
      .should('be.empty')
      .type(topicName);

    cy.intercept('POST', '/topics/courses/*').as('postTopic');

    cy.get('button').contains('Save').click();

    cy.wait('@postTopic').its('response.statusCode').should('eq', 200);
  }

  beforeEach(() => {
    cy.deleteQuestionsAndAnswers();
    cy.cleanTestTopics();
    cy.demoTeacherLogin();
    cy.intercept('GET', '/topics/courses/*').as('getTopics');
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="manageTopicsMenuButton"]').click();
    cy.get('[data-cy="Search"]').click();

    cy.wait('@getTopics').its('response.statusCode').should('eq', 200);
  });

  afterEach(() => {
    cy.logout();
  });

  it('Can create a new topic', function () {
    let topicName = `CY - Test topic ${new Date().toJSON()}`;

    createTopicWithName(topicName);

    cy.get('[data-cy="topicsGrid"]').first().should('contain', topicName);
  });

  it('Can edit created new topics', function () {
    let topicName = `CY - Test topic ${new Date().toJSON()}`;

    createTopicWithName(topicName);

    cy.get('[data-cy="topicsGrid"] table > tbody > tr:first')
      .should('contain', topicName)
      .within(() => {
        cy.get('[data-cy="topicsGridEditButton"]').click();
      });

    cy.get('[data-cy="topicsCreateOrEditDialog"]').should('be.visible');

    cy.get('[data-cy="topicsFormTopicNameInput"]')
      .should('contain.value', topicName)
      .clear()
      .type('CY - EDITED');

    cy.intercept('PUT', '/topics/*').as('putTopic');

    cy.get('button').contains('Save').click();

    cy.wait('@putTopic').its('response.statusCode').should('eq', 200);

    cy.get('[data-cy="topicsGrid"]').first().should('contain', 'CY - EDITED');
  });

  it('Can delete created topics', function () {
    let topicName = `CY - Test topic ${new Date().toJSON()}`;

    createTopicWithName(topicName);

    cy.intercept('DELETE', '/topics/*').as('deleteTopic');

    cy.get('[data-cy="topicsGrid"]').contains(topicName).should('exist');

    cy.get('[data-cy="topicsGrid"] table > tbody > tr:first')
      .should('contain', topicName)
      .within(() => {
        cy.get('[data-cy="topicsGridDeleteButton"]').click();
      });

    cy.wait('@deleteTopic').its('response.statusCode').should('eq', 200);

    cy.get('[data-cy="topicsGrid"]').contains(topicName).should('not.exist');
  });
});
