// Answers
module.exports = function (db) {
  var module = {};

  module.get = function (req, res, next) {
    db.tx(t => {
        // `t` and `this` here are the same;
        // this.ctx = transaction config + state context;
        var quizID = parseInt(req.params.quizid);
        var questionID = parseInt(req.params.questionid);

        return t.batch([
            t.one('select * from quizes where id = $1', quizID),
            t.one('select * from questions where id = $2 and id in (select questionid from quizhasquestions where quizid=$1)', [quizID, questionID]),
            t.any('select * from answers where questionid = $1 order by id', questionID),
            t.any('select * from QuestionHasCorrectAnswers where questionid = $2 and questionid in (select questionid from quizhasquestions where quizid=$1)', [quizID, questionID]),
            t.any('select * from questions where id < $2 and id in (select questionid from quizhasquestions where quizid=$1) ORDER BY id DESC LIMIT 1', [quizID, questionID]),
            t.any('select * from questions where id > $2 and id in (select questionid from quizhasquestions where quizid=$1) ORDER BY id ASC LIMIT 1;', [quizID, questionID]),
            t.any('select * from QuestionHasImage where questionid = $2 and questionid in (select questionid from quizhasquestions where quizid=$1)', [quizID, questionID])
        ]);
    })
    // using .spread(function(user, event)) is best here, if supported;
    .then(
      function (data) {
          res.render('editQuizQuestion', {
            data: {
              quiz: data[0],
              question: data[1],
              answers: data[2],
              correctAnswers: data[3],
              previous: data[4],
              next: data[5],
              image: data[6]
            }
        });
      }).catch(err => {
        return next(err);
    });
  };

  return module;
};
