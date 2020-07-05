<template>
  <v-card-text class="text-left">
    <div v-for="index in syncedQuestion.options.length" :key="index" data-cy="questionOptionsInput">
      <v-switch
        v-model="syncedQuestion.options[index - 1].correct"
        class="ma-4"
        label="Correct"
      />
      <v-textarea
        outline
        rows="10"
        v-model="syncedQuestion.options[index - 1].content"
        :label="`Option ${index}`"
      ></v-textarea>
    </div>
  </v-card-text>
</template>

<script lang="ts">
import { Component, Vue, Watch, Prop, PropSync } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import MultipleChoiceQuestion from '../../models/management/multiple-choice/MultipleChoiceQuestion';

@Component
export default class MultipleChoiceQuestionEdit extends Vue {
    @PropSync('question', {type: MultipleChoiceQuestion}) readonly syncedQuestion!: Question;
}
</script>
