// Answers
module.exports = function (db) {
  var module = {};

  module.getAll = function (req, res, next) {
    db.any('select * from quizes')
      .then(function (data) {
        res.status(200)
          .json({
            status: 'success',
            data: data,
            message: 'Retrieved ALL quizes'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.previous = function (req, res, next) {
    var quizID = parseInt(req.params.id);
    db.any('SELECT * FROM quizes WHERE id < $1 ORDER BY id DESC LIMIT 1', quizID)
    .then(function (data) {
      res.status(200)
        .json({
          status: 'success',
          data: data,
          message: 'Retrieved previous quiz'
        });
    })
    .catch(function (err) {
      return next(err);
    });
  };

  module.next = function (req, res, next) {
    var quizID = parseInt(req.params.id);
    db.any('SELECT * FROM quizes WHERE id > $1 ORDER BY id ASC LIMIT 1', quizID)
    .then(function (data) {
      res.status(200)
        .json({
          status: 'success',
          data: data,
          message: 'Retrieved next quiz'
        });
    })
    .catch(function (err) {
      return next(err);
    });
  };

  module.remove = function (req, res, next) {
    var quizId = parseInt(req.params.id);
    db.result('delete from quizes where id = $1', quizId)
      .then(function (result) {
        /* jshint ignore:start */
        res.status(200)
          .json({
            status: 'success',
            message: `Removed ${result.rowCount} quiz`
          });
        /* jshint ignore:end */
      })
      .catch(function (err) {
        return next(err);
      });
  };

  return module;
};
