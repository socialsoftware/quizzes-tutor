// CorrectAnswers
module.exports = function(db) {
  var module = {};

  // Question has Correct Answers
  module.getAll = function (req, res, next) {
    db.any('select * from questionhascorrectanswers')
      .then(function (data) {
        res.status(200)
          .json({
            status: 'success',
            data: data,
            message: 'Retrieved ALL correct answers'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.get = function (req, res, next) {
    var correctanswerID = parseInt(req.params.id);
    db.one('select * from questionhascorrectanswers where id = $1', correctanswerID)
      .then(function (data) {
        res.status(200)
          .json({
            status: 'success',
            data: data,
            message: 'Retrieved ONE correct answer'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.create = function (req, res, next) {
    req.body.questionid = parseInt(req.body.questionid);
    req.body.answerid = parseInt(req.body.answerid);
    db.none('insert into questionhascorrectanswers(questionid, answerid)' +
        'values(${questionid}, ${answerid})',
      req.body)
      .then(function () {
        res.status(200)
          .json({
            status: 'success',
            message: 'Inserted one correct answer'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.update = function (req, res, next) {
    db.none('update questionhascorrectanswers set questionid=$1, answerid=$2',
      [parseInt(req.body.questionid), parseInt(req.body.answerid)])
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
    var correctanswerID = parseInt(req.params.id);
    db.result('delete from questionhascorrectanswers where id = $1', correctanswerID)
      .then(function (result) {
        /* jshint ignore:start */
        res.status(200)
          .json({
            status: 'success',
            message: `Removed ${result.rowCount} correct answer`
          });
        /* jshint ignore:end */
      })
      .catch(function (err) {
        return next(err);
      });
  };

  // Correct Answers for Question
  module.getForQuestion = function (req, res, next) {
    var questionID = parseInt(req.params.id);
    db.any('select * from QuestionHasCorrectAnswers where questionid = $1', questionID)
      .then(function (data) {
        res.status(200)
          .json({
            status: 'success',
            data: data,
            message: 'Retrieved ALL correct answers for question ${req.params.id}'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  return module;
}
