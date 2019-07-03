<template>
  <div id="DIV_1">
    <div id="DIV_2">
      <header id="HEADER_3">
        <span id="SPAN_7"><i class="fas fa-clock"></i> 00:00</span>
        <span class="end-quiz" @click="endQuiz"
          ><i class="fas fa-times"></i>End Quiz</span
        >
      </header>

      <div id="DIV_11">
        <span id="SPAN_13" @click="decreaseOrder"
          ><i class="fas fa-chevron-left"></i
        ></span>
        <div id="DIV_15">
          <div id="DIV_16">
            <span
              v-for="index in +quiz.numberOfQuestions"
              v-bind:class="[
                'question-button',
                index === order + 1 ? 'current-question-button' : ''
              ]"
              :key="index"
              @click="changeOrder(index - 1)"
            >
              {{ index }}
            </span>
          </div>
        </div>
        <span id="SPAN_49" @click="increaseOrder"
          ><i class="fas fa-chevron-right"></i
        ></span>
      </div>
      <question-component
        v-if="currentQuestion && !completed"
        v-model="order"
        :answer="quiz.answers[order]"
        :correctAnswers="quiz.correctAnswers"
        :question="currentQuestion"
        v-on:increase-order="increaseOrder"
        v-on:select-option="changeAnswer"
      ></question-component>

      <result-component
        v-if="completed && quiz.correctAnswers[order]"
        v-model="order"
        :answer="quiz.answers[order]"
        :correctAnswer="quiz.correctAnswers[order]"
        :question="currentQuestion"
        v-on:increase-order="increaseOrder"
      ></result-component>
    </div>
    <div class="results">{{ correctAnswersNumber }}</div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Quiz from "../models/Quiz";
import Question from "@/models/Question";
import Answer from "@/models/Answer";
import QuestionComponent from "@/components/QuestionComponent.vue";
import ResultComponent from "@/components/ResultComponent.vue";

@Component({
  components: {
    "question-component": QuestionComponent,
    "result-component": ResultComponent
  }
})
export default class QuizView extends Vue {
  quiz: Quiz = Quiz.getInstance;
  currentQuestion: Question | null = null;
  order: number = 0;
  correctAnswersNumber: number | null = null;
  completed: boolean = false;

  constructor() {
    super();
  }

  async beforeMount() {
    await this.quiz.getQuestions();
    this.currentQuestion = this.quiz.questions[this.order];
  }

  increaseOrder(): void {
    if (this.order + 1 < +this.quiz.numberOfQuestions) {
      this.order += 1;
    }
    this.currentQuestion = this.quiz.questions[this.order];
  }

  decreaseOrder(): void {
    if (this.order > 0) {
      this.order -= 1;
    }
    this.currentQuestion = this.quiz.questions[this.order];
  }

  changeOrder(n: number): void {
    if (n >= 0 && n < +this.quiz.numberOfQuestions) {
      this.order = n;
    }
    this.currentQuestion = this.quiz.questions[this.order];
  }

  changeAnswer(answer: Answer) {
    this.quiz.answers[this.order] = answer;
  }

  async endQuiz() {
    this.quiz.correctAnswers = await this.quiz.answer();
    this.completed = true;
    this.order = 0;
    this.correctAnswersNumber = this.quiz.correctAnswersNumber;
  }
}
</script>

<style>
#DIV_1 {
  bottom: 0;
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  height: 804px;
  left: 0;
  position: relative;
  right: 0;
  text-decoration: none solid rgb(51, 51, 51);
  text-size-adjust: 100%;
  top: 0;
  touch-action: pan-y;
  column-rule-color: rgb(51, 51, 51);
  perspective-origin: 789px 402px;
  transform-origin: 789px 402px;
  user-select: none;
  caret-color: rgb(51, 51, 51);
  border: 0 none rgb(51, 51, 51);
  font: normal normal 400 normal 16px / 16px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(51, 51, 51) none 0;
  transition: all 0.5s linear 0s;
}

#DIV_2 {
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  height: 804px;
  text-decoration: none solid rgb(51, 51, 51);
  text-size-adjust: 100%;
  column-rule-color: rgb(51, 51, 51);
  perspective-origin: 789px 402px;
  transform-origin: 789px 402px;
  user-select: none;
  caret-color: rgb(51, 51, 51);
  border: 0 none rgb(51, 51, 51);
  font: normal normal 400 normal 16px / 16px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(51, 51, 51) none 0;
}

#HEADER_3 {
  bottom: 0;
  box-sizing: border-box;
  color: rgb(255, 255, 255);
  height: 51px;
  left: 0;
  position: relative;
  right: 0;
  text-align: center;
  text-decoration: none solid rgb(255, 255, 255);
  text-size-adjust: 100%;
  text-transform: uppercase;
  top: 0;
  column-rule-color: rgb(255, 255, 255);
  perspective-origin: 789px 25.5px;
  transform-origin: 789px 25.5px;
  user-select: none;
  caret-color: rgb(255, 255, 255);
  background: rgb(112, 128, 144) none repeat scroll 0 0 / auto padding-box
    border-box;
  border-top: 0 none rgb(255, 255, 255);
  border-right: 0 none rgb(255, 255, 255);
  border-bottom: 1px solid rgb(101, 115, 130);
  border-left: 0 none rgb(255, 255, 255);
  font: normal normal 700 normal 18px / 51px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(255, 255, 255) none 0;
}

#SPAN_7 {
  bottom: 0;
  box-sizing: border-box;
  color: rgb(255, 255, 255);
  left: 0;
  position: relative;
  right: 0;
  text-align: center;
  text-decoration: none solid rgb(255, 255, 255);
  text-size-adjust: 100%;
  text-transform: uppercase;
  top: 0;
  column-rule-color: rgb(255, 255, 255);
  perspective-origin: 0 0;
  transform-origin: 0 0;
  user-select: none;
  caret-color: rgb(255, 255, 255);
  border: 0 none rgb(255, 255, 255);
  font: normal normal 700 normal 18px / 51px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(255, 255, 255) none 0;
}

.end-quiz {
  bottom: 0;
  box-sizing: border-box;
  color: rgb(255, 255, 255);
  cursor: pointer;
  display: block;
  height: 50px;
  min-width: 170px;
  position: absolute;
  right: 0;
  text-align: center;
  text-decoration: none solid rgb(255, 255, 255);
  text-size-adjust: 100%;
  text-transform: uppercase;
  top: 0;
  width: 170px;
  column-rule-color: rgb(255, 255, 255);
  perspective-origin: 85px 25px;
  transform-origin: 85px 25px;
  user-select: none;
  caret-color: rgb(255, 255, 255);
  background: rgb(90, 103, 115) none repeat scroll 0 0 / auto padding-box
    border-box;
  border-top: 0 none rgb(255, 255, 255);
  border-right: 0 none rgb(255, 255, 255);
  border-bottom: 0 none rgb(255, 255, 255);
  border-left: 1px solid rgb(84, 97, 110);
  font: normal normal 700 normal 18px / 51px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(255, 255, 255) none 0;
  padding: 0 8px;
}

#DIV_11 {
  bottom: 0;
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  height: 40px;
  left: 0;
  position: relative;
  right: 0;
  text-decoration: none solid rgb(51, 51, 51);
  text-size-adjust: 100%;
  top: 0;
  vertical-align: middle;
  column-rule-color: rgb(51, 51, 51);
  perspective-origin: 789px 20px;
  transform-origin: 789px 20px;
  user-select: none;
  caret-color: rgb(51, 51, 51);
  background: rgb(249, 249, 249) none repeat scroll 0 0 / auto padding-box
    border-box;
  border: 0 solid rgb(221, 221, 221);
  font: normal normal 400 normal 16px / 16px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(51, 51, 51) none 0;
  padding: 5px 0;
}

#SPAN_13 {
  bottom: 0;
  box-sizing: border-box;
  color: rgb(194, 194, 194);
  cursor: pointer;
  display: block;
  height: 40px;
  left: 0;
  position: absolute;
  right: 1528px;
  text-align: center;
  text-decoration: none solid rgb(194, 194, 194);
  text-size-adjust: 100%;
  top: 0;
  vertical-align: middle;
  width: 50px;
  z-index: 10;
  column-rule-color: rgb(194, 194, 194);
  perspective-origin: 25px 20px;
  transform-origin: 25px 20px;
  user-select: none;
  caret-color: rgb(194, 194, 194);
  background: rgba(0, 0, 0, 0) -webkit-linear-gradient(
      right,
      rgba(245, 245, 245, 0) 0,
      rgba(245, 245, 245, 0.77) 15%,
      rgb(245, 245, 245) 40%
    ) repeat scroll 0 0 / auto padding-box border-box;
  border: 0 none rgb(194, 194, 194);
  font: normal normal 400 normal 26px / 45px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(194, 194, 194) none 0;
  padding: 0 15px 0 10px;
}

#DIV_15 {
  bottom: 0;
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  height: 40px;
  left: 0;
  position: absolute;
  right: 0;
  text-align: center;
  text-decoration: none solid rgb(51, 51, 51);
  text-size-adjust: 100%;
  top: 0;
  touch-action: pan-y;
  vertical-align: middle;
  white-space: nowrap;
  column-rule-color: rgb(51, 51, 51);
  perspective-origin: 789px 20px;
  transform-origin: 789px 20px;
  user-select: none;
  caret-color: rgb(51, 51, 51);
  border: 0 none rgb(51, 51, 51);
  font: normal normal 400 normal 16px / 16px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(51, 51, 51) none 0;
  overflow: hidden;
}

#DIV_16 {
  bottom: 0;
  box-sizing: border-box;
  color: rgb(51, 51, 51);
  height: 40px;
  left: 0;
  position: relative;
  right: 0;
  text-align: center;
  text-decoration: none solid rgb(51, 51, 51);
  text-size-adjust: 100%;
  top: 0;
  white-space: nowrap;
  column-rule-color: rgb(51, 51, 51);
  perspective-origin: 789px 20px;
  user-select: none;
  caret-color: rgb(51, 51, 51);
  border: 0 none rgb(51, 51, 51);
  font: normal normal 400 normal 16px / 16px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(51, 51, 51) none 0;
}

.current-question-button {
  box-sizing: border-box;
  color: rgb(255, 255, 255);
  cursor: pointer;
  display: inline-block;
  height: 40px;
  text-align: center;
  text-decoration: none solid rgb(255, 255, 255);
  text-size-adjust: 100%;
  white-space: nowrap;
  width: 40px;
  column-rule-color: rgb(255, 255, 255);
  perspective-origin: 20px 20px;
  transform-origin: 20px 20px;
  user-select: none;
  caret-color: rgb(255, 255, 255);
  background: rgb(101, 115, 130) none repeat scroll 0 0 / auto padding-box
    border-box;
  border: 0 none rgb(255, 255, 255);
  font: normal normal 700 normal 16px / 32px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(255, 255, 255) none 0;
  overflow: hidden;
  padding: 5px;
}

.question-button {
  box-sizing: border-box;
  color: rgb(169, 169, 169);
  cursor: pointer;
  display: inline-block;
  height: 40px;
  text-align: center;
  text-decoration: none solid rgb(169, 169, 169);
  text-size-adjust: 100%;
  white-space: nowrap;
  width: 40px;
  column-rule-color: rgb(169, 169, 169);
  perspective-origin: 20px 20px;
  transform-origin: 20px 20px;
  user-select: none;
  caret-color: rgb(169, 169, 169);
  border: 0 none rgb(169, 169, 169);
  font: normal normal 700 normal 16px / 32px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(169, 169, 169) none 0;
  overflow: hidden;
  padding: 5px;
}

#SPAN_49 {
  bottom: 0;
  box-sizing: border-box;
  color: rgb(194, 194, 194);
  cursor: pointer;
  display: block;
  height: 40px;
  position: absolute;
  right: 0;
  text-align: center;
  text-decoration: none solid rgb(194, 194, 194);
  text-size-adjust: 100%;
  top: 0;
  vertical-align: middle;
  width: 50px;
  z-index: 10;
  column-rule-color: rgb(194, 194, 194);
  perspective-origin: 25px 20px;
  transform-origin: 25px 20px;
  user-select: none;
  caret-color: rgb(194, 194, 194);
  background: rgba(0, 0, 0, 0) -webkit-linear-gradient(
      left,
      rgba(245, 245, 245, 0) 0,
      rgba(245, 245, 245, 0.77) 15%,
      rgb(245, 245, 245) 40%
    ) repeat scroll 0 0 / auto padding-box border-box;
  border: 0 none rgb(194, 194, 194);
  font: normal normal 400 normal 26px / 45px "Varela Round", Helvetica, Arial,
    sans-serif;
  outline: rgb(194, 194, 194) none 0;
  padding: 0 15px 0 10px;
}
</style>
