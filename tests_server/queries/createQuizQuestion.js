// Answers
module.exports = function (db) {
  var module = {};

  module.get = function (req, res, next) {
    db.tx(t => {
        // `t` and `this` here are the same;
        // this.ctx = transaction config + state context;
        var quizID = parseInt(req.params.quizid);

        return t.batch([
            t.one('select * from quizes where id = $1', quizID)
        ]);
    })
    // using .spread(function(user, event)) is best here, if supported;
    .then(
      function (data) {
          res.render('createQuizQuestion', {
            data: {
              quiz: data[0]
            }
        });
      }).catch(err => {
        return next(err);
    });
  };

  return module;
};
