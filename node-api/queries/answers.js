// Answers
module.exports = function (db) {
  var module = {};

  // Answers
  module.getAll = function (req, res, next) {
    db.any('select * from answers')
      .then(function (data) {
        res.status(200)
          .json({
            status: 'success',
            data: data,
            message: 'Retrieved ALL answers'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.get = function (req, res, next) {
    var answerID = parseInt(req.params.id);
    db.one('select * from answers where id = $1', answerID)
      .then(function (data) {
        res.status(200)
          .json({
            status: 'success',
            data: data,
            message: 'Retrieved ONE answer'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.create = function (req, res, next) {
    req.body.questionid = parseInt(req.body.questionid);
    db.none('insert into answers(content, questionid)' +
        'values(${content}, ${questionid})',
      req.body)
      .then(function () {
        res.status(200)
          .json({
            status: 'success',
            message: 'Inserted one answer'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.update = function (req, res, next) {
    db.none('update answers set content=$1, questionid=$2 where id=$3',
      [req.body.content, parseInt(req.body.questionid), parseInt(req.params.id)])
      .then(function () {
        res.status(200)
          .json({
            status: 'success',
            message: 'Updated answer'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.remove = function (req, res, next) {
    var answerID = parseInt(req.params.id);
    db.result('delete from answers where id = $1', answerID)
      .then(function (result) {
        /* jshint ignore:start */
        res.status(200)
          .json({
            status: 'success',
            message: `Removed ${result.rowCount} answer`
          });
        /* jshint ignore:end */
      })
      .catch(function (err) {
        return next(err);
      });
  };

  // Answers for Question
  module.getForQuestion = function (req, res, next) {
    var questionID = parseInt(req.params.id);
    db.any('select * from answers where questionid = $1', questionID)
      .then(function (data) {
        res.status(200)
          .json({
            status: 'success',
            data: data,
            message: 'Retrieved ALL answers for question ${req.params.id}'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  return module;
};
