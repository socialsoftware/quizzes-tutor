<template style="height: 100%">
  <div
    tabindex="0"
    class="quiz-container"
    @keydown.right="confirmAnswer"
    @keydown.left="decreaseOrder"
    v-if="!confirmed"
  >
    <header>
      <span class="timer" @click="hideTime = !hideTime" v-if="statementQuiz">
        <i class="fas fa-clock"></i>
        <span v-if="!hideTime">{{
          convertToHHMMSS(statementQuiz.timeToSubmission)
        }}</span>
      </span>
      <span
        data-cy="endQuizButton"
        class="end-quiz"
        @click="confirmationDialog = true"
        ><i class="fas fa-times" />End Quiz</span
      >
    </header>

    <div class="question-navigation">
      <div data-cy="navigationButtons" class="navigation-buttons">
        <span
          :questionNumber="statementQuiz.questions.length"
          v-for="index in +statementQuiz.questions.length"
          v-bind:class="[
            'question-button',
            index === questionOrder + 1 ? 'current-question-button' : '',
          ]"
          :key="index"
          @click="changeOrder(index - 1)"
        >
          {{ index }}
        </span>
      </div>
      <span
        class="left-button"
        @click="decreaseOrder"
        v-if="questionOrder !== 0 && !statementQuiz.oneWay"
        ><i class="fas fa-chevron-left"
      /></span>
      <span
        class="right-button"
        data-cy="nextQuestionButton"
        @click="confirmAnswer"
        v-if="questionOrder !== statementQuiz.questions.length - 1"
        ><i class="fas fa-chevron-right"
      /></span>
    </div>
    <question-component
      v-model="questionOrder"
      v-if="statementQuiz.answers[questionOrder]"
      :answer="statementQuiz.answers[questionOrder]"
      :question="statementQuiz.questions[questionOrder]"
      :questionNumber="statementQuiz.questions.length"
      :backsies="!statementQuiz.oneWay"
      @increase-order="confirmAnswer"
      @question-answer-update="changeAnswer"
      @decrease-order="decreaseOrder"
    />

    <v-dialog v-model="confirmationDialog" width="50%">
      <v-card>
        <v-card-title primary-title class="secondary white--text headline">
          Confirmation
        </v-card-title>

        <v-card-text class="text--black title">
          <br />
          Are you sure you want to finish?
          <br />
          <span v-if="statementQuiz.unansweredQuestions()">
            You still have
            {{ statementQuiz.unansweredQuestions() }}
            unanswered questions!
          </span>
        </v-card-text>

        <v-divider />

        <v-card-actions>
          <v-spacer />
          <v-btn color="secondary" text @click="confirmationDialog = false">
            Cancel
          </v-btn>
          <v-btn
            color="primary"
            text
            data-cy="confirmationButton"
            @click="concludeQuiz"
          >
            I'm sure
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="nextConfirmationDialog" width="50%">
      <v-card>
        <v-card-title primary-title class="secondary white--text headline">
          Confirmation
        </v-card-title>

        <v-card-text class="text--black title">
          <br />
          Are you sure you want to go to the next question?
          <br />
        </v-card-text>

        <v-divider />

        <v-card-actions>
          <v-spacer />
          <v-btn color="secondary" text @click="nextConfirmationDialog = false">
            Cancel
          </v-btn>
          <v-btn
            data-cy="confirmationButton"
            color="primary"
            text
            @click="increaseOrder"
          >
            I'm sure
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>

  <div class="container" v-else-if="quizSubmitted">
    <v-card>
      <v-card-title class="justify-center">
        The quiz was submitted!
      </v-card-title>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import QuestionComponent from '@/views/student/quiz/QuestionComponent.vue';
import StatementManager from '@/models/statement/StatementManager';
import RemoteServices from '@/services/RemoteServices';
import StatementQuiz from '@/models/statement/StatementQuiz';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';

@Component({
  components: {
    'question-component': QuestionComponent,
  },
})
export default class QuizView extends Vue {
  statementManager: StatementManager = StatementManager.getInstance;
  statementQuiz: StatementQuiz | null =
    StatementManager.getInstance.statementQuiz;
  confirmationDialog: boolean = false;
  confirmed: boolean = false;
  nextConfirmationDialog: boolean = false;
  startTime: Date = new Date();
  questionOrder: number = 0;
  hideTime: boolean = false;
  quizSubmitted: boolean = false;

  async created() {
    if (!this.statementQuiz?.id) {
      await this.$router.push({ name: 'create-quiz' });
    }
    await this.setCurrentQuestion(0);
  }

  async setCurrentQuestion(order: number) {
    if (
      this.statementQuiz != null &&
      !this.statementQuiz.questions[order].content
    ) {
      const question = await RemoteServices.getQuestion(
        this.statementQuiz.id,
        this.statementQuiz.questions[order].questionId
      );
      this.statementQuiz.questions[order].content = question.content;
      this.statementQuiz.questions[order].image = question.image;
      this.statementQuiz.questions[order].questionDetails =
        question.questionDetails;
    }

    this.questionOrder = order;
  }

  increaseOrder(): void {
    if (this.questionOrder + 1 < +this.statementQuiz!.questions.length) {
      this.calculateTime();
      this.setCurrentQuestion(this.questionOrder + 1);
    }
    this.nextConfirmationDialog = false;
  }

  decreaseOrder(): void {
    if (this.questionOrder > 0 && !this.statementQuiz?.oneWay) {
      this.calculateTime();
      this.setCurrentQuestion(this.questionOrder - 1);
    }
  }

  changeOrder(newOrder: number): void {
    if (!this.statementQuiz?.oneWay) {
      if (newOrder >= 0 && newOrder < +this.statementQuiz!.questions.length) {
        this.calculateTime();
        this.setCurrentQuestion(newOrder);
      }
    }
  }

  async changeAnswer() {
    if (this.statementQuiz && this.statementQuiz.answers[this.questionOrder]) {
      try {
        this.calculateTime();
        let newAnswer = this.statementQuiz.answers[this.questionOrder];

        if (!!this.statementQuiz && this.statementQuiz.timed) {
          newAnswer.timeToSubmission = this.statementQuiz.timeToSubmission;
          RemoteServices.submitAnswer(this.statementQuiz.id, newAnswer);
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  confirmAnswer() {
    if (
      this.statementQuiz?.oneWay &&
      this.questionOrder + 1 < +this.statementQuiz!.questions.length
    ) {
      this.nextConfirmationDialog = true;
    } else {
      this.increaseOrder();
    }
  }

  @Watch('statementQuiz.timeToSubmission')
  submissionTimerWatcher() {
    if (!!this.statementQuiz && !this.statementQuiz.timeToSubmission) {
      this.concludeQuiz();
    }
  }

  convertToHHMMSS(time: number | undefined | null): string {
    return milisecondsToHHMMSS(time);
  }

  async concludeQuiz() {
    await this.$store.dispatch('loading');
    try {
      this.calculateTime();
      this.confirmed = true;
      await this.statementManager.concludeQuiz();

      if (this.statementManager.correctAnswers.length !== 0) {
        await this.$router.push({ name: 'quiz-results' });
      } else {
        this.quizSubmitted = true;
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  calculateTime() {
    if (this.statementQuiz) {
      this.statementQuiz.answers[this.questionOrder].timeTaken +=
        new Date().getTime() - this.startTime.getTime();
      this.startTime = new Date();
    }
  }
}
</script>
