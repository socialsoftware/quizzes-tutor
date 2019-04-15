module.exports = function(db) {
  var module = {};

  // Questions
  module.getAll = function (req, res, next) {
    db.any('select * from questions')
      .then(function (data) {
        res.status(200)
          .json({
            status: 'success',
            data: data,
            message: 'Retrieved ALL questions'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.get = function (req, res, next) {
    var questionID = parseInt(req.params.id);
    db.one('select * from questions where id = $1', questionID)
      .then(function (data) {
        res.status(200)
          .json({
            status: 'success',
            data: data,
            message: 'Retrieved ONE question'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.create = function (req, res, next) {
    req.body.answertime = parseInt(req.body.answertime);
    db.none('insert into questions(content, answertime)' +
        'values(${content}, ${answerTime})',
      req.body)
      .then(function () {
        res.status(200)
          .json({
            status: 'success',
            message: 'Inserted one question'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.update = function (req, res, next) {
    db.none('update questions set content=$1, answerTime=$2 where id=$3',
      [req.body.content, parseInt(req.body.answerTime), parseInt(req.params.id)])
      .then(function () {
        res.status(200)
          .json({
            status: 'success',
            message: 'Updated question'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.remove = function (req, res, next) {
    var questionID = parseInt(req.params.id);
    db.result('delete from questions where id = $1', questionID)
      .then(function (result) {
        /* jshint ignore:start */
        res.status(200)
          .json({
            status: 'success',
            message: `Removed ${result.rowCount} question`
          });
        /* jshint ignore:end */
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.previous = function (req, res, next) {
    var questionID = parseInt(req.params.id);
    db.any('SELECT * FROM questions WHERE id < $1 ORDER BY id DESC LIMIT 1', questionID)
    .then(function (data) {
      res.status(200)
        .json({
          status: 'success',
          data: data,
          message: 'Retrieved previous question'
        });
    })
    .catch(function (err) {
      return next(err);
    });
  };

  module.next = function (req, res, next) {
    var questionID = parseInt(req.params.id);
    db.any('SELECT * FROM questions WHERE id > $1 ORDER BY id ASC LIMIT 1', questionID)
    .then(function (data) {
      res.status(200)
        .json({
          status: 'success',
          data: data,
          message: 'Retrieved next question'
        });
    })
    .catch(function (err) {
      return next(err);
    });
  };

  return module;
};
