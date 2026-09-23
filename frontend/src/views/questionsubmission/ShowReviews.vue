<template>
  <v-container>
    <p v-if="reviews.length === 0" style="text-align: center; color: lightgrey">
      No reviews available
    </p>
    <v-container
      v-for="(review, index) in reviews"
      :key="review.id"
      class="review"
    >
      <span style="float: left; padding-right: 10px">
        <b>{{ review.name + '  ' }}</b>
        <v-chip size="x-small">{{ review.creationDate }}</v-chip>
        {{ '| ' }}
        <span v-if="!review.isComment()">
          <v-chip size="small" :color="review.getStatusColor()">
            {{ review.getType(index === reviews.length - 1) }}
          </v-chip>
          {{ ' |' }}
        </span>
      </span>
      {{ review.comment }}
    </v-container>
    <hr v-if="reviews.length !== 0" style="border: 0.5px lightgrey solid" />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import Review from '@/models/management/Review';
import QuestionSubmission from '@/models/management/QuestionSubmission';
import RemoteServices from '@/services/RemoteServices';

const props = defineProps<{
  questionSubmission: QuestionSubmission;
}>();

const store = useStore();
const reviews = ref<Review[]>([]);

onMounted(async () => {
  await getReviews();
});

const getReviews = async () => {
  store.setLoading();
  try {
    const rawReviews = await RemoteServices.getQuestionSubmissionReviews(
      props.questionSubmission.id!
    );
    reviews.value = rawReviews.sort((a, b) => sortNewestFirst(a, b));
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const sortNewestFirst = (a: Review, b: Review) => {
  if (a.creationDate && b.creationDate)
    return a.creationDate < b.creationDate ? 1 : -1;
  else return 0;
};
</script>

<style lang="scss" scoped>
.review {
  border-style: solid;
  border-color: lightgrey;
  border-width: 1px 1px 0 10px;
}
</style>
