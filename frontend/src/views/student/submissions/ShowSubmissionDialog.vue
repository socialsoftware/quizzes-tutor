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
            </v-card-text>

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
  import { Component, Vue, Prop, Model } from 'vue-property-decorator';
  import ShowQuestion from '@/views/student/submissions/ShowQuestion.vue';
  import Submission from '../../../models/management/Submission';

  @Component({
    components: {
      'show-question': ShowQuestion
    }
  })
  export default class ShowSubmissionDialog extends Vue {
    @Model('dialog', Boolean) dialog!: boolean;
    @Prop({ type: Submission, required: true }) readonly submission!: Submission;
  }
</script>
