<template>
  <v-dialog
    :model-value="dialog"
    @update:model-value="$emit('update:dialog', $event)"
    @keydown.esc="$emit('update:dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card class="px-5">
      <v-card-title>
        <span class="headline">
          {{
            editMode(editQuestionSubmission)
              ? 'Edit Submission'
              : 'New Submission'
          }}
        </span>
      </v-card-title>

      <v-card-text class="pa-4 text-left" v-if="editQuestionSubmission">
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-select
              v-model="questionType"
              :rules="[(v) => !!v || 'Question type is required']"
              label="Question Type"
              required
              :items="questionTypesOptions"
              @update:model-value="updateQuestionType"
              :readonly="editMode(editQuestionSubmission)"
            />
          </v-row>
          <v-row>
            <v-text-field
              v-model="editQuestionSubmission.question.title"
              :rules="[(v) => !!v || 'Question title is required']"
              data-cy="QuestionTitle"
              label="Title"
              required
            />
          </v-row>

          <v-row>
            <v-textarea
              v-model="editQuestionSubmission.question.content"
              :rules="[(v) => !!v || 'Question content is required']"
              auto-grow
              data-cy="QuestionContent"
              label="Question"
              required
              rows="4"
            ></v-textarea>
          </v-row>

          <component
            :is="getQuestionComponent(editQuestionSubmission.question.questionDetailsDto.type)"
            :questionDetails="
              editQuestionSubmission.question.questionDetailsDto as any
            "
            :readonlyEdit="editMode(editQuestionSubmission)"
          />

          <v-row>
            <v-textarea
              variant="outlined"
              rows="1"
              v-model="comment"
              label="Comment"
              data-cy="Comment"
            ></v-textarea>
          </v-row>
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="red darken-1"
          @click="$emit('update:dialog', false)"
          data-cy="CancelButton"
          >Cancel</v-btn
        >
        <v-btn
          color="green darken-1"
          @click="createQuestionSubmission(false)"
          data-cy="SaveButton"
          >Save</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="createQuestionSubmission(true)"
          data-cy="RequestReviewButton"
          >Request Review</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import QuestionSubmission from '../../models/management/QuestionSubmission';
import MultipleChoiceCreate from '@/components/multiple-choice/MultipleChoiceCreate.vue';
import CodeFillInCreate from '@/components/code-fill-in/CodeFillInCreate.vue';
import CodeOrderCreate from '@/components/code-order/CodeOrderCreate.vue';
import { QuestionFactory, QuestionTypes } from '@/services/QuestionHelpers';

const props = defineProps<{
  dialog: boolean;
  questionSubmission: QuestionSubmission;
}>();

const emit = defineEmits(['update:dialog', 'submit-submission', 'save-submission']);

const store = useStore();

const editQuestionSubmission = ref<QuestionSubmission>(
  new QuestionSubmission(props.questionSubmission)
);
const comment = ref('');
const questionType = ref('');

const getQuestionComponent = (type: string) => {
  if (type === 'multiple_choice') return MultipleChoiceCreate;
  if (type === 'code_fill_in') return CodeFillInCreate;
  if (type === 'code_order') return CodeOrderCreate;
  return null;
}

onMounted(() => {
  questionType.value = props.questionSubmission.question.questionDetailsDto.type;
});

const questionTypesOptions = computed(() => {
  return Object.values(QuestionTypes).map((qt) => ({
    title: qt.replace(/_/g, ' '),
    value: qt,
  }));
});

const updateQuestionType = () => {
  editQuestionSubmission.value.question.questionDetailsDto =
    QuestionFactory.getFactory(
      questionType.value
    ).createEmptyQuestionDetails();
};

watch(() => props.questionSubmission, () => {
  editQuestionSubmission.value = new QuestionSubmission(
    props.questionSubmission
  );
}, { immediate: true, deep: true });

const createQuestionSubmission = async (requestReview: boolean) => {
  if (
    editQuestionSubmission.value &&
    (!editQuestionSubmission.value.question.title ||
      !editQuestionSubmission.value.question.content)
  ) {
    store.setError('Error: Question must have title and content');
    return;
  } else if (requestReview && comment.value.trim().length == 0) {
    store.setError('Error: Please insert a short comment to justify your submission');
    return;
  } else if (
    !requestReview &&
    comment.value.trim().length > 0 &&
    !confirm('Comment will not be saved. Do you wish to proceed?')
  ) {
    return;
  }
  try {
    let result;
    if (editQuestionSubmission.value.question.id != null) {
      result = await RemoteServices.updateQuestionSubmission(
        editQuestionSubmission.value
      );
    } else {
      result = await RemoteServices.createQuestionSubmission(
        editQuestionSubmission.value
      );
    }
    if (requestReview) {
      emit('submit-submission', comment.value, result);
    } else {
      emit('save-submission', result);
    }
  } catch (error) {
    store.setError(error as string);
  }
};

const editMode = (editQS: QuestionSubmission) => {
  return (
    editQS.question &&
    editQS.question.id !== null
  );
};
</script>

<style lang="scss" scoped>
.v-select-list,
.v-select {
  text-transform: capitalize;
}
</style>
