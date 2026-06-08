<template>
  <v-dialog
    v-model="dialogOpen"
    @keydown.esc="dialogOpen = false"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">Clarifications</span>
      </v-card-title>

      <clarification-component
        :clarifications="clarifications"
        :can-change="true"
        v-on:make-private="onMakePrivate"
      >
      </clarification-component>

      <v-card-actions>
        <v-spacer />
        <v-btn class="text-white" color="blue darken-1" @click="dialogOpen = false">close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';
import Reply from '@/models/management/Reply';
import ClarificationComponent from '@/views/student/discussions/ClarificationComponent.vue';

const dialogOpen = defineModel<boolean>('dialog', { default: false });

const props = defineProps<{
  question: Question;
}>();

const emit = defineEmits(['remove-clarification']);
const store = useStore();

const clarifications = ref<Reply[]>([]);

onMounted(async () => {
  store.setLoading();
  try {
    const fetchedClarifications = await RemoteServices.getClarificationsByQuestionId(props.question.id!);
    clarifications.value = fetchedClarifications;
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

const onMakePrivate = (clarificationId: number) => {
  clarifications.value = clarifications.value.filter(
    (clarification) => clarification.id !== clarificationId
  );
  emit('remove-clarification', props.question.id);
};
</script>

<style lang="scss" scoped>
.clarification-container {
  color: rgb(51, 51, 51);
  user-select: none;
  caret-color: rgb(51, 51, 51);

  .discussion {
    margin: 5px;
    padding: 25px;
  }

  ul {
    list-style-type: none;
  }

  .reply {
    margin: 5px;
    padding: 15px 15px 0 30px;
  }

  .textarea-reply {
    margin-bottom: -18px;
  }
}
</style>
