<template>
  <v-form>
    <v-autocomplete
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
  </v-form>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { useStore } from '@/store';
import Topic from '@/models/management/Topic';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';

const props = defineProps<{
  question: Question;
  topics: Topic[];
}>();

const emit = defineEmits(['question-changed-topics']);
const store = useStore();
const questionTopics = ref<Topic[]>([]);

onMounted(() => {
  questionTopics.value = JSON.parse(JSON.stringify(props.question.topics));
});

watch(() => props.question.topics, () => {
  questionTopics.value = JSON.parse(JSON.stringify(props.question.topics));
}, { deep: true });

const saveTopics = async () => {
  if (props.question.id) {
    try {
      await RemoteServices.updateQuestionTopics(
        props.question.id,
        questionTopics.value
      );
    } catch (error) {
      store.setError(error as string);
    }
  }

  emit(
    'question-changed-topics',
    props.question.id,
    questionTopics.value
  );
};

const removeTopic = (topic: Topic) => {
  questionTopics.value = questionTopics.value.filter(
    (element) => element.id != topic.id
  );
  saveTopics();
};
</script>
