<template>
  <v-dialog v-model="dialog" @keydown.esc="closeQuestionDialog" max-width="75%">
    <v-card v-if="question">
      <v-card-title>
        <span class="headline">{{ question.title }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-question :question="question" />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="closeQuestionDialog"
          >close</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop } from "vue-property-decorator";
import Question from "@/models/management/Question";
import ShowQuestion from "@/views/teacher/questions/ShowQuestion.vue";

@Component({
  components: {
    "show-question": ShowQuestion
  }
})
export default class ShowQuestionDialog extends Vue {
  @Prop({ type: Question, required: true }) readonly question!: Question | null;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;

  closeQuestionDialog() {
    this.$emit("close-show-question-dialog");
  }
}
</script>
