// Answers
module.exports = function (db) {
  var module = {};

  module.get = function (req, res, next) {
    res.render('createQuestion');
  }

  return module;
};
