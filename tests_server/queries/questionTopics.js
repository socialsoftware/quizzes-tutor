// QuestionTopics
module.exports = function (db) {
  var module = {};

  // Question has Topics
  module.getAll = function (req, res, next) {
    db.any('select * from questionhastopics')
      .then(function (data) {
        res.status(200)
          .json({
            status: 'success',
            data: data,
            message: 'Retrieved ALL question topics'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.get = function (req, res, next) {
    var questiontopicID = parseInt(req.params.id);
    db.one('select * from questionhastopics where id = $1', questiontopicID)
      .then(function (data) {
        res.status(200)
          .json({
            status: 'success',
            data: data,
            message: 'Retrieved ONE question topic'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.create = function (req, res, next) {
    req.body.questionid = parseInt(req.body.questionid);
    req.body.topicid = parseInt(req.body.topicid);
    db.none('insert into questionhastopics(questionid, topicid)' +
        'values(${questionid}, ${topicid})',
      req.body)
      .then(function () {
        res.status(200)
          .json({
            status: 'success',
            message: 'Inserted one question topic'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.update = function (req, res, next) {
    db.none('update questionhastopics set questionid=$1, topicid=$2',
      [parseInt(req.body.questionid), parseInt(req.body.topicid)])
      .then(function () {
        res.status(200)
          .json({
            status: 'success',
            message: 'Updated correct answer'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.remove = function (req, res, next) {
    var questiontopicID = parseInt(req.params.id);
    db.result('delete from questionhastopics where id = $1', questiontopicID)
      .then(function (result) {
        /* jshint ignore:start */
        res.status(200)
          .json({
            status: 'success',
            message: `Removed ${result.rowCount} question topic`
          });
        /* jshint ignore:end */
      })
      .catch(function (err) {
        return next(err);
      });
  };

  // Topics for Question
  module.getForQuestion = function (req, res, next) {
    var questionID = parseInt(req.params.id);
    db.any('select * from QuestionHasTopics where questionid = $1', questionID)
      .then(function (data) {
        res.status(200)
          .json({
            status: 'success',
            data: data,
            message: 'Retrieved ALL topics for question ${req.params.id}'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  return module;
};
