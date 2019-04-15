var quizInterface = (function() {

  var deletedQuestions = [];

  return {

    hello: function(id) {
      alert(id);
    },

    removeOldQuestion: function (questionId) {
      $("#" + questionId).slideUp('slow', function(){
        var id = questionId.replace("question-", "");
        deletedQuestions.push(id);
        $("#" + questionId).remove();
      });
    },

    getQuizData: function () {

      var result = {
        "quiz": this.getUpdatedQuiz($(".quiz")),
        "deletedQuestions": this.getDeletedQuestions()
      }
      return result;
    },

    getUpdatedQuiz: function (quizNode) {
      // TODO: add fetching of imgContent (still requires html/pug implementation) and fetching of question id
      var id = quizNode.attr("id").replace("quiz-", "");

      var quiz = {
        "id": id,
        "title": $("#quizTitle-" + id).val(),
      }

      return quiz;
    },

    // returns a list of answerids to delete
    // deletedAnswers: id
    getDeletedQuestions: function() {
      return deletedQuestions;
    }

  }

})();
