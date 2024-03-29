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
        <v-sheet class="mx-auto" max-width="300">
          <v-slide-group v-model="slideItemPosition" show-arrows center-active>
            <v-slide-item
              v-for="index in +statementQuiz.questions.length"
              :key="index"
            >
              <span
                :questionNumber="statementQuiz.questions.length"
                v-bind:class="[
                  'question-button',
                  statementQuiz.answers[
                    index - 1
                  ].answerDetails.isQuestionAnswered()
                    ? 'answered-question-button'
                    : '',
                  index === questionOrder + 1 ? 'current-question-button' : '',
                ]"
                @click="changeOrder(index - 1)"
              >
                {{ index }}
              </span>
            </v-slide-item>
          </v-slide-group>
        </v-sheet>
      </div>
      <span class="number-of-questions"
        >{{
          statementQuiz.answers.filter((q) =>
            q.answerDetails.isQuestionAnswered()
          ).length
        }}
        / {{ statementQuiz.questions.length }}
      </span>
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
import RemoteServices from '@/services/RemoteServices';
import StatementQuiz from '@/models/statement/StatementQuiz';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';

@Component({
  components: {
    'question-component': QuestionComponent,
  },
})
export default class QuizView extends Vue {
  statementQuiz: StatementQuiz | null = this.$store.getters.getStatementQuiz;
  confirmationDialog: boolean = false;
  confirmed: boolean = false;
  nextConfirmationDialog: boolean = false;
  startTime: Date = new Date();
  questionOrder: number = 0;
  slideItemPosition: number = 1;
  hideTime: boolean = false;
  quizSubmitted: boolean = false;

  async created() {
    if (!this.statementQuiz?.id) {
      await this.$router.push({ name: 'create-quizzes' });
    }
    await this.setCurrentQuestion(this.statementQuiz?.questionOrder);
  }

  async setCurrentQuestion(order: number | undefined) {
    if (!order) order = 0;
    if (
      this.statementQuiz != null &&
      !this.statementQuiz.questions[order].content
    ) {
      let newAnswer = this.statementQuiz.answers[order];
      newAnswer.timeTaken = 0;
      newAnswer.timeToSubmission = this.statementQuiz.timeToSubmission;
      try {
        const question = await RemoteServices.getQuestion(
          this.statementQuiz.id,
          this.statementQuiz.questions[order].questionId,
          newAnswer
        );

        this.statementQuiz.questions[order].content = question.content;
        this.statementQuiz.questions[order].image = question.image;
        this.statementQuiz.questions[order].questionDetails =
          question.questionDetails;

        // ensure that the questions ids are equal to que returned by the server
        this.statementQuiz.questions[order].questionId = question.questionId;
        this.statementQuiz.answers[order].questionId = question.questionId;
      } catch (error) {
        await this.$store.dispatch('error', error);
        await this.$router.push({ name: 'available-quizzes' });
      }
    }

    this.slideItemPosition = order + 1;
    this.questionOrder = order;
  }

  async increaseOrder() {
    if (this.questionOrder + 1 < +this.statementQuiz!.questions.length) {
      try {
        this.calculateTime();
        await this.setCurrentQuestion(this.questionOrder + 1);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
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
      this.calculateTime();

      try {
        if (!!this.statementQuiz && this.statementQuiz.timed) {
          let newAnswer = this.statementQuiz.answers[this.questionOrder];
          newAnswer.timeToSubmission = this.statementQuiz.timeToSubmission;

          await RemoteServices.submitAnswer(this.statementQuiz.id, newAnswer);
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
        await this.$router.push({ name: 'available-quizzes' });
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

      let correctAnswers: StatementCorrectAnswer[] = [];
      if (this.statementQuiz) {
        correctAnswers = await RemoteServices.concludeQuiz(this.statementQuiz);
      } else {
        throw Error('No quiz');
      }

      if (correctAnswers.length !== 0) {
        await this.$store.dispatch('correctAnswers', correctAnswers);
        await this.$router.push({ name: 'quiz-results' });
      } else {
        this.quizSubmitted = true;
        await this.$store.dispatch('statementQuiz', null);
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
