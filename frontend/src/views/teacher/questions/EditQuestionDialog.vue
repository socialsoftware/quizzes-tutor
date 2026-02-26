<template>
  <v-dialog
    :model-value="dialog"
    @update:model-value="$emit('update:dialog', false)"
    @keydown.esc="$emit('update:dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card class="px-5" data-cy="createOrEditQuestionDialog">
      <v-card-title>
        <span class="headline">
          {{
            editQuestion && editQuestion.id === null
              ? 'New Question'
              : 'Edit Question'
          }}
        </span>
      </v-card-title>

      <v-card-text class="pa-4 text-left" v-if="editQuestion">
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-select
              v-model="questionType"
              :rules="[(v) => !!v || 'Question type is required']"
              label="Question Type"
              required
              :items="questionTypesOptions"
              @update:model-value="updateQuestionType"
              :readonly="editQuestion.id != null"
              data-cy="questionTypeInput"
            />
          </v-row>
          <v-row>
            <v-text-field
              v-model="editQuestion.title"
              :rules="[(v) => !!v || 'Question title is required']"
              label="Title"
              required
              data-cy="questionTitleTextArea"
            />
          </v-row>

          <v-row>
            <v-textarea
              v-model="editQuestion.content"
              label="Question"
              :rules="[(v) => !!v || 'Question content is required']"
              auto-grow
              required
              data-cy="questionQuestionTextArea"
              rows="4"
            ></v-textarea>
          </v-row>

          <component
            :is="componentMap[editQuestion.questionDetailsDto.type]"
            :questionDetails="editQuestion.questionDetailsDto"
            :readonlyEdit="editQuestion.id != null"
          />
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn class="text-white" color="red darken-1" @click="$emit('update:dialog', false)"
          >Cancel</v-btn
        >
        <v-btn
          class="text-white"
          color="green darken-1"
          @click="saveQuestion"
          :disabled="disableCreateButton"
          data-cy="saveQuestionButton"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { useStore } from '@/store';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';
import MultipleChoiceCreate from '@/components/multiple-choice/MultipleChoiceCreate.vue';
import CodeFillInCreate from '@/components/code-fill-in/CodeFillInCreate.vue';
import CodeOrderCreate from '@/components/code-order/CodeOrderCreate.vue';
import { QuestionFactory, QuestionTypes } from '@/services/QuestionHelpers';

const props = defineProps<{
  dialog: boolean;
  question: Question;
}>();

const emit = defineEmits(['update:dialog', 'save-question']);
const store = useStore();

const form = ref<any>(null);
const editQuestion = ref(new Question(props.question));
const questionType = ref(editQuestion.value.questionDetailsDto.type);
const disableCreateButton = ref(false);

const componentMap: Record<string, any> = {
  multiple_choice: MultipleChoiceCreate,
  code_fill_in: CodeFillInCreate,
  code_order: CodeOrderCreate,
};

const questionTypesOptions = computed(() => {
  return Object.values(QuestionTypes).map((qt) => ({
    title: qt.replace(/_/g, ' '),
    value: qt,
  }));
});

const updateQuestionType = () => {
  editQuestion.value.questionDetailsDto = QuestionFactory.getFactory(
    questionType.value
  ).createEmptyQuestionDetails();
};

watch(() => props.question, () => {
  editQuestion.value = new Question(props.question);
  questionType.value = editQuestion.value.questionDetailsDto.type;
}, { immediate: true, deep: true });

const saveQuestion = async () => {
  disableCreateButton.value = true;
  store.setLoading();
  form.value.validate();
  try {
    const result =
      editQuestion.value.id != null
        ? await RemoteServices.updateQuestion(editQuestion.value)
        : await RemoteServices.createQuestion(editQuestion.value);

    emit('save-question', result);
  } catch (error) {
    disableCreateButton.value = false;
    store.setError(error as string);
  }
  store.clearLoading();
};
</script>

<style lang="scss" scoped>
.v-select-list,
.v-select {
  text-transform: capitalize;
}
</style>
