var testInterface = (function() {

  return {

    appendQuestions: function (containerID, questions) {
      questions.forEach(function (question) {
        this.appendQuestionForm(containerID, question)
      }.bind(this));

      var lastQuestionID = "question-" + questions[questions.length - 1].id;
      console.log(lastQuestionID);
      $("#" + lastQuestionID).removeClass("border-bottom");
    },

    appendQuestionForm: function (containerID, question) {
      var questionFormID = "question-" + question.id;
      var questionAnswersID = questionFormID + '-answers';

      var form = "<div class='card-body border-bottom' id='" + questionFormID + "'>" + this.generateQuestionForm(question) +

        '<div id="' + questionAnswersID + '" style="display: none;">' +
          this.generateAnswersForm() +
        '</div>' +
      '</div>';

      $("#" + containerID).append(form);

      // this should only be called if we are generating the addQuestion form (so it shouldnt really be here)
      //this.appendAddAnswerButton(questionAnswersID, question);
    },

    generateQuestionForm: function(question) {
      var questionFormID = "question-" + question.id;
      var questionToggleAnswersID = "toggle-answers-question-" + question.id;
      var questionAnswersID = questionFormID + '-answers';
      var questionTextID = "questionText-" + question.id;

      var form = "" +


        '<div class="form-group">'  +

          '<label class="row">'  +
            '<div class="col-md-10 col-sm-7 text-secondary font-weight-bold">Question ' + question.id + '</div>' +
            '<div class="col-md-2 col-sm-5 text-right">' +
              '<button class="btn btn-sm btn-outline-secondary" type="button" id="' + questionToggleAnswersID + '" data-toggle="button" aria-pressed="false" autocomplete="off">' +
                '<i class="fa fa-caret-down"></i>' +
              '</button>' +
              '<script>' +
                '$("#' + questionToggleAnswersID + '").click(function() {' +
                  '$("#' + questionAnswersID + '").slideToggle("slow", function() {})});' +
              '</script>' +
            '</div>' +
          '</label>' +

          '<div class="row">' +
            '<div class="col-12">' +
              '<textarea disabled="" class="form-control-plaintext" id="' + questionTextID + '" oninput="textareaFit.resize(\'' + questionTextID + '\')"">' + question.content + '</textarea>' +
              '<script>' +
                'textareaFit.resize("' + questionTextID + '");' +
              '</script>' +
            '</div>' +
          '</div>' +
        '</div>';

      return form;
    },

    generateAnswersForm: function() {
      var form = "" +

      '<div class="form-group" id="question-answer-9">' +
        '<label class="row">' +
          '<div class="col-md-10 col-sm-7 text-secondary">Answer 9</div>' +
          '<div class="col-md-2 col-sm-5"><i class="fa fa-check text-success float-right"></i></div>' +
        '</label>' +
        '<div class="row">' +
          '<div class="col-12">' +
            '<textarea disabled="" class="form-control-plaintext" id="answerText-x" rows="3">' +
              'However, functional requirements do not have any impact on the architecture because the systemic qualities of an architecture are non-functional </textarea>' +
          '</div>' +
        '</div>' +
      '</div>' +
      '<div class="form-group" id="question-answer-10">' +
        '<label class="row">' +
          '<div class="col-md-10 col-sm-7 text-secondary">Answer 10</div>' +
          '<div class="col-md-2 col-sm-5"><i class="fa fa-check text-success float-right"></i></div>' +
        '</label>' +
        '<div class="row">' +
          '<div class="col-12">' +
            '<textarea disabled="" class="form-control-plaintext" id="answerText-x" rows="3">The functional requirements have a large impact on the definition of views of the component-and-connector viewtype because each component executes a functionality </textarea>' +
            '</div>' +
          '</div>' +
        '</div>' +
        '<div class="form-group" id="question-answer-11">' +
          '<label class="row">' +
            '<div class="col-md-10 col-sm-7 text-secondary">Answer 11</div>' +
            '<div class="col-md-2 col-sm-5"><i class="fa fa-times text-danger float-right"></i></div>' +
          '</label>' +
          '<div class="row">' +
            '<div class="col-12">' +
              '<textarea disabled="" class="form-control-plaintext" id="answerText-x" rows="3">The functional requirements have a large impact on the definition of views of the module viewtype because they are used to define the high cohesion and low coupling of modules </textarea>' +
            '</div>' +
          '</div>' +
        '</div>' +
        '<div class="form-group" id="question-answer-12">' +
          '<label class="row">' +
            '<div class="col-md-10 col-sm-7 text-secondary">Answer 12</div>' +
            '<div class="col-md-2 col-sm-5"><i class="fa fa-times text-danger float-right"></i></div>' +
          '</label>' +
          '<div class="row">' +
            '<div class="col-12">' +
              '<textarea disabled="" class="form-control-plaintext" id="answerText-x" rows="3">The functional requirements can be considered as constraints on the software architecture design </textarea>' +
            '</div>' +
          '</div>' +
        '</div>' +
      '</div>';


      return form;
    },

    appendAddAnswerButton(containerID, question) {
      var addQuestionID  = "add-question-" + question.id;

      var form = "" +
      '<div class="row col-12">' +
        '<button class="btn btn-sm btn-outline-secondary" type="button" id="' + addQuestionID + '">Add Question ' + question.id + '</button>' +
      '</div>';

      $("#" + containerID).after(form);
    }


  }
})();
