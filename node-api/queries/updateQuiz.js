// Answers
module.exports = function (db) {
  var module = {};

  module.update = function (req, res, next) {
    db.tx(t => {

        // Query for updating the Question content
        var queries = [
            t.none('update quizes set title=$2 where id = $1',
              [req.body.quiz.id, req.body.quiz.title]),
        ];

        // Query for deleted answers
        req.body.deletedQuestions.forEach(function(questionId) {

          queries.push(t.none('delete from quizhasquestions where quizid = $1 and questionid = $2',
            [req.body.quiz.id, questionId]));

          queries.push(t.none('delete from questions where id = $1',
            [questionId]));

            console.log("delete question: " + questionId)

        });

      return t.batch(queries);
    })
    // using .spread(function(user, event)) is best here, if supported;
    .then(
      function (data) {
        res.status(200)
          .json({
            status: 'success',
            message: 'Updated quiz'
          });
      }).catch(err => {
        //debug print
        console.log(err);

        return next(err);
    });
  };

  return module;
};
