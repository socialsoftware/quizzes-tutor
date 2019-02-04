// Answers
module.exports = function (db) {
  var module = {};

  module.get = function (req, res, next) {
    db.tx(t => {
        // `t` and `this` here are the same;
        // this.ctx = transaction config + state context;
        var quizID = parseInt(req.params.id);

        return t.batch([
            t.one('select * from quizes where id = $1', quizID),
            t.any('select * from questions where id in (select questionid from quizhasquestions where quizid=$1) order by id', quizID),
            // questions has answers for questions in quiz
            t.any('select * from answers where questionid in (select questionid from quizhasquestions where quizid=$1) order by id', quizID),
            // questions has correct answers for questions in quiz
            t.any('select * from questionhascorrectanswers where questionid in (select id from questions where id in (select questionid from quizhasquestions where quizid=$1))', quizID),
            t.any('SELECT * FROM quizes WHERE id < $1 ORDER BY id DESC LIMIT 1', quizID),
            t.any('SELECT * FROM quizes WHERE id > $1 ORDER BY id ASC LIMIT 1', quizID),
            t.any('select * from questionhasimage where questionid in (select id from questions where id in (select questionid from quizhasquestions where quizid=$1))', quizID),
            // questions not in quiz
            t.any('select * from questions where id not in (select questionid from quizhasquestions where quizid=$1) order by id', quizID),
            t.any('select * from answers where questionid not in (select questionid from quizhasquestions where quizid=$1) order by id', quizID),
            t.any('select * from questionhascorrectanswers where questionid not in (select id from questions where id in (select questionid from quizhasquestions where quizid=$1))', quizID),
            t.any('select * from questionhasimage where questionid not in (select id from questions where id in (select questionid from quizhasquestions where quizid=$1))', quizID),
        ]);
    })
    // using .spread(function(user, event)) is best here, if supported;
    .then(
      function (data) {
        var questions = data[1];
        var answers = data[2];
        var correctAnswers = data[3];
        var images = data[6];

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

        // questions not in quiz
        var otherQuestions = data[7];
        var otherAnswers = data[8];
        var otherCorrectAnswers = data[9];
        var otherImages = data[10];

        otherQuestions.forEach(function (question) {
          question.answers = [];
          question.correctAnswers = [];
          question.image = [];

          otherAnswers.forEach(function (answer) {
            if(question.id == answer.questionid) {
              question.answers.push(answer);
            };
          });

          otherImages.forEach(function (image) {
            if(question.id == image.questionid) {
              question.image.push(image);
            };
          });

          otherCorrectAnswers.forEach(function (correctAnswer) {
            if(question.id == correctAnswer.questionid) {
              question.correctAnswers.push(correctAnswer);
            }
          });

        });

        res.render('editQuiz', {
          data: {
            quiz: data[0],
            questions: questions,
            previous: data[4],
            next: data[5],
            otherQuestions: otherQuestions

          }
        });
      }).catch(err => {
        return next(err);
    });
  };

  return module;
};
