<template style="height: 100%">
  <div
    tabindex="0"
    class="quiz-container"
    @keydown.right="confirmAnswer"
    @keydown.left="decreaseOrder"
    v-if="statementQuiz && !confirmed"
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
      v-model:questionOrder="questionOrder"
      v-if="statementQuiz && statementQuiz.answers[questionOrder]"
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
        <v-card-title class="bg-secondary text-white text-h5">
          Confirmation
        </v-card-title>

        <v-card-text class="text-h6 text-center mt-4 mb-4">
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
        <v-card-title class="bg-secondary text-white text-h5">
          Confirmation
        </v-card-title>

        <v-card-text class="text-h6 text-center mt-4 mb-4">
          Are you sure you want to go to the next question?
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

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useStore } from '@/store';
import { useRouter } from 'vue-router';
import QuestionComponent from '@/views/student/quiz/QuestionComponent.vue';
import RemoteServices from '@/services/RemoteServices';
import StatementQuiz from '@/models/statement/StatementQuiz';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';

const store = useStore();
const router = useRouter();

const statementQuiz = ref<StatementQuiz | null>(store.statementQuiz);
const confirmationDialog = ref(false);
const confirmed = ref(false);
const nextConfirmationDialog = ref(false);
const startTime = ref<Date>(new Date());
const questionOrder = ref(0);
const slideItemPosition = ref(1);
const hideTime = ref(false);
const quizSubmitted = ref(false);

const setCurrentQuestion = async (order?: number) => {
  if (order === undefined) order = 0;
  if (statementQuiz.value != null && !statementQuiz.value.questions[order].content) {
    let newAnswer = statementQuiz.value.answers[order];
    newAnswer.timeTaken = 0;
    newAnswer.timeToSubmission = statementQuiz.value.timeToSubmission;
    try {
      const question = await RemoteServices.getQuestion(
        statementQuiz.value.id,
        statementQuiz.value.questions[order].questionId,
        newAnswer
      );

      statementQuiz.value.questions[order].content = question.content;
      statementQuiz.value.questions[order].image = question.image;
      statementQuiz.value.questions[order].questionDetails = question.questionDetails;

      statementQuiz.value.questions[order].questionId = question.questionId;
      statementQuiz.value.answers[order].questionId = question.questionId;
    } catch (error) {
      store.setError(error as string);
      await router.push({ name: 'available-quizzes' });
    }
  }

  slideItemPosition.value = order + 1;
  questionOrder.value = order;
};

const calculateTime = () => {
  if (statementQuiz.value) {
    statementQuiz.value.answers[questionOrder.value].timeTaken +=
      new Date().getTime() - startTime.value.getTime();
    startTime.value = new Date();
  }
};

onMounted(async () => {
  if (!statementQuiz.value?.id) {
    await router.push({ name: 'create-quizzes' });
  }
  await setCurrentQuestion(statementQuiz.value?.questionOrder);
});

const increaseOrder = async () => {
  if (statementQuiz.value && questionOrder.value + 1 < statementQuiz.value.questions.length) {
    try {
      calculateTime();
      await setCurrentQuestion(questionOrder.value + 1);
    } catch (error) {
      store.setError(error as string);
    }
  }
  nextConfirmationDialog.value = false;
};

const decreaseOrder = (): void => {
  if (questionOrder.value > 0 && !statementQuiz.value?.oneWay) {
    calculateTime();
    setCurrentQuestion(questionOrder.value - 1);
  }
};

const changeOrder = (newOrder: number): void => {
  if (!statementQuiz.value?.oneWay) {
    if (newOrder >= 0 && statementQuiz.value && newOrder < statementQuiz.value.questions.length) {
      calculateTime();
      setCurrentQuestion(newOrder);
    }
  }
};

const changeAnswer = async () => {
  if (statementQuiz.value && statementQuiz.value.answers[questionOrder.value]) {
    calculateTime();

    try {
      if (statementQuiz.value && statementQuiz.value.timed) {
        let newAnswer = statementQuiz.value.answers[questionOrder.value];
        newAnswer.timeToSubmission = statementQuiz.value.timeToSubmission;

        await RemoteServices.submitAnswer(statementQuiz.value.id, newAnswer);
      }
    } catch (error) {
      store.setError(error as string);
      await router.push({ name: 'available-quizzes' });
    }
  }
};

const confirmAnswer = () => {
  if (
    statementQuiz.value?.oneWay &&
    statementQuiz.value && questionOrder.value + 1 < statementQuiz.value.questions.length
  ) {
    nextConfirmationDialog.value = true;
  } else {
    increaseOrder();
  }
};

const concludeQuiz = async () => {
  store.setLoading();
  try {
    calculateTime();
    confirmed.value = true;

    let correctAnswers: StatementCorrectAnswer[] = [];
    if (statementQuiz.value) {
      correctAnswers = await RemoteServices.concludeQuiz(statementQuiz.value);
    } else {
      throw Error('No quiz');
    }

    if (correctAnswers.length !== 0) {
      store.correctAnswers = correctAnswers;
      await router.push({ name: 'quiz-results' });
    } else {
      quizSubmitted.value = true;
      store.statementQuiz = null;
    }
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

watch(
  () => statementQuiz.value?.timeToSubmission,
  (newVal) => {
    if (statementQuiz.value && !newVal) {
      concludeQuiz();
    }
  }
);

const convertToHHMMSS = (time: number | undefined | null): string => {
  return milisecondsToHHMMSS(time);
};
</script>
