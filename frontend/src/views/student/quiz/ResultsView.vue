<template>
  <div class="quiz-container" v-if="statementQuiz && correctAnswers.length > 0">
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
      v-model:questionOrder="questionOrder"
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

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import ResultComponent from '@/views/student/quiz/ResultComponent.vue';
import DiscussionComponent from '@/views/student/discussions/DiscussionComponent.vue';
import Discussion from '@/models/management/Discussion';
import RemoteServices from '@/services/RemoteServices';
import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';

const store = useStore();

const statementQuiz = ref<StatementQuiz | null>(store.statementQuiz);
const correctAnswers = ref<StatementCorrectAnswer[]>(store.correctAnswers);
const questionOrder = ref(0);
const slideItemPosition = ref(1);
const discussion = ref<Discussion>(new Discussion());

const updateDiscussion = () => {
  discussion.value = new Discussion();
};

const concludeQuiz = async () => {
  if (statementQuiz.value) {
    correctAnswers.value = await RemoteServices.concludeQuiz(statementQuiz.value);
  } else {
    throw Error('No quiz');
  }
};

onMounted(async () => {
  if (correctAnswers.value.length === 0) {
    store.setLoading();
    setTimeout(() => {
      concludeQuiz();
    }, 2000);
    store.clearLoading();
  }
  updateDiscussion();
});

const submitDiscussion = async () => {
  if (discussion.value.message === '') {
    store.setError('Discussion must have content');
    return;
  }

  discussion.value.courseExecutionId = store.currentCourse?.courseExecutionId as number;
  discussion.value.date = new Date().toISOString();
  
  if (statementQuiz.value) {
    statementQuiz.value.answers[questionOrder.value].userDiscussion =
      await RemoteServices.createDiscussion(
        discussion.value,
        statementQuiz.value.answers[questionOrder.value].questionAnswerId
      );
  }
};

const increaseOrder = (): void => {
  if (statementQuiz.value && questionOrder.value + 1 < statementQuiz.value.questions.length) {
    questionOrder.value += 1;
    slideItemPosition.value += 1;
  }
  updateDiscussion();
};

const decreaseOrder = (): void => {
  if (questionOrder.value > 0) {
    questionOrder.value -= 1;
    slideItemPosition.value -= 1;
  }
  updateDiscussion();
};

const changeOrder = (n: number): void => {
  if (statementQuiz.value && n >= 0 && n < statementQuiz.value.questions.length) {
    questionOrder.value = n;
    slideItemPosition.value = n + 1;
  }
  updateDiscussion();
};

const updateMessage = (discussionMessage: string) => {
  discussion.value.message = discussionMessage;
};
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
