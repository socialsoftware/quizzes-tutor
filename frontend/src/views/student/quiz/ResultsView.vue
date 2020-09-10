<template>
  <div class="quiz-container" v-if="statementManager.correctAnswers.length > 0">
    <div class="question-navigation">
      <div class="navigation-buttons">
        <span
          v-for="index in +statementManager.statementQuiz.questions.length"
          v-bind:class="[
            'question-button',
            index === questionOrder + 1 ? 'current-question-button' : '',
            index === questionOrder + 1 &&
            statementManager.correctAnswers[index - 1].correctOptionId !==
              statementManager.statementQuiz.answers[index - 1].optionId
              ? 'incorrect-current'
              : '',
            statementManager.correctAnswers[index - 1].correctOptionId !==
            statementManager.statementQuiz.answers[index - 1].optionId
              ? 'incorrect'
              : ''
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
      :has-discussion="
        statementManager.statementQuiz.questions[questionOrder]
          .hasUserDiscussion
      "
      :answered="
        statementManager.statementQuiz.answers[questionOrder].optionId != null
      "
      :discussions="
        statementManager.statementQuiz.questions[questionOrder].discussions
      "
      :question="
        statementManager.statementQuiz.questions[questionOrder].question
      "
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
import Question from '@/models/management/Question';
import Store from '@/store';

@Component({
  components: {
    'result-component': ResultComponent,
    'discussion-component': DiscussionComponent
  }
})
export default class ResultsView extends Vue {
  statementManager: StatementManager = StatementManager.getInstance;
  questionOrder: number = 0;
  discussion!: Discussion;

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

    try {
      const question = this.statementManager.statementQuiz!.questions[
        this.questionOrder
      ].question;
      this.discussion!.question = question;
      this.discussion!.questionId = question.id!;
      this.discussion!.userId = this.$store.getters.getUser.id;
      this.discussion!.userName = this.$store.getters.getUser.username;
      this.discussion!.courseExecutionId = this.$store.getters.getCurrentCourse.courseExecutionId;

      const result = await RemoteServices.createDiscussion(this.discussion!);
      this.statementManager.statementQuiz!.questions[
        this.questionOrder
      ].discussions!.push(result);
      this.statementManager.statementQuiz!.questions[
        this.questionOrder
      ].hasUserDiscussion = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }

    this.updateDiscussion();
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
    if (
      !this.statementManager.statementQuiz!.questions[this.questionOrder]
        .hasUserDiscussion
    ) {
      this.discussion = new Discussion();
    }
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
