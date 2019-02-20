// Answers
module.exports = function (db) {
  var module = {};

  module.get = function (req, res, next) {
    db.any('select * from quizes order by id DESC')
    // using .spread(function(user, event)) is best here, if supported;
    .then(
      function (data) {
        res.render('listQuizes', {
          data: {
            quizes: data
          }
        });
      }).catch(error => {
        return next(error);
    });
  };

  return module;
};
