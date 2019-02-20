// Answers
module.exports = function (db) {
  var module = {};

  module.post = function (req, res, next) {
    db.tx(t => {

        // Query for updating the Question content
        var queries = [
            t.one('insert into quizes (title) values ($1) returning id',
              [req.body.quiz.title])
            // t.none('update questionhasimage set url=$1 where questionid=$2', [req.body.question.imgSrc, parseInt(req.params.id)])
        ];

        return t.batch(queries)
      })
    // using .spread(function(user, event)) is best here, if supported;
    .then(
      function (data) {
        res.status(200)
          .json({
            status: 'success',
            message: 'Created question',
            id: data[0].id
          });
          console.log(data[0].id)
      }).catch(err => {
        //debug print
        console.log(err);

        return next(err);
    });
  };

  return module;
};
