<template>
  <v-form>
    <v-autocomplete
      v-if="canEditTopics()"
      v-model="questionTopics"
      :items="topics"
      multiple
      return-object
      item-title="name"
      item-value="name"
      @update:model-value="saveTopics"
    >
      <template v-slot:selection="{ item }">
        <v-chip
          closable
          @click:close="removeTopic((item as any).raw)"
        >
          {{ (item as any).raw.name }}
        </v-chip>
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
      <template v-slot:selection="{ item }">
        <v-chip>
          {{ (item as any).raw.name }}
        </v-chip>
      </template>
    </v-select>
  </v-form>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import Topic from '@/models/management/Topic';
import RemoteServices from '@/services/RemoteServices';
import QuestionSubmission from '@/models/management/QuestionSubmission';

const props = defineProps<{
  questionSubmission: QuestionSubmission;
  topics: Topic[];
  readOnly?: boolean;
}>();

const emit = defineEmits(['submission-changed-topics']);

const store = useStore();
const questionTopics = ref<Topic[]>([]);

onMounted(() => {
  questionTopics.value = JSON.parse(
    JSON.stringify(props.questionSubmission.question.topics)
  );
});

const saveTopics = async () => {
  if (props.questionSubmission.question.id) {
    try {
      if (store.isStudent) {
        await RemoteServices.updateQuestionSubmissionTopics(
          props.questionSubmission.id!,
          questionTopics.value
        );
      } else {
        await RemoteServices.updateQuestionTopics(
          props.questionSubmission.question.id,
          questionTopics.value
        );
      }
    } catch (error) {
      store.setError(error as string);
    }
  }

  emit(
    'submission-changed-topics',
    props.questionSubmission.question.id,
    questionTopics.value
  );
};

const removeTopic = (topic: Topic) => {
  questionTopics.value = questionTopics.value.filter(
    (element) => element.id != topic.id
  );
  saveTopics();
};

const canEditTopics = () => {
  return (
    !props.readOnly &&
    ((store.isStudent &&
      props.questionSubmission.isInRevision()) ||
      (store.isTeacher &&
        !props.questionSubmission.isRejected()))
  );
};
</script>
