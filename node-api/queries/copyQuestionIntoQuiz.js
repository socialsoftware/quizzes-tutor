// Answers
module.exports = function (db) {
  var module = {};

  module.post = function (req, res, next) {
    db.tx(t => {
        // `t` and `this` here are the same;
        // this.ctx = transaction config + state context;
        var questionID = parseInt(req.body.questionid);

        return t.batch([
            t.one('select * from questions where id = $1', questionID),
            t.any('select * from answers where questionid = $1', questionID),
            t.any('select * from QuestionHasCorrectAnswers where questionid = $1', questionID),
            t.any('select * from QuestionHasImage where questionid = $1', questionID)
        ]).then(
          function (data) {
            var question = data[0];
            var answers = data[1];
            var correctAnswers = data[2];

            answers.forEach(function (answer) {
              answer.correct = false;

              correctAnswers.forEach(function (correctAnswer) {
                console.log("answer - " + answer.id + " correct - " + correctAnswer.answerid)

                if(answer.id == correctAnswer.answerid) {
                  answer.correct = true;
                  return;
                }
              })
            });

            var image = data[3];
            // if (image.length > 0) {
            //   question.image = [image[0].url];
            // } else {
            //   question.image = []
            // }

            console.log("question - " + JSON.stringify(question));
            console.log("answers - " + JSON.stringify(answers) + "\n");

            var queries = [t.one('insert into questions (content, answertime) values ($1, $2) returning id',
              [question.content, question.answertime])];

            return t.batch(queries).then( function (question) {
              queries = [];

              // Query for new Answers
              answers.forEach(function(answer) {

                queries.push(t.one('insert into answers (questionid, content)' +
                  ' values ($1, $2) RETURNING id',
                  [question[0].id, answer.content]));

              });

              return t.batch(queries).then(insertedAnswers => {
                console.log("2")
                queries = [];

                // Query for new Answers correct
                for(var i = 0 ; i < insertedAnswers.length; i++) {
                  if(answers[i].correct == true) {
                    queries.push(
                      t.none('insert into questionhascorrectanswers (questionid, answerid)' +
                          'values($1, $2)',
                        [question[0].id, insertedAnswers[i].id]))
                  }
                }

                var quizid = parseInt(req.body.quizid);

                queries.push(t.none('INSERT INTO QuizHasQuestions (QuizID, QuestionID) VALUES ($1, $2)',
                  [quizid, question[0].id]));

                if(image.length > 0) {
                  queries.push(t.none('INSERT INTO QuestionHasImage (QuestionID, url) VALUES ($1, $2)',
                    [question[0].id, image[0].url]));
                }

                return t.batch(queries);
              });
            });
        })
    })
    // using .spread(function(user, event)) is best here, if supported;
    .then(
      function (data) {
        res.status(200)
        .json({
          status: 'success',
          message: 'Created question'
        });
      }).catch(err => {
        return next(err);
    });
  };

  return module;
};
