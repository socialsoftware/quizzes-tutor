var promise = require('bluebird');
var fs = require("fs");

// Get content from DB file
var contents = fs.readFileSync("config/db.json");
// Define to JSON type
var dbConfig = JSON.parse(contents);

var options = {
  // Initialization Options
  promiseLib: promise
};

var pgp = require('pg-promise')(options);
var db = pgp(dbConfig);

module.exports = {
  db: db
};
