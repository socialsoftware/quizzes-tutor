<template>
  <div class="quiz-container" v-if="statementManager.correctAnswers.length > 0">
    <div class="question-navigation">
      <div data-cy="navigationButtons" class="navigation-buttons">
        <span
          v-for="index in +statementManager.statementQuiz.questions.length"
          v-bind:class="[
            'question-button',
            index === questionOrder + 1 ? 'current-question-button' : '',
            index === questionOrder + 1 &&
            !statementManager.statementQuiz.answers[index - 1].isAnswerCorrect(
              statementManager.correctAnswers[index - 1]
            )
              ? 'incorrect-current'
              : '',
            !statementManager.statementQuiz.answers[index - 1].isAnswerCorrect(
              statementManager.correctAnswers[index - 1]
            )
              ? 'incorrect'
              : '',
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
        v-if="questionOrder !== 0"
        ><i class="fas fa-chevron-left"
      /></span>
      <span
        class="right-button"
        data-cy="nextQuestionButton"
        @click="increaseOrder"
        v-if="
          questionOrder !== statementManager.statementQuiz.questions.length - 1
        "
        ><i class="fas fa-chevron-right"
      /></span>
    </div>
    <result-component
      v-model="questionOrder"
      :answer="statementManager.statementQuiz.answers[questionOrder]"
      :correctAnswer="statementManager.correctAnswers[questionOrder]"
      :question="statementManager.statementQuiz.questions[questionOrder]"
      :questionNumber="statementManager.statementQuiz.questions.length"
      @increase-order="increaseOrder"
      @decrease-order="decreaseOrder"
    />
    <discussion-component
      :userDiscussion="
        statementManager.statementQuiz.answers[questionOrder].userDiscussion
      "
      :question="statementManager.statementQuiz.questions[questionOrder]"
      v-on:discussionMessage="updateMessage"
      @submit-discussion="submitDiscussion"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import StatementManager from '@/models/statement/StatementManager';
import ResultComponent from '@/views/student/quiz/ResultComponent.vue';
import DiscussionComponent from '@/views/student/discussions/DiscussionComponent.vue';
import Discussion from '@/models/management/Discussion';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {
    'result-component': ResultComponent,
    'discussion-component': DiscussionComponent,
  },
})
export default class ResultsView extends Vue {
  statementManager: StatementManager = StatementManager.getInstance;
  questionOrder: number = 0;
  discussion: Discussion = new Discussion();

  async created() {
    if (this.statementManager.isEmpty()) {
      await this.$router.push({ name: 'create-quiz' });
    } else if (this.statementManager.correctAnswers.length === 0) {
      await this.$store.dispatch('loading');
      setTimeout(() => {
        this.statementManager.concludeQuiz();
      }, 2000);

      await this.$store.dispatch('clearLoading');
    }

    this.updateDiscussion();
  }

  async submitDiscussion() {
    if (this.discussion!.message === '') {
      await this.$store.dispatch('error', 'Discussion must have content');
      return;
    }

    this.discussion!.courseExecutionId = this.$store.getters.getCurrentCourse.courseExecutionId;
    this.discussion!.date = new Date().toISOString();
    this.statementManager.statementQuiz!.answers[
      this.questionOrder
    ].userDiscussion = await RemoteServices.createDiscussion(
      this.discussion!,
      this.statementManager.statementQuiz!.answers[this.questionOrder]
        .questionAnswerId
    );
  }

  increaseOrder(): void {
    if (
      this.questionOrder + 1 <
      +this.statementManager.statementQuiz!.questions.length
    ) {
      this.questionOrder += 1;
    }

    this.updateDiscussion();
  }

  decreaseOrder(): void {
    if (this.questionOrder > 0) {
      this.questionOrder -= 1;
    }

    this.updateDiscussion();
  }

  changeOrder(n: number): void {
    if (n >= 0 && n < +this.statementManager.statementQuiz!.questions.length) {
      this.questionOrder = n;
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
