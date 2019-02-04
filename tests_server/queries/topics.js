// Topics

module.exports = function (db) {
  var module = {};

  // Topics
  module.getAll = function (req, res, next) {
    db.any('select * from topics')
      .then(function (data) {
        res.status(200)
          .json({
            status: 'success',
            data: data,
            message: 'Retrieved ALL topics'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.get = function (req, res, next) {
    var topicID = parseInt(req.params.id);
    db.one('select * from topics where id = $1', topicID)
      .then(function (data) {
        res.status(200)
          .json({
            status: 'success',
            data: data,
            message: 'Retrieved ONE topic'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.create = function (req, res, next) {
    db.none('insert into topics(name)' +
        'values(${name})',
      req.body)
      .then(function () {
        res.status(200)
          .json({
            status: 'success',
            message: 'Inserted one topic'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.update = function (req, res, next) {
    db.none('update topics set name=$1',
      [req.body.name])
      .then(function () {
        res.status(200)
          .json({
            status: 'success',
            message: 'Updated topic'
          });
      })
      .catch(function (err) {
        return next(err);
      });
  };

  module.remove = function (req, res, next) {
    var topicID = parseInt(req.params.id);
    db.result('delete from topics where id = $1', topicID)
      .then(function (result) {
        /* jshint ignore:start */
        res.status(200)
          .json({
            status: 'success',
            message: `Removed ${result.rowCount} topic`
          });
        /* jshint ignore:end */
      })
      .catch(function (err) {
        return next(err);
      });
  };

  return module;
};
