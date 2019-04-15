<template>
  <div id="app">
    <survey v-if="survey" :survey="survey"></survey>
    <div ref="surveyResult"></div>
    <latex :content="tex" />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from "vue-property-decorator";
import * as SurveyVue from "survey-vue";
import Quiz from "../models/Quiz";
import VueLaTeX2JS from "latex2vue";

Vue.use(VueLaTeX2JS);
Vue.component("survey", SurveyVue.Survey);

@Component
export default class QuizView extends Vue {
  survey: any = null;
  quiz: Quiz;
  tex = String.raw`Probably the best part of using PSTricks is that you can mix both graphics and mathematics:\begin{center}\begin{pspicture}(0,-3)(8,3)\rput(0,0){$x(t)$}\rput(4,1.5){$f(t)$}\rput(4,-1.5){$g(t)$}\rput(8.2,0){$y(t)$}\rput(1.5,-2){$h(t)$}\psframe(1,-2.5)(7,2.5)\psframe(3,1)(5,2)\psframe(3,-1)(5,-2)\rput(4,0){$X_k = \frac{1}{p} \sum \limits_{n=\langle p\rangle}x(n)e^{-ik\omega_0n}$}\psline[linewidth=1.25 pt]{->}(0.5,0)(1.5,0)\psline[linewidth=1.25 pt]{->}(1.5,1.5)(3,1.5)\psline[linewidth=1.25 pt]{->}(1.5,-1.5)(3,-1.5)\psline[linewidth=1.25 pt]{->}(6.5,1.5)(6.5,0.25)\psline[linewidth=1.25 pt]{->}(6.5,-1.5)(6.5,-0.25)\psline[linewidth=1.25 pt]{->}(6.75,0)(7.75,0)\psline[linewidth=1.25 pt](1.5,-1.5)(1.5,1.5)\psline[linewidth=1.25 pt](5,1.5)(6.5,1.5)\psline[linewidth=1.25 pt](5,-1.5)(6.5,-1.5)\psline[linewidth=1.25 pt](6,-1.5)(6.5,-1.5)\pscircle(6.5,0){0.25}\psline(6.25,0)(6.75,0)\psline(6.5,0.5)(6.5,-0.5)\end{pspicture}\end{center}which can be produced with the following code:\begin{verbatim}\rput(0,0){$x(t)$}\rput(4,1.5){$f(t)$}\rput(4,-1.5){$g(t)$}\rput(8.2,0){$y(t)$}\rput(1.5,-2){$h(t)$}\psframe(1,-2.5)(7,2.5)\psframe(3,1)(5,2)\psframe(3,-1)(5,-2)\rput(4,0){$X_k = \frac{1}{p} \sum \limits_{n=\langle p\rangle}x(n)e^{-ik\omega_0n}$}\psline[linewidth=1.25 pt]{->}(0.5,0)(1.5,0)\psline[linewidth=1.25 pt]{->}(1.5,1.5)(3,1.5)\psline[linewidth=1.25 pt]{->}(1.5,-1.5)(3,-1.5)\psline[linewidth=1.25 pt]{->}(6.5,1.5)(6.5,0.25)\psline[linewidth=1.25 pt]{->}(6.5,-1.5)(6.5,-0.25)\psline[linewidth=1.25 pt]{->}(6.75,0)(7.75,0)\psline[linewidth=1.25 pt](1.5,-1.5)(1.5,1.5)\psline[linewidth=1.25 pt](5,1.5)(6.5,1.5)\psline[linewidth=1.25 pt](5,-1.5)(6.5,-1.5)\psline[linewidth=1.25 pt](6,-1.5)(6.5,-1.5)\pscircle(6.5,0){0.25}\psline(6.25,0)(6.75,0)\psline(6.5,0.5)(6.5,-0.5)\end{verbatim}`;

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
    }
  }
}
</script>
