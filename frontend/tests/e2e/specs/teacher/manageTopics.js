describe('Manage Topics Walk-through', () => {
  function createTopicWithName(topicName) {
    cy.get('[data-cy="topicsNewTopicBtn"]', { force: true }).click();

    cy.get('[data-cy="topicsCreateOrEditDialog"]').should('be.visible');

    cy.get('[data-cy="topicsFormTopicNameInput"]')
      .should('be.empty')
      .type(topicName);

    cy.route('POST', '/courses/*/topics/').as('postTopic');

    cy.get('button')
      .contains('Save')
      .click();

    cy.wait('@postTopic')
      .its('status')
      .should('eq', 200);
  }

  beforeEach(() => {
    cy.cleanTestTopics();
    cy.demoTeacherLogin();
    cy.server();
    cy.route('GET', '/courses/*/topics').as('getTopics');
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="manageTopicsMenuButton"]').click();
    cy.get('[data-cy="Search"]').click();

    cy.wait('@getTopics')
      .its('status')
      .should('eq', 200);
  });

  afterEach(() => {
    cy.logout();
  });

  it('Can create a new topic', function() {
    let topicName = `CY - Test topic ${new Date().toJSON()}`;

    createTopicWithName(topicName);

    cy.get('[data-cy="topicsGrid"]')
      .first()
      .should('contain', topicName);
  });

  it('Can edit created new topics', function() {
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

    cy.route('PUT', '/topics/*').as('putTopic');

    cy.get('button')
      .contains('Save')
      .click();

    cy.wait('@putTopic')
      .its('status')
      .should('eq', 200);

    cy.get('[data-cy="topicsGrid"]')
      .first()
      .should('contain', 'CY - EDITED');
  });

  it('Can delete created topics', function() {
    let topicName = `CY - Test topic ${new Date().toJSON()}`;

    createTopicWithName(topicName);

    cy.route('DELETE', '/topics/*').as('deleteTopic');

    cy.get('[data-cy="topicsGrid"]')
      .contains(topicName)
      .should('exist');

    cy.get('[data-cy="topicsGrid"] table > tbody > tr:first')
      .should('contain', topicName)
      .within(() => {
        cy.get('[data-cy="topicsGridDeleteButton"]').click();
      });

    cy.wait('@deleteTopic')
      .its('status')
      .should('eq', 200);

    cy.get('[data-cy="topicsGrid"]')
      .contains(topicName)
      .should('not.exist');
  });
});
