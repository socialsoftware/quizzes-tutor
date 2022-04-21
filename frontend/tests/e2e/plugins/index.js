const { Client } = require('pg');

async function queryDB(query, credentials) {
  const client = new Client(credentials);
  await client.connect();
  const result = await client.query(query);
  await client.end();

  return result.rows;
}

module.exports = (on, config) => {
  on('task', {
    queryDatabase({ query, credentials }) {
      return queryDB(query, credentials);
    },
  });

  return Object.assign({}, config, {
    fixturesFolder: 'tests/e2e/fixtures',
    integrationFolder: 'tests/e2e/specs',
    screenshotsFolder: 'tests/e2e/screenshots',
    videosFolder: 'tests/e2e/videos',
    supportFile: 'tests/e2e/support/index.js',
  });
};
