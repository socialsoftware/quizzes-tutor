// Answers
module.exports = function (db) {
  var module = {};

  module.get = function (req, res, next) {
    db.tx(t => {
        // `t` and `this` here are the same;
        // this.ctx = transaction config + state context;
        var questionID = parseInt(req.params.id);

        return t.batch([
            t.one('select * from questions where id = $1', questionID),
            t.any('select * from answers where questionid = $1', questionID),
            t.any('select * from QuestionHasCorrectAnswers where questionid = $1', questionID),
            t.any('SELECT * FROM questions WHERE id < $1 ORDER BY id DESC LIMIT 1', questionID),
            t.any('SELECT * FROM questions WHERE id > $1 ORDER BY id ASC LIMIT 1', questionID),
            t.any('select * from QuestionHasImage where questionid = $1', questionID)
        ]);
    })
    // using .spread(function(user, event)) is best here, if supported;
    .then(
      function (data) {
          res.render('editQuestion', {
            data: {
              question: data[0],
              answers: data[1],
              correctAnswers: data[2],
              previous: data[3],
              next: data[4],
              image: data[5]
            }
        });
      }).catch(err => {
        return next(err);
    });
  };

  return module;
};
