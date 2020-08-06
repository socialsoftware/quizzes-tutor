<template>
  <v-form>
    <v-select
      v-model="questionTopics"
      :items="topics"
      multiple
      disabled
      append-icon="false"
    >
      <template v-slot:selection="data">
        <v-chip v-bind="data.attrs">
          {{ data.item.name }}
        </v-chip>
      </template>
    </v-select>
  </v-form>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Topic from '@/models/management/Topic';
import QuestionSubmission from '@/models/management/QuestionSubmission';

@Component
export default class ViewQuestionSubmissionTopics extends Vue {
  @Prop({ type: QuestionSubmission, required: true })
  readonly questionSubmission!: QuestionSubmission;
  @Prop({ type: Array, required: true }) readonly topics!: Topic[];

  questionTopics: Topic[] = JSON.parse(
    JSON.stringify(this.questionSubmission.question.topics)
  );
}
</script>
