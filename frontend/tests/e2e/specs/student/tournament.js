describe('Student walkthrough', () => {

  beforeEach(() => {
    cy.demoStudentLogin();
    cy.addQuestionTopic();
  });

  afterEach(() => {
    cy.exec(
      'PGPASSWORD=' +
      Cypress.env('PASS') +
      ' psql -d ' +
      Cypress.env('DBNAME') +
      ' -U ' +
      Cypress.env('USER') +
      ' -h localhost -c "DELETE FROM topics_questions WHERE questions_id = 1389;' +
      ' DELETE FROM tournaments_participants;' +
      ' DELETE FROM tournaments;' +
      ' ALTER SEQUENCE tournaments_id_seq RESTART WITH 1;' +
      ' UPDATE tournaments SET id=nextval(\'tournaments_id_seq\');" '
    );
    cy.contains('Demo Course').click();
    cy.logout();
  });

  it('login sees all tournaments', () => {
    cy.seeTournamentsLists('All');
  });

  it('login sees open tournaments', () => {
    cy.seeTournamentsLists('Open');
  });

  it('login sees closed tournaments', () => {
    cy.seeTournamentsLists('Closed');
  });

  it('login sees my tournaments', () => {
    cy.seeTournamentsLists('My');
  });

  it('login creates a tournament', () => {
    cy.seeTournamentsLists('All');
    cy.createTournament('1');
  });

  it('login creates private tournament', () => {
    cy.seeTournamentsLists('All');
    cy.createPrivateTournament('1');
  });

  it('login creates a tournament and joins', () => {
    cy.seeTournamentsLists('All');
    cy.createTournament('1');
    cy.joinTournament('1');
  });

  it('login creates a private tournament and joins', () => {
    cy.seeTournamentsLists('All');
    cy.createPrivateTournament('1');
    cy.joinPrivateTournament('1');
  });

  it('login creates, joins and solves tournament', () => {
    cy.seeTournamentsLists('Open');
    cy.createOpenTournament('1');
    cy.joinTournament('1');
    cy.wait(100);
    cy.solveTournament('1');
  });

  it('login creates, joins and leaves tournament', () => {
    cy.seeTournamentsLists('All');
    cy.createTournament('1');
    cy.joinTournament('1');
    cy.wait(100);
    cy.leaveTournament('1');
  });

  it('login creates and edits tournament', () => {
    cy.seeTournamentsLists('My');
    cy.createTournament('1');
    cy.editTournament('1');
  });

  it('login creates and cancel tournament', () => {
    cy.seeTournamentsLists('My');
    cy.createTournament('1');
    cy.cancelTournament('1');
  });

  it('login creates and remove tournament', () => {
    cy.seeTournamentsLists('My');
    cy.createTournament('1');
    cy.removeTournament('1');
  });
});
