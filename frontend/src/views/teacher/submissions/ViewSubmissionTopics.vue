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
import Submission from '@/models/management/Submission';

@Component
export default class ViewSubmissionTopics extends Vue {
  @Prop({ type: Submission, required: true }) readonly submission!: Submission;
  @Prop({ type: Array, required: true }) readonly topics!: Topic[];

  questionTopics: Topic[] = JSON.parse(
    JSON.stringify(this.submission.question.topics)
  );
}
</script>
