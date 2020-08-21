function dbPasswordCommand(password){
    if (Cypress.platform === 'win32'){
        return `set PGPASSWORD=${password}&& `
    }
    else{
        return `PGPASSWORD=${password} `
    }
}

function dbCommand(command){
    return cy.exec(
        dbPasswordCommand(Cypress.env('psql_db_password')) +
        `psql -d ${Cypress.env('psql_db_name')} ` +
        `-U ${Cypress.env('psql_db_username')} ` +
        `-h ${Cypress.env('psql_db_host')} ` +
        `-p ${Cypress.env('psql_db_port')} ` +
        `-c "${command.replace(/\r?\n/g, " ")}"`
    );
}

Cypress.Commands.add('addQuestionTopic', () => {
    dbCommand(`
        INSERT INTO topics_questions (topics_id, questions_id) 
        VALUES (82, 1389);
    `)
});

Cypress.Commands.add('cleanTestTopics', () => {
    dbCommand(`
        DELETE FROM topics
        WHERE name like 'CY%'
    `)
});

Cypress.Commands.add('updateTournamentStartTime', () => {
    dbCommand(`
        UPDATE tournaments SET start_time = '2020-07-16 07:57:00';
    `)
});

Cypress.Commands.add('afterEachTournament', () => {
    dbCommand(`
        DELETE FROM topics_questions WHERE questions_id = 1389;
        DELETE FROM tournaments_participants;
        DELETE FROM tournaments; 
        ALTER SEQUENCE tournaments_id_seq RESTART WITH 1;
        UPDATE tournaments SET id=nextval('tournaments_id_seq');
    `)
});