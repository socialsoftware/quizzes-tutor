// Answers
module.exports = function (db) {
  var module = {};

  module.get = function (req, res, next) {
    db.tx(t => {
        // `t` and `this` here are the same;
        // this.ctx = transaction config + state context;
        var quizID = parseInt(req.params.id);

        return t.batch([
            // questions not in quiz
            t.any('select * from questions where id not in (select questionid from quizhasquestions where quizid=$1) order by id DESC', quizID),
            t.any('select * from answers where questionid not in (select questionid from quizhasquestions where quizid=$1) order by questionid DESC', quizID),
            t.any('select * from questionhascorrectanswers where questionid not in (select id from questions where id in (select questionid from quizhasquestions where quizid=$1)) order by questionid DESC', quizID),
            t.any('select * from questionhasimage where questionid not in (select id from questions where id in (select questionid from quizhasquestions where quizid=$1)) order by questionid DESC', quizID)
        ]);
    })
    // using .spread(function(user, event)) is best here, if supported;
    .then(
      function (data) {
        var questions = data[0];
        var answers = data[1];
        var correctAnswers = data[2];
        var images = data[3];

        questions.forEach(function (question) {
          question.answers = [];
          question.correctAnswers = [];
          question.image = [];

          answers.forEach(function (answer) {
            if(question.id == answer.questionid) {
              question.answers.push(answer);
            };
          });

          images.forEach(function (image) {
            if(question.id == image.questionid) {
              question.image.push(image);
            };
          });

          correctAnswers.forEach(function (correctAnswer) {
            if(question.id == correctAnswer.questionid) {
              question.correctAnswers.push(correctAnswer);
            }
          });

        });

        res.render('addQuizQuestion', {
          data: {
            quiz : {id: parseInt(req.params.id)},
            questions: questions
          }
        });
      }).catch(err => {
        return next(err);
    });
  };

  return module;
};
