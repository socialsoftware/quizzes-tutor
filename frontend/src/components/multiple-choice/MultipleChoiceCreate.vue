<template>
  <div class="multiple-choice-options">
    <div v-for="index in sQuestionDetails.options.length" :key="index">
      <v-row>
        <v-textarea
          auto-grow
          rows="2"
          v-model="sQuestionDetails.options[index - 1].content"
          :label="`Option ${index}`"
        ></v-textarea>
        <v-switch
          v-model="sQuestionDetails.options[index - 1].correct"
          class="ma-4"
          :label="`Correct ${index}`"
        />
      </v-row>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Model, PropSync, Vue, Watch } from 'vue-property-decorator';
import MultipleChoiceQuestionDetails from '@/models/management/questions/MultipleChoiceQuestionDetails';

@Component
export default class MultipleChoiceCreate extends Vue {
  @PropSync('questionDetails', { type: MultipleChoiceQuestionDetails })
  sQuestionDetails!: MultipleChoiceQuestionDetails;
}
</script>
