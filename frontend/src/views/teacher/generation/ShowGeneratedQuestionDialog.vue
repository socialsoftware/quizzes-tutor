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
          <span class="headline">Generated question</span>
          <v-spacer />
          <v-chip :color="questionGeneration.getStatusColor()">
            {{ questionGeneration.getStatusName() }}
          </v-chip>
        </v-card-title>

        <v-alert
          v-if="questionGeneration.needsHumanAttention"
          type="warning"
          variant="tonal"
          class="mb-3"
          data-cy="AttentionAlert"
        >
          The automatic checks did not pass after {{ questionGeneration.verificationRetries }}
          retries. Read this question carefully before approving it.
        </v-alert>

        <v-card variant="outlined" class="text-left" id="question">
          <v-card-title>
            <span class="headline">{{ questionGeneration.question.title }}</span>
          </v-card-title>
          <v-card-text>
            <show-question :question="questionGeneration.question" />
            <div v-if="questionGeneration.explanation" class="explanation" data-cy="Explanation">
              <b>Why the answer is correct:</b> {{ questionGeneration.explanation }}
            </div>
          </v-card-text>
        </v-card>

        <div class="origin text-left" data-cy="Origin">
          Written by <b>{{ questionGeneration.modelId || 'unknown model' }}</b>
          ({{ groundingLabel }}), from {{ questionGeneration.sourceChunkIds.length }} section(s) of your
          materials.
        </div>

        <v-card-title class="headline">Reviews</v-card-title>
        <v-card variant="outlined">
          <div v-if="questionGeneration.isOpen()" class="text-left">
            <v-card variant="flat">
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
                      item-value="key"
                      label="Review Type"
                      data-cy="SelectMenu"
                    >
                      <template #selection="{ item: selectItem }">
                        <v-chip size="small" :color="(selectItem as any).raw?.color">{{
                          (selectItem as any).raw?.title
                        }}</v-chip>
                      </template>
                    </v-select>
                  </v-col>
                  <v-btn color="blue-darken-1" @click="review" data-cy="SubmitButton">submit</v-btn>
                </v-row>
              </v-card-text>
            </v-card>
          </div>
          <v-expansion-panels hover flat focusable data-cy="ReviewLog">
            <v-expansion-panel>
              <v-expansion-panel-title>
                <span>Review Log</span>
              </v-expansion-panel-title>
              <v-expansion-panel-text>
                <show-reviews
                  class="history"
                  :key="reviewsComponentKey"
                  :questionGeneration="questionGeneration"
                />
              </v-expansion-panel-text>
            </v-expansion-panel>
          </v-expansion-panels>
        </v-card>
      </v-container>
      <v-card-actions>
        <v-spacer />
        <v-btn data-cy="CloseButton" color="blue-darken-1" @click="$emit('update:dialog', false)">
          close
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';
import ShowReviews from '@/views/questionsubmission/ShowReviews.vue';
import Review from '@/models/management/Review';
import QuestionGeneration from '@/models/management/generation/QuestionGeneration';

const props = defineProps<{
  dialog: boolean;
  questionGeneration: QuestionGeneration;
}>();

const emit = defineEmits<{
  (e: 'update:dialog', open: boolean): void;
  (e: 'reviewed'): void;
}>();

const store = useStore();

const comment = ref('');
const selected = ref<string | null>(null);
const reviewsComponentKey = ref(0);
const statusOptions = [...Review.statusOptions];

const groundingLabel = computed(() =>
  props.questionGeneration.groundingMode === 'ENRICHED'
    ? 'materials plus general knowledge'
    : 'only from the materials'
);

const review = async () => {
  if (selected.value === null) {
    store.setError('Error: Please select review type');
    return;
  }
  if (comment.value.trim() === '') {
    store.setError('Error: Please write a comment');
    return;
  }

  store.setLoading();
  try {
    const newReview = new Review();
    newReview.prepareGenerationReview(
      props.questionGeneration.id,
      selected.value,
      comment.value,
      store.getUser!.id as number
    );
    await RemoteServices.createGenerationReview(newReview);

    emit('reviewed');
    if (selected.value === 'COMMENT') {
      comment.value = '';
      selected.value = null;
      reviewsComponentKey.value += 1;
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
.explanation {
  margin-top: 12px;
  color: gray;
}
.origin {
  margin: 12px 4px;
  color: gray;
  font-size: 0.9rem;
}
</style>
