var questionInterface = (function() {

  var nextAnswerId = 1;

  var deletedAnswers = [];

  return {

    addAnswer : function (newAnswersId) {
      var newAnswerId = nextAnswerId;
      var newAnswerIdText = "newAnswer-" + newAnswerId;
      var newAnswerOptionId = "newAnswerText-" + newAnswerId;
      var newAnswerHeader = "New Answer " + newAnswerId
      var newAnswerRemoveButton = "newAnswerRemove-" + newAnswerId;
      var correctNewAnswerId = "correctNewAnswer-" + newAnswerId;
      var newAnswerIdTrue = "correctNewAnswerTrue-" + newAnswerId;
      var newAnswerIdFalse = "correctNewAnswerFalse-"  + newAnswerId;

      var questionForm = ''
      + '<div class="card-body newAnswer" id="' + newAnswerIdText + '" style="display: none;" >'
        + '<div class="form-group">'
          + '<label class="row" for="' + newAnswerOptionId + '" >'
            + '<div class="col-md-10 col-sm-7">' + newAnswerHeader + '</div>'
            + '<div class="col-md-2 col-sm-5 text-right">'
              + '<div id="' + correctNewAnswerId + '" class="btn-group btn-group-toggle" data-toggle="buttons">'
                + '<label class="btn btn-outline-success btn-sm">'
                  + '<input id="' + newAnswerIdTrue + '" type="radio" name="options" autocomplete="off" checked="" value="true">'
                  + '<i class="fa fa-check"></i>'
                + '</label>'
                + '<label class="btn btn-outline-danger btn-sm active">'
                  + '<input id="' + newAnswerIdFalse + '" type="radio" name="options" autocomplete="off" checked="checked" value="false">'
                  + '<i class="fa fa-times"></i>'
                + '</label>'
              + '</div>'
            + '</div>'
          + '</label>'
          + '<div class="form-group">'
            + '<textarea class="form-control" id="' + newAnswerOptionId + '" x="" rows="3"></textarea>'
          + '</div>'
          + '<div class="row">'
            + '<div class="col-12">'
              + '<div class="form-group float-right">'
                + '<button class="btn" type="button" id="' + newAnswerRemoveButton + '">Remove Answer</button>'
              + '</div>'
            + '</div>'
          + '</div>'
        + '</div>'
      + '</div>'

      nextAnswerId += 1;

      $("#" + newAnswersId).append(questionForm);

      $("#" + newAnswerIdText).slideDown("slow", function() {
        // Animation complete.

        //remove button click code
        $("#" + newAnswerRemoveButton).click(questionInterface.removeNewAnswer.bind(null, newAnswerIdText));

      });
    },

    removeOldAnswer: function (answerId) {
      $("#" + answerId).slideUp('slow', function(){
        var id = answerId.replace("answer-", "");
        deletedAnswers.push(id);
        $("#" + answerId).remove();
      });
    },

    removeNewAnswer: function (answerId) {
      $("#" + answerId).slideUp('slow', function(){
        var id = answerId.replace("newAnswer-", "");

        $("#" + answerId).remove();
      });
    },

    // TODO
    // Ideally, these should be bindable to a root node so that i just look
    // for content under that root node
    // This allows me to reuse these functions for the quiz edit view
    // find out how to filter nodes by id from a root node (think i used this before)

    getQuestionData: function () {

      var result = {
        "question": this.getUpdatedQuestion($(".question")),
        "updatedAnswers": this.getUpdatedAnswers(".question"),
        "deletedAnswers": this.getDeletedAnswers(),
        "newAnswers": this.getNewAnswers(".question")

      }

      return result;
    },

    // returns question id, question content and question image address
    getUpdatedQuestion: function (questionNode) {
      // TODO: add fetching of imgContent (still requires html/pug implementation) and fetching of question id
      var id = questionNode.attr("id").replace("question-", "");

      var question = {
        "id": id,
        "content": $("#questionText-" + id).val(),
        "answerTime": "15",
        "imgSrc": $("#question-img-src").val()
      }

      return question;
    },

    // returns a list of answers to update
    // updatedAnswer: id, content, correct
    getUpdatedAnswers: function(questionNode) {
      var updatedAnswers = [];

      //TODO: this needs changes in order for it to be generalizable to a forms that contain multiple questions
      var answerFormsId = this.getUpdatedAnswersIds(questionNode);

      answerFormsId.forEach(function (answerId) {
        var result = this.getUpdatedAnswer(answerId);
        updatedAnswers.push(result);
      }.bind(this));


      return updatedAnswers;
    },

    getUpdatedAnswersIds: function(questionNode) {
      var result = [];

      $(questionNode + " .answer").each(function() {
        result.push($(this).attr('id'))
      });

      return result;
    },

    // return an answer to update
    // updatedAnswer: id, content, correct
    getUpdatedAnswer: function(answerId) {
      // TODO: add fetching of correct (though correctAnswer could probably be sorted differently)
      var updatedAnswer = {
        // $("#" + formId +".answer").attr('id')
        "id": answerId.replace("answer-", ""),
        "content": $("#answerText-" + answerId.replace("answer-", "")).val(),
        "correct": this.getCorrectAnswer("correctAnswer-" + answerId.replace("answer-", ""))
      }

      return updatedAnswer;
    },

    // returns a list of answerids to delete
    // deletedAnswers: id
    getDeletedAnswers: function() {
      return deletedAnswers;
    },

    // returns a list of new answers to insert
    // newAnswer: content, correct
    getNewAnswers: function(questionNode) {
      var newAnswers = [];

      var newAnswersIds = this.getNewAnswersIds(questionNode);

      newAnswersIds.forEach(function(answerId) {
        var result = this.getNewAnswer(answerId);
        newAnswers.push(result);
      }.bind(this));

      return newAnswers;
      // get new answers on fields that belong to newAnswer-id div
    },

    getNewAnswersIds: function(questionNode) {
      var result = [];

      $(questionNode + " .newAnswer").each(function() {
        result.push($(this).attr('id'))
      });

      return result;
    },

    getNewAnswer: function(answerId) {
      // TODO: add fetching of correct (though correctAnswer could probably be sorted differently)

      var newAnswer = {
        "content": $("#newAnswerText-" + answerId.replace("newAnswer-", "")).val(),
        "correct": this.getCorrectAnswer("correctNewAnswer-" + answerId.replace("newAnswer-", ""))
      }

      return newAnswer;
    },

    getCorrectAnswer: function(correctAnswerId) {
      var result = $("#" + correctAnswerId + " label.active input").val();
      return result;
    }

  }

})();
