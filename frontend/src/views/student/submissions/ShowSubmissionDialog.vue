<template>
    <v-dialog
            :value="dialog"
            @input="$emit('dialog', false)"
            @keydown.esc="$emit('dialog', false)"
            max-width="75%"
    >
        <v-card>
            <v-card-title>
                <span class="headline">{{ submission.question.title }}</span>
            </v-card-title>

            <v-card-text class="text-left">
                <show-question :question="submission.question" />
                <div v-if="submission.argument !== null && submission.name === $store.getters.getUser.name">
                    <h3 style="display: inline">Argument: </h3>
                    <p style="display: inline">{{ submission.argument }}</p>
                </div>
            </v-card-text>
            <v-card-title>
                <span class="headline">{{ 'Reviews' }}</span>
            </v-card-title>
            <div class="text-left">
                <show-reviews
                        class="history"
                        :key="reviewsComponentKey"
                        :submission="submission"
                />
            </div>
            <v-card-actions>
                <v-spacer />
                <v-btn dark color="primary" data-cy="close" @click="$emit('dialog')"
                >close</v-btn
                >
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script lang="ts">
  import { Component, Vue, Prop, Model, Watch } from 'vue-property-decorator';
  import ShowReviews from '@/views/student/submissions/ShowReviews.vue'
  import ShowQuestion from '@/views/student/submissions/ShowQuestion.vue';
  import Submission from '@/models/management/Submission';

  @Component({
    components: {
      'show-question': ShowQuestion,
      'show-reviews': ShowReviews
    }
  })
  export default class ShowSubmissionDialog extends Vue {
    @Model('dialog', Boolean) dialog!: boolean;
    @Prop({ type: Submission, required: true }) readonly submission!: Submission;

    reviewsComponentKey: number = 0;

    @Watch('dialog')
    forceRerender() {
      if (this.dialog) {
        this.reviewsComponentKey += 1;
      }
    }


  }
</script>
<style lang="scss" scoped>
    .history {
        max-height: 235px;
        overflow-y: auto;
    }
</style>
