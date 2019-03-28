// Answers
module.exports = function (db) {
  var module = {};

  module.get = function (req, res, next) {
    db.tx(t => {
        var quizID = parseInt(req.params.id);

        return t.batch([
          t.any('select * from quizes'),
          t.any('select * from questions')
        ])
    })
    .then(
      function (data) {

        res.render('start', {
          data: {
            quizes: data[0],
            questions: data[1]
          }
        });
      }).catch(err => {
        return next(err);
    });
  };

  return module;
};
