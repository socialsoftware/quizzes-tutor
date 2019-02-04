var quizInterface = (function() {

  var nextAnswerId = 1;

  var deletedAnswers = [];

  return {

    removeQuestion: function(questionId) {
      $("#" + questionId).slideUp('slow', function(){
        var id = questionId.replace("question-", "");
        // deletedAnswers.push(id);
        $("#" + questionId).remove();
      });
    },

    openAddQuestions: function(dataResult) {
      $("#addQuestionsButtonDiv").slideUp('slow', function() {
        $("#addQuestionsInterface").slideDown('slow', function() {

        });
      });
    },

    closeAddQuestions: function() {
      $("#addQuestionsInterface").slideUp('slow', function() {
        $("#addQuestionsButtonDiv").slideDown('slow', function() {

        });
      });
    },

    removeQuestionsForm: function(newQuestionsId) {
      $("#" + newQuestionsId).empty();
    },

    addQuestion: function() {

    },

    addQuestionsForm: function(newQuestionsId, dataResult) {

      var questionForm = '';

      var questions = "";

      // questions += this.addQuestionForm(dataResult.data[0]);

      dataResult.data.questions.forEach(function(questionData) {
        questions += '<div class="card-body border-bottom">';
        questions += this.addQuestionForm(questionData.question);
        questions += this.addAnswersForm(questionData.question, questionData.answers, questionData.correctAnswers);
        questions += this.addButtonForm(questionData.question);
        questions += '</div>'
      }.bind(this));

      // alert(questions);

      questionForm += questions;

      //questionForm += '</div>';

      //var newQuestionIdText = "newAnswer-" + newQuestionsId;

      $("#" + newQuestionsId).append(questionForm);

      // this needs to be done here
      dataResult.data.questions.forEach(function(questionData) {
        $("#toggle-answers-question-" + questionData.question.id).click(function() {
          $("#question-" + questionData.question.id + "-answers").slideToggle("slow", function() {})});
      }.bind(this));
    },

    addQuestionForm: function(question) {
      var questionForm =  '<div>' +
        '<div class="form-group" id="question-' + question.id + '">' +
          '<label class="row">' +
            '<div class="col-md-10 col-sm-7 text-secondary font-weight-bold">Question ' + question.id + '</div>' +
            '<div class="col-md-2 col-sm-5 text-right">' +
              '<button class="btn btn-sm btn-outline-secondary" type="button" id="toggle-answers-question-' + question.id + '"><i class="fa fa-caret-down"></i></button>' +
            '</div>' +
          '</label>' +
          '<div class="row">' +
            '<div class="col-12">' +
              '<textarea disabled class="form-control-plaintext" id="questionText-x" rows="3" >' + question.content + '</textarea>' +
            '</div>' +
          '</div>' +
        '</div>' +
       '</div>';

      return questionForm;
    },

    addAnswersForm: function(question, answers, correctAnswers) {
      var result = '<div id="question-' + question.id + '-answers" style="display: none;">';

      answers.forEach(function (answer) {
        result += this.addAnswerForm(question, answer, correctAnswers);
      }.bind(this));

      result += '</div>';

      return result;
    },

    addAnswerForm: function(question, answer, correctAnswers) {
      var correctForm = '';

      var correct = false;

      for(var i = 0; i < correctAnswers.length; i++) {
        if(answer.id == correctAnswers[i].answerid) {
          correct = true;
          break;
        }
      }

      if(correct == true) {

        correctForm = '<div class="col-md-2 col-sm-5"><i class="fa fa-check text-success float-right"></i></div>';

      } else {

        correctForm = '<div class="col-md-2 col-sm-5"><i class="fa fa-times text-danger float-right"></i></div>';

      }


      var answerForm =  '' +
      '<div class="form-group" id="question-answer-' + question.id + '">' +
        '<label class="row">' +
          '<div class="col-md-10 col-sm-7 text-secondary">Answer ' + answer.id + '</div>' +
          correctForm +
        '</label>' +
        '<div class="row">' +
          '<div class="col-12">' +
            '<textarea disabled class="form-control-plaintext" id="answerText-x" rows="3" >' + answer.content + '</textarea>' +
          '</div>' +
        '</div>' +
      '</div>';

      return answerForm;
    },

    addButtonForm: function(question) {
      var buttonForm = '<div class="row col-12">' +
        '<button class="btn btn-sm btn-outline-secondary" type="button" id="add-question-' + question.id + '">Add Question ' + question.id + '</button>' +
      '</div>';

      return buttonForm;
    },

    editQuestion: function() {

    }

  }

})();
