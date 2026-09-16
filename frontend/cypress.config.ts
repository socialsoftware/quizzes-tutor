import { defineConfig } from 'cypress';

const { Client } = require('pg');
const { execFile } = require('child_process');

// Cypress 16 removed `Cypress.env()`. The database credentials are only ever
// needed by these node tasks, so they are read from `config.env` here and
// never cross into the browser process.
function credentialsFrom(config: { env: Record<string, string> }) {
  return {
    user: config.env.psql_db_username,
    host: config.env.psql_db_host,
    database: config.env.psql_db_name,
    password: config.env.psql_db_password,
    port: config.env.psql_db_port,
  };
}

async function queryDB(query: string, credentials: object) {
  const client = new Client(credentials);
  await client.connect();
  const result = await client.query(query);
  await client.end();

  return result.rows;
}

function runPsql(command: string, credentials: Record<string, string>) {
  return new Promise((resolve, reject) => {
    execFile(
      'psql',
      [
        '-d', credentials.database,
        '-U', credentials.user,
        '-h', credentials.host,
        '-p', String(credentials.port),
        '-c', command.replace(/\r?\n/g, ' '),
      ],
      { env: { ...process.env, PGPASSWORD: credentials.password } },
      (error: Error | null, stdout: string, stderr: string) => {
        if (error) reject(new Error(`${error.message}\n${stderr}`));
        else resolve({ code: 0, stdout, stderr });
      }
    );
  });
}

export default defineConfig({
  defaultCommandTimeout: 10000,
  retries: 0,
  // The browser renderer runs out of memory partway through the longer specs
  // and the process dies, which surfaces as a blank page and a timed-out
  // selector. Cypress 16 manages browser memory by default; this keeps fewer
  // test snapshots around on top of that.
  // https://on.cypress.io/renderer-process-crashed
  numTestsKeptInMemory: 5,
  fixturesFolder: 'tests/e2e/fixtures',
  projectId: '6y833w',
  videoCompression: false,
  screenshotsFolder: 'tests/e2e/screenshots',
  videosFolder: 'tests/e2e/videos',
  e2e: {
    setupNodeEvents(on, config) {
      const credentials = credentialsFrom(config);

      on('task', {
        queryDatabase({ query }) {
          return queryDB(query, credentials);
        },
        execSql(command: string) {
          return runPsql(command, credentials);
        },
      });
    },
    baseUrl: 'http://localhost:8081',
    specPattern: 'tests/e2e/specs/**/*.{js,jsx,ts,tsx}',
    supportFile: 'tests/e2e/support/index.js',
  },
});
