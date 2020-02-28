<template>
  <v-dialog
    v-model="dialog"
    @keydown.esc="$emit('close-quiz-dialog')"
    max-width="75%"
  >
    <v-card v-if="quiz">
      <v-card-title>{{ quiz.title }}</v-card-title>

      <v-card-text>
        <show-question-list :questions="quiz.questions" />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="$emit('close-quiz-dialog')"
          >close</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { Quiz } from '@/models/management/Quiz';
import ShowQuestionList from '@/views/teacher/questions/ShowQuestionList.vue';

@Component({
  components: {
    'show-question-list': ShowQuestionList
  }
})
export default class ShowQuizDialog extends Vue {
  @Prop({ type: Quiz, required: true }) readonly quiz!: Quiz | null;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
}
</script>
