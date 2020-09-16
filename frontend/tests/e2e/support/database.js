function dbPasswordCommand(password) {
  if (Cypress.platform === 'win32') {
    return `set PGPASSWORD=${password}&& `;
  } else {
    return `PGPASSWORD=${password} `;
  }
}

function dbCommand(command) {
  return cy.exec(
    dbPasswordCommand(Cypress.env('psql_db_password')) +
      `psql -d ${Cypress.env('psql_db_name')} ` +
      `-U ${Cypress.env('psql_db_username')} ` +
      `-h ${Cypress.env('psql_db_host')} ` +
      `-p ${Cypress.env('psql_db_port')} ` +
      `-c "${command.replace(/\r?\n/g, ' ')}"`
  );
}

Cypress.Commands.add('cleanTestTopics', () => {
  dbCommand(`
        DELETE FROM topics
        WHERE name like 'CY%'
    `);
});


Cypress.Commands.add('cleanTestCourses', () => {
    dbCommand(`
    delete from users_course_executions where course_executions_id in (select id from course_executions where acronym like 'TEST-%');
    delete from course_executions where acronym like 'TEST-%';
    `)
});

Cypress.Commands.add('addQuestionSubmission', (title, submissionStatus, userId) => {
    dbCommand(`WITH quest AS (INSERT INTO questions (title, content, status, course_id, creation_date) VALUES ('${title}', 'Question?', 'SUBMITTED', 1, current_timestamp) RETURNING id)
    INSERT INTO question_submissions (status, question_id, submitter_id, course_execution_id) VALUES ('${submissionStatus}', (SELECT id from quest), ${userId}, 1);`);

    //add options
    for (let content in [0, 1, 2, 3]) {
        let correct = content === '0' ? 't' : 'f';
        dbCommand(
            `WITH quest AS (SELECT id FROM questions WHERE title='${title}' limit 1),
            quest_details as (INSERT INTO question_details (question_type, question_id) VALUES ('multiple_choice', (SELECT id FROM quest)) RETURNING id)
            INSERT INTO options(content, correct, question_details_id, sequence) 
            VALUES ('${content}', '${correct}', (SELECT id FROM quest_details), ${content});`);
    }
  }
);

Cypress.Commands.add('removeQuestionSubmission', (hasReviews=false) => {
    if (hasReviews) {
        dbCommand(`WITH rev AS (DELETE FROM reviews WHERE id IN (SELECT max(id) FROM reviews) RETURNING question_submission_id)
                      , sub AS (DELETE FROM question_submissions WHERE id IN (SELECT * FROM rev) RETURNING question_id) 
                      , opt AS (DELETE FROM options WHERE question_details_id IN (SELECT qd.id FROM sub JOIN question_details qd on qd.question_id = sub.question_id)) 
                      , det AS (DELETE FROM question_details WHERE question_id in (SELECT * FROM sub))
                        DELETE FROM questions WHERE id IN (SELECT * FROM sub);`);
    } else {
        dbCommand(`WITH sub AS (DELETE FROM question_submissions WHERE id IN (SELECT max(id) FROM question_submissions) RETURNING question_id)
                      , opt AS (DELETE FROM options WHERE question_details_id IN (SELECT qd.id FROM sub JOIN question_details qd on qd.question_id = sub.question_id)) 
                      , det AS (DELETE FROM question_details WHERE question_id in (SELECT * FROM sub))
                    DELETE FROM questions WHERE id IN (SELECT * FROM sub);`);
    }
});
