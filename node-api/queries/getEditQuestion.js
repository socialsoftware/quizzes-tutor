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
            t.any('select * from QuestionHasCorrectAnswers where questionid = $1', questionID)
        ]);
    })
    // using .spread(function(user, event)) is best here, if supported;
    .then(
      function (data) {
        res.status(200)
          .json({
            status: 'success',
            data: {
              question: data[0],
              answers: data[1],
              correctAnswers: data[2]
            },
            message: 'Retrieved data'
          });
      }).catch(error => {
        return next(error);
    });
  };

  module.getAll = function (req, res, next) {
    db.tx(t => {
        // `t` and `this` here are the same;
        // this.ctx = transaction config + state context;
        var questionID = parseInt(req.params.id);

        return t.batch([
            t.any('select * from questions'),
            t.any('select * from answers'),
            t.any('select * from QuestionHasCorrectAnswers')
        ]);
    })
    // using .spread(function(user, event)) is best here, if supported;
    .then(
      function (data) {
        res.status(200)
          .json({
            status: 'success',
            data: {
              questions: function(questions, answers, correctAnswers) {
                var result = [];

                questions.forEach(function(question) {

                  var newQuestion = {
                    question: question,
                    answers: function(answers) {
                      var result = [];

                      answers.forEach(function(answer) {
                        if(answer.questionid == question.id) {
                          result.push(answer);
                        }
                      });

                      return result;

                    }(answers),

                    correctAnswers: function(correctAnswers) {
                      var result = [];

                      correctAnswers.forEach(function(correctAnswer) {
                          if(correctAnswer.questionid == question.id) {
                            result.push(correctAnswer);
                          }
                      });

                      return result;

                    }(correctAnswers)
                  }
                  result.push(newQuestion);
                });



                return result;
              }(data[0], data[1], data[2])
              /* data[0],
              answers: data[1],
              correctAnswers: data[2] */
            },
            message: 'Retrieved data'
          });
      }).catch(error => {
        return next(error);
    });
  };

  return module;
};
