// Answers
module.exports = function (db) {
  var module = {};

  module.post = function (req, res, next) {
    db.tx(t => {

        var quizid = req.params.quizid;

        // Query for updating the Question content
        var queries = [
            t.one('insert into questions (content, answertime) values ($1, $2) returning id',
              [req.body.question.content, parseInt(req.body.question.answerTime)])
            // t.none('update questionhasimage set url=$1 where questionid=$2', [req.body.question.imgSrc, parseInt(req.params.id)])
        ];

        return t.batch(queries).then( function (question) {
          queries = [];

          // Query for new Answers
          req.body.newAnswers.forEach(function(answer) {

            queries.push(t.one('insert into answers (questionid, content)' +
              ' values ($1, $2) RETURNING id',
              [question[0].id, answer.content]));

          });

          queries.push(t.none('INSERT INTO QuizHasQuestions (QuizID, QuestionID) VALUES ($1, $2)',
            [quizid, question[0].id]));

          return t.batch(queries).then(newAnswers => {
            console.log("2")
            queries = [];

            // Query for new Answers correct
            for(var i = 0 ; i < newAnswers.length - 1; i++) {
              if(req.body.newAnswers[i].correct == "true") {
                queries.push(
                  t.none('insert into questionhascorrectanswers (questionid, answerid)' +
                      'values($1, $2)',
                    [question[0].id, newAnswers[i].id]))
              }
            }

            return t.batch(queries);
          });
        });
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
        //debug print
        console.log(err);

        return next(err);
    });
  };

  return module;
};
