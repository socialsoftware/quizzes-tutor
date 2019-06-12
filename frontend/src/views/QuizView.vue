<template>
  <div id="app">
    <survey v-if="survey" :survey="survey"></survey>
    <survey v-if="survey_result" :survey="survey_result"></survey>
    <div ref="surveyAnswer"></div>
    <div ref="surveyResult"></div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import * as SurveyVue from "survey-vue";
import Quiz from "../models/Quiz";
import showdown from "showdown";
import Results from "@/models/Results";
import Question from "@/models/Question";

Vue.component("survey", SurveyVue.Survey);

@Component
export default class QuizView extends Vue {
  survey: any = null;
  survey_result: any = null;
  questions: Question[] = [];
  id: number;

  constructor() {
    super();
    this.questions = Quiz.getQuestions();
    this.id = Quiz.getId();
  }

  getJson(questionsJson: string[]) {
    if (this.questions) {
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
        this.survey_result = new SurveyVue.Model(json);
        this.survey_result.data = result.data;
        this.survey_result.mode = "display";

        let el = this.$refs.surveyAnswer as HTMLElement;
        el.innerHTML = "answer: " + JSON.stringify(result.data);
        Results.getAnswers(result.data, this.id).then(s => {
          let el = this.$refs.surveyResult as HTMLElement;
          el.innerHTML = "result: " + JSON.stringify(s);
        });
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
  max-width: 50%;
  max-height: 50%;
}
</style>
