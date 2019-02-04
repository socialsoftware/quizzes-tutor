// Answers
module.exports = function (db) {
  var module = {};

  module.update = function (req, res, next) {
    db.tx(t => {
        
        // Query for updating the Question content
        var queries = [
            t.none('update questions set content=$1, answerTime=$2 where id=$3',
              [req.body.question.content, parseInt(req.body.question.answerTime), parseInt(req.params.id)]),
            t.none('update questionhasimage set url=$1 where questionid=$2', [req.body.question.imgSrc, parseInt(req.params.id)])
        ];

        // Query for updated answers
        req.body.updatedAnswers.forEach(function(answer) {

          queries.push(t.none('update answers set content=$1 where id=$2',
            [answer.content, parseInt(answer.id)]));

          queries.push(t.none('delete from questionhascorrectanswers where questionid = $1 and answerid = $2',
            [req.params.id, answer.id]))

          if(answer.correct == "true") {
            queries.push(
              t.none('insert into questionhascorrectanswers (questionid, answerid)' +
                  'values($1, $2)',
                [req.params.id, answer.id]))
          }
        });

        // Query for deleted answers
        req.body.deletedAnswers.forEach(function(answerId) {

          queries.push(t.none('delete from answers where id = $1',
            [answerId]));

            console.log("delete answer: " + answerId)

        });

        return t.batch(queries).then( function () {

          queries = [];

          // Query for new Answers
          req.body.newAnswers.forEach(function(answer) {

            queries.push(t.one('insert into answers (questionid, content)' +
              ' values ($1, $2) RETURNING id',
              [parseInt(req.params.id), answer.content]));

          });

          return t.batch(queries).then(newAnswers => {

            queries = [];

            // Query for new Answers correct
            for(var i = 0 ; i < newAnswers.length; i++) {
              if(req.body.newAnswers[i].correct == "true") {
                queries.push(
                  t.none('insert into questionhascorrectanswers (questionid, answerid)' +
                      'values($1, $2)',
                    [req.params.id, newAnswers[i].id]))
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
            message: 'Updated question'
          });
      }).catch(err => {
        //debug print
        console.log(err);

        return next(err);
    });
  };

  return module;
};
