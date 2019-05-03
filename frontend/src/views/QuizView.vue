<template>
  <div id="app">
    <survey v-if="survey" :survey="survey"></survey>
    <div ref="surveyResult"></div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import * as SurveyVue from "survey-vue";
import Quiz from "../models/Quiz";
import showdown from "showdown";

Vue.component("survey", SurveyVue.Survey);

@Component
export default class QuizView extends Vue {
  survey: any = null;
  quiz: Quiz;

  constructor() {
    super();

    this.quiz = new Quiz();
    this.quiz.getJson().then(questions => this.getJson(questions));
  }

  getJson(questionsJson: string[]) {
    if (this.quiz) {
      let json = {
        title: "As Questionaire",
        showProgressBar: "bottom",
        firstPageIsStarted: false,
        startSurveyText: "Start Quiz",
        pages: questionsJson,
        completedHtml:
          "<h4>You have answered correctly <b>{correctedAnswers}</b> questions from <b>{questionCount}</b>.</h4>"
      };
      this.survey = new SurveyVue.Model(json);
      this.survey.onComplete.add((result: { data: any }) => {
        let el = this.$refs.surveyResult as HTMLElement;
        el.innerHTML = "result: " + JSON.stringify(result.data);
      });

      const converter = new showdown.Converter();
      this.survey.onTextMarkdown.add((survey: any, options: any) => {
        //convert the mardown text to html
        var str = converter.makeHtml(options.text);
        //remove root paragraphs <p></p>
        str = str.substring(3);
        str = str.substring(0, str.length - 4);
        //set html
        options.html = str;
      });
    }
  }
}
</script>

<style>
img {
  max-width: 80%;
}
</style>
