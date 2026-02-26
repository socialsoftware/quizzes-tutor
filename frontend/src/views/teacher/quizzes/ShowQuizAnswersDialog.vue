<template>
  <div>
    <v-dialog
      :model-value="dialog"
      @update:model-value="$emit('update:dialog', $event)"
      @keydown.esc="$emit('update:dialog', false)"
      max-width="85%"
    >
      <v-data-table
        :headers="headers"
        :items="quizAnswers.quizAnswers"
        :search="search"
        disable-pagination
        :hide-default-footer="true"
        :mobile-breakpoint="0"
        class="show-quiz-answer"
      >
        <template v-slot:top>
          <v-card-title>
            <v-text-field
              v-model="search"
              append-icon="search"
              label="Search"
              class="mx-2"
            />

            <v-spacer />
            <span>{{ convertToHHMMSS(quizAnswers.timeToSubmission) }}</span>
          </v-card-title>
        </template>

        <template v-slot:[`item.name`]="{ item }">
          <span>
            {{ item.name }}
          </span>
          <span v-if="item.fraud" class="text-red-darken-1"
            >(Fraud Suspicion: check log)</span
          >
        </template>

        <template v-slot:[`item.submissionLag`]="{ item }">
          <span
            :class="[
              new Date(item.answerDate).getTime() -
                new Date(conclusionDate).getTime() <
              0
                ? 'text-green'
                : 'text-red-darken-4',
            ]"
          >
            {{
              convertToHHMMSS(
                new Date(item.answerDate).getTime() -
                  new Date(conclusionDate as Date | string).getTime()
              )
            }}
          </span>
        </template>

        <template v-slot:[`item.answers`]="{ item }">
          <span
            v-for="(questionAnswer, index) in item.questionAnswers"
            :key="questionAnswer.question.id || index"
            v-bind:class="[
              'answer',
              questionAnswer.answerDetails.isCorrect(
                questionAnswer.question.questionDetailsDto
              )
                ? 'correct'
                : 'incorrect',
            ]"
            @click="openAnswerDetailsDialog(item, index)"
            >{{
              questionAnswer.answerDetails.answerRepresentation(
                questionAnswer.question.questionDetailsDto
              )
            }}</span
          >
        </template>

        <template v-slot:[`body.append`]>
          <tr>
            <td colspan="4">Correct key:</td>
            <td>
              <span
                v-for="(sequence, index) in quizAnswers.correctSequence"
                :key="index"
                class="answer-key"
              >
                {{ sequence }}
              </span>
            </td>
          </tr>
        </template>
      </v-data-table>
    </v-dialog>
    <show-quiz-answers-details-dialog
      v-if="detailDialog"
      v-model:dialog="detailDialog"
      :quizAnswer="quizAnswerDetails as QuizAnswer"
      :questionNumber="quizAnswerDetailCurrentQuestion"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';
import { QuizAnswers } from '@/models/management/QuizAnswers';
import { QuizAnswer } from '@/models/management/QuizAnswer';
import ShowQuizAnswersDetailsDialog from '@/views/teacher/quizzes/ShowQuizAnswersDetailsDialog.vue';

defineProps<{
  dialog: boolean;
  quizAnswers: QuizAnswers;
  conclusionDate: string | Date;
}>();

defineEmits(['update:dialog']);

const detailDialog = ref(false);
const quizAnswerDetails = ref<QuizAnswer | undefined>(undefined);
const quizAnswerDetailCurrentQuestion = ref<number | undefined>(undefined);
const search = ref('');

const headers = ref<any[]>([
  { title: 'Name', value: 'name', align: 'start', width: '5%' },
  { title: 'Username', value: 'username', align: 'center', width: '5%' },
  { title: 'Start Date', value: 'creationDate', align: 'center', width: '5%' },
  { title: 'Submission Lag', value: 'submissionLag', align: 'center', width: '5%' },
  { title: 'Answers', value: 'answers', align: 'center', width: '15%' },
]);

const convertToHHMMSS = (time: number | undefined | null): string => {
  return milisecondsToHHMMSS(time);
};

const openAnswerDetailsDialog = (quizAnswerD: QuizAnswer, index: number) => {
  quizAnswerDetailCurrentQuestion.value = index;
  quizAnswerDetails.value = quizAnswerD;
  detailDialog.value = true;
};
</script>

<style lang="scss">
.show-quiz-answer {
  .answer {
    padding: 2px 2px;
    margin: 0px 1px;
    text-align: center;
    color: white;
    cursor: pointer;
    min-width: 15px;
    display: inline-block;

    &:hover {
      filter: brightness(0.7);
    }

    &.correct {
      background-color: #4caf50;
      border-color: #4caf50;
    }
    &.incorrect {
      background-color: #b71c1c;
      border-color: #b71c1c;
    }
  }

  .answer-key {
    padding: 2px 2px;
    margin: 0px 1px;
    text-align: center;
    color: white;
    min-width: 15px;
    display: inline-block;
    background-color: #808080;
  }
}
</style>
