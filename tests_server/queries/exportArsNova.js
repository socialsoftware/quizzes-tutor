// Answers
module.exports = function (db) {
  var module = {};

  module.get = function (req, res, next) {
    db.tx(t => {
        // `t` and `this` here are the same;
        // this.ctx = transaction config + state context;
        var quizID = parseInt(req.params.id);

        return t.batch([
            t.one('select * from quizes where id = $1', quizID),
            t.any('select * from questions where id in (select questionid from quizhasquestions where quizid=$1)', quizID),
            // questions has answers for questions in quiz
            t.any('select * from answers where questionid in (select questionid from quizhasquestions where quizid=$1)', quizID),
            // questions has correct answers for questions in quiz
            t.any('select * from questionhascorrectanswers where questionid in (select id from questions where id in (select questionid from quizhasquestions where quizid=$1))', quizID),
            t.any('select * from questionhasimage where questionid in (select id from questions where id in (select questionid from quizhasquestions where quizid=$1))', quizID),

        ]);
    })
    // using .spread(function(user, event)) is best here, if supported;
    .then(
      function (data) {

        var quiz = data[0];
        var questions = data[1];
        var answers = data[2];
        var correctAnswers = data[3];
        var images = data[4];

        questions.forEach(function (question) {
          question.answers = [];
          question.correctAnswers = [];
          question.image = [];

          answers.forEach(function (answer) {
            if(question.id == answer.questionid) {
              question.answers.push(answer);
            };
          });

          images.forEach(function (image) {
            if(question.id == image.questionid) {
              question.image.push(image);
            };
          });

          correctAnswers.forEach(function (correctAnswer) {
            if(question.id == correctAnswer.questionid) {
              question.correctAnswers.push(correctAnswer);
            }
          });

        });

        var result = module.initResult(quiz.title);

        result.questionList = module.getQuestions(questions, quiz.title);

        res.status(200)
          .json({
            result
          });

      }).catch(err => {
        return next(err);
    });
  };

  module.initResult = function (title) {
    var result = {
      // Title ? Topics
      "hashtag": title,
      "isFirstStart": false,
      "questionList": [
        ],
      "configuration": {
        // Title ? Topics
        "hashtag": title,
        "music": {
          // Title ? Topics
          "hashtag": title,
          "isUsingGlobalVolume": false,
          "lobbyEnabled": false,
          "lobbyTitle": "Song2",
          "lobbyVolume": 50,
          "countdownRunningEnabled": false,
          "countdownRunningTitle": "Song1",
          "countdownRunningVolume": 80,
          "countdownEndEnabled": true,
          "countdownEndTitle": "Song1",
          "countdownEndVolume": 100
        },
        "nicks": {
          // Title ? Topics
          "hashtag": title,
          "selectedValues": [],
          "blockIllegal": true,
          "restrictToCASLogin": false
        },
        "theme": "theme-Material",
        "readingConfirmationEnabled": false,
        "showResponseProgress": false,
        "confidenceSliderEnabled": true
      },
      "type": "DefaultQuestionGroup"
    };

    return result;
  }

  module.getQuestions = function (questions, title) {
    var result = [];
    var questionIndex = 0;

    questions.forEach(function (question) {
      result.push(module.getQuestion(question, questionIndex, title));
      questionIndex += 1;
    });

    return result;
  }

  module.getQuestion = function (question, questionIndex, title) {

    var result = {
      // Title? Topics?
      "hashtag": title,
      "isFirstStart": false,
      "questionText": module.getQuestionText(question),
      // this should be gotten from DB
      "timer": 40,
      // Maybe use index to calculate (the example didnt seem to care)
      "startTime": 0,
      "questionIndex": questionIndex,
      "displayAnswerText": false,
      "answerOptionList": [
        ],
      "showOneAnswerPerRow": false,
      // This might be variable and need to be calculated
      "type": "MultipleChoiceQuestion"
    };

    result.answerOptionList = module.getAnswers(question.correctAnswers, questionIndex, question.answers, title);

    return result;
  }

  module.getQuestionText = function (question) {
    var result = "";

    console.log(JSON.stringify(question) + "\n")

    if(question.image.length > 0) {
      result  = "![" + "" /* this seems to be the legend */ + "](http://impressquizeditor.duckdns.org" + question.image[0].url + ")\n\n" + question.content;
    } else {
      result = question.content;
    }

    return result;
  }

  module.getAnswers = function (correctAnswers, questionIndex, answers, title) {
    var result = [];
    var answerIndex = 0;

    answers.forEach(function (answer) {

      var resultAnswer = module.getAnswer(questionIndex, answer, answerIndex, title);

      correctAnswers.forEach(function (correctAnswer) {
        if(correctAnswer.answerid == answer.id) {
          resultAnswer.isCorrect = true;
        }
      });

      result.push(resultAnswer);
      answerIndex += 1;
    });

    return result;
  }

  module.getAnswer = function (questionIndex, answer, answerIndex, title) {

    var result = {
      "hashtag": title,
      "questionIndex": questionIndex,
      "answerText": answer.content,
      "answerOptionNumber": answerIndex,
      "isCorrect": false,
      "type": "DefaultAnswerOption"
    }

    return result;
  }

  return module;
};
