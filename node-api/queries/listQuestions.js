// Answers
module.exports = function (db) {
  var module = {};

  module.get = function (req, res, next) {
    db.any('select * from questions order by id DESC')
    // using .spread(function(user, event)) is best here, if supported;
    .then(
      function (data) {
        res.render('listQuestions', {
          data: {
            questions: data
          }
        });
      }).catch(error => {
        return next(error);
    });
  };

  return module;
};
