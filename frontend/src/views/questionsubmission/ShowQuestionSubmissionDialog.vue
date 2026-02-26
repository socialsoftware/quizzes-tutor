<template>
  <v-dialog
    :model-value="dialog"
    @update:model-value="$emit('update:dialog', $event)"
    @keydown.esc="$emit('update:dialog', false)"
    max-width="75%"
  >
    <v-card>
      <v-container fluid>
        <v-card-title>
          <span class="headline">Question Submission</span>
          <v-spacer />
          <v-chip :color="questionSubmission.getStatusColor()">
            {{ questionSubmission.getStatus() }}
          </v-chip>
        </v-card-title>
        <v-card ripple outlined class="text-left" id="question">
          <v-card-title>
            <span class="headline">{{
              questionSubmission.question.title
            }}</span>
          </v-card-title>
          <v-card-text>
            <show-question :question="questionSubmission.question" />
          </v-card-text>
        </v-card>
        <v-card-title class="headline">Reviews</v-card-title>
        <v-card outlined>
          <div
            class="text-left"
            v-if="
              store.isTeacher && questionSubmission.isInDiscussion()
            "
          >
            <v-card flat>
              <v-card-text>
                <v-row align="center" class="newReview">
                  <v-textarea
                    rows="1"
                    v-model="comment"
                    label="Comment"
                    data-cy="Comment"
                    variant="outlined"
                  ></v-textarea>
                  <v-col cols="3">
                    <v-select
                      v-model="selected"
                      :items="statusOptions"
                      data-cy="SelectMenu"
                      chips
                      label="Review Type"
                    >
                      <template #selection="{ item: selectItem }">
                        <v-chip size="small" :color="(selectItem as any).raw.color">{{
                          (selectItem as any).raw.text
                        }}</v-chip>
                      </template>
                    </v-select>
                  </v-col>
                  <v-btn
                    color="blue-darken-1"
                    @click="reviewQuestionSubmission(selected)"
                    data-cy="SubmitButton"
                    >submit</v-btn
                  >
                </v-row>
              </v-card-text>
            </v-card>
          </div>
          <div
            class="text-left"
            v-if="
              store.isStudent && questionSubmission.isInDiscussion()
            "
          >
            <v-card-text>
              <v-row align="center" class="newReview">
                <v-textarea
                  rows="1"
                  v-model="comment"
                  label="Comment"
                  data-cy="Comment"
                  variant="outlined"
                ></v-textarea>
                <v-btn
                  color="blue-darken-1"
                  @click="reviewQuestionSubmission('COMMENT')"
                  data-cy="SubmitButton"
                  >submit</v-btn
                >
              </v-row>
            </v-card-text>
          </div>
          <v-expansion-panels
            hover
            flat
            focusable
            v-bind:style="{
              'border-top': questionSubmission.isInDiscussion()
                ? '1px solid lightgrey'
                : '',
            }"
            data-cy="ReviewLog"
          >
            <v-expansion-panel>
              <v-expansion-panel-title>
                <span>Review Log</span>
              </v-expansion-panel-title>
              <v-expansion-panel-text>
                <show-reviews
                  class="history"
                  :key="reviewsComponentKey"
                  :questionSubmission="questionSubmission"
                />
              </v-expansion-panel-text>
            </v-expansion-panel>
          </v-expansion-panels>
        </v-card>
      </v-container>
      <v-card-actions>
        <v-spacer />
        <v-btn
          data-cy="CloseButton"
          color="blue-darken-1"
          @click="$emit('update:dialog', false)"
          >close</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { useStore } from '@/store';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';
import QuestionSubmission from '@/models/management/QuestionSubmission';
import Review from '@/models/management/Review';
import RemoteServices from '@/services/RemoteServices';
import ShowReviews from '@/views/questionsubmission/ShowReviews.vue';

const props = defineProps<{
  dialog: boolean;
  questionSubmission: QuestionSubmission;
}>();

const emit = defineEmits(['update:dialog']);
const store = useStore();

const reviewsComponentKey = ref<number>(0);
const comment = ref<string>('');
const statusOptions = ref([...Review.statusOptions]);
const selected = ref<string | null>(null);

const forceRerender = () => {
  if (props.dialog) {
    updateReviews();
  }
};

onMounted(() => {
  forceRerender();
});

watch(() => props.dialog, () => {
  forceRerender();
});

const updateReviews = () => {
  reviewsComponentKey.value += 1;
  comment.value = '';
  selected.value = null;
};

const createReview = (status: string) => {
  let review = new Review();
  review.prepareReview(
    props.questionSubmission.id as number,
    status,
    comment.value,
    store.getUser!.id as number
  );
  return review;
};

const reviewQuestionSubmission = async (type: string | null) => {
  if (type === null) {
    store.setError('Error: Please select review type');
    return;
  }
  store.setLoading();
  try {
    await RemoteServices.createReview(createReview(type!));
    store.isTeacher
      ? await RemoteServices.toggleStudentNotificationRead(
          props.questionSubmission.id as number,
          false
        )
      : await RemoteServices.toggleTeacherNotificationRead(
          props.questionSubmission.id as number,
          false
        );
    if (type == 'COMMENT') {
      updateReviews();
    } else {
      emit('update:dialog', false);
    }
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};
</script>

<style lang="scss" scoped>
.history {
  max-height: 210px;
  overflow-y: auto;
  text-align: left;
}
.newReview {
  display: flex;
  justify-content: flex-end;
  padding-right: 20px;
  padding-left: 20px;
}
.editText {
  color: gray;
  font-size: 15px;
}
</style>
