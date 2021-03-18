<template>
  <v-form>
    <v-autocomplete
      v-if="canEditTopics()"
      v-model="questionTopics"
      :items="topics"
      multiple
      return-object
      item-text="name"
      item-value="name"
      @change="saveTopics"
    >
      <template v-slot:selection="data">
        <v-chip
          v-bind="data.attrs"
          :input-value="data.selected"
          close
          @click="data.select"
          @click:close="removeTopic(data.item)"
        >
          {{ data.item.name }}
        </v-chip>
      </template>
      <template v-slot:item="data">
        <v-list-item-content>
          <v-list-item-title v-html="data.item.name" />
        </v-list-item-content>
      </template>
    </v-autocomplete>

    <v-select
      v-else
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
import RemoteServices from '@/services/RemoteServices';
import QuestionSubmission from '@/models/management/QuestionSubmission';

@Component
export default class EditQuestionSubmissionTopics extends Vue {
  @Prop({ type: QuestionSubmission, required: true })
  readonly questionSubmission!: QuestionSubmission;
  @Prop({ type: Array, required: true }) readonly topics!: Topic[];
  @Prop({ type: Boolean }) readOnly: boolean | undefined;

  questionTopics: Topic[] = JSON.parse(
    JSON.stringify(this.questionSubmission.question.topics)
  );

  async saveTopics() {
    if (this.questionSubmission.question.id) {
      try {
        if (this.$store.getters.isStudent) {
          await RemoteServices.updateQuestionSubmissionTopics(
            this.questionSubmission.id,
            this.questionTopics
          );
        } else {
          await RemoteServices.updateQuestionTopics(
            this.questionSubmission.question.id,
            this.questionTopics
          );
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }

    this.$emit(
      'submission-changed-topics',
      this.questionSubmission.question.id,
      this.questionTopics
    );
  }

  removeTopic(topic: Topic) {
    this.questionTopics = this.questionTopics.filter(
      (element) => element.id != topic.id
    );
    this.saveTopics();
  }

  canEditTopics() {
    return (
      !this.readOnly &&
      ((this.$store.getters.isStudent &&
        this.questionSubmission.isInRevision()) ||
        (this.$store.getters.isTeacher &&
          !this.questionSubmission.isRejected()))
    );
  }
}
</script>
