module.exports = {
  env: {
    mocha: true
  },
  globals: {
    cy: 'readonly',
    Cypress: 'readonly',
    expect: 'readonly',
    assert: 'readonly',
    describe: 'readonly',
    context: 'readonly',
    it: 'readonly',
    before: 'readonly',
    beforeEach: 'readonly',
    after: 'readonly',
    afterEach: 'readonly'
  },
  rules: {
    strict: 'off'
  }
};
