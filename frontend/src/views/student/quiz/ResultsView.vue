<template>
  <div class="quiz-container" v-if="correctAnswers.length > 0">
    <div class="question-navigation">
      <div
        data-cy="navigationButtons"
        class="navigation-buttons"
        show-arrows
        center-active
      >
        <v-sheet class="mx-auto" max-width="400">
          <v-slide-group v-model="slideItemPosition" show-arrows>
            <v-slide-item
              v-for="index in +statementQuiz.questions.length"
              :key="index"
            >
              <span
                v-bind:class="[
                  'question-button',
                  index === questionOrder + 1 ? 'current-question-button' : '',
                  index === questionOrder + 1 &&
                  !statementQuiz.answers[index - 1].isAnswerCorrect(
                    correctAnswers[index - 1]
                  )
                    ? 'incorrect-current'
                    : '',
                  !statementQuiz.answers[index - 1].isAnswerCorrect(
                    correctAnswers[index - 1]
                  )
                    ? 'incorrect'
                    : '',
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
          statementQuiz.answers.filter((a1) =>
            a1.isAnswerCorrect(
              correctAnswers.filter((a2) => a2.sequence === a1.sequence)[0]
            )
          ).length
        }}
        /
        {{ statementQuiz.questions.length }}
      </span>
      <!--      <span-->
      <!--        class="left-button"-->
      <!--        @click="decreaseOrder"-->
      <!--        v-if="questionOrder !== 0"-->
      <!--        ><i class="fas fa-chevron-left"-->
      <!--      /></span>-->
    </div>
    <result-component
      v-model="questionOrder"
      :answer="statementQuiz.answers[questionOrder]"
      :correctAnswer="correctAnswers[questionOrder]"
      :question="statementQuiz.questions[questionOrder]"
      :questionNumber="statementQuiz.questions.length"
      @increase-order="increaseOrder"
      @decrease-order="decreaseOrder"
    />
    <discussion-component
      :userDiscussion="statementQuiz.answers[questionOrder].userDiscussion"
      :question="statementQuiz.questions[questionOrder]"
      v-on:discussionMessage="updateMessage"
      @submit-discussion="submitDiscussion"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import ResultComponent from '@/views/student/quiz/ResultComponent.vue';
import DiscussionComponent from '@/views/student/discussions/DiscussionComponent.vue';
import Discussion from '@/models/management/Discussion';
import RemoteServices from '@/services/RemoteServices';
import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';

@Component({
  components: {
    'result-component': ResultComponent,
    'discussion-component': DiscussionComponent,
  },
})
export default class ResultsView extends Vue {
  statementQuiz: StatementQuiz | null = this.$store.getters.getStatementQuiz;
  correctAnswers: StatementCorrectAnswer[] =
    this.$store.getters.getCorrectAnswers;
  questionOrder: number = 0;
  slideItemPosition: number = 1;
  discussion: Discussion = new Discussion();

  async created() {
    if (this.correctAnswers.length === 0) {
      await this.$store.dispatch('loading');
      setTimeout(() => {
        this.concludeQuiz();
      }, 2000);

      await this.$store.dispatch('clearLoading');
    }

    this.updateDiscussion();
  }

  async concludeQuiz() {
    if (this.statementQuiz) {
      this.correctAnswers = await RemoteServices.concludeQuiz(
        this.statementQuiz
      );
    } else {
      throw Error('No quiz');
    }
  }

  async submitDiscussion() {
    if (this.discussion!.message === '') {
      await this.$store.dispatch('error', 'Discussion must have content');
      return;
    }

    this.discussion!.courseExecutionId =
      this.$store.getters.getCurrentCourse.courseExecutionId;
    this.discussion!.date = new Date().toISOString();
    this.statementQuiz!.answers[this.questionOrder].userDiscussion =
      await RemoteServices.createDiscussion(
        this.discussion!,
        this.statementQuiz!.answers[this.questionOrder].questionAnswerId
      );
  }

  increaseOrder(): void {
    if (this.questionOrder + 1 < +this.statementQuiz!.questions.length) {
      this.questionOrder += 1;
      this.slideItemPosition += 1;
    }

    this.updateDiscussion();
  }

  decreaseOrder(): void {
    if (this.questionOrder > 0) {
      this.questionOrder -= 1;
      this.slideItemPosition -= 1;
    }

    this.updateDiscussion();
  }

  changeOrder(n: number): void {
    if (n >= 0 && n < +this.statementQuiz!.questions.length) {
      this.questionOrder = n;
      this.slideItemPosition = n + 1;
    }

    this.updateDiscussion();
  }

  updateDiscussion() {
    this.discussion = new Discussion();
  }

  updateMessage(discussionMessage: string) {
    this.discussion!.message = discussionMessage;
  }
}
</script>

<style lang="scss" scoped>
.incorrect {
  color: #cf2323 !important;
}

.incorrect-current {
  background-color: #cf2323 !important;
  color: #fff !important;
}
</style>
