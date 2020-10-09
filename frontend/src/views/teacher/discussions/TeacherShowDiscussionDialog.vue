<template>
  <v-dialog
      :value="dialog"
      @input="$emit('dialog', false)"
      @keydown.esc="$emit('dialog', false)"
      max-width="75%"
      max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ discussion.question.title }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-question :question="discussion.question" />
      </v-card-text>

      <v-card-text class="text-left">
        <v-card>
          <show-replies
              v-if="this.discussion != null"
              :discussion="this.discussion"
          />
        </v-card>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="$emit('dialog')">close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import Discussion from '@/models/management/Discussion';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';
import ShowReplies from '@/views/teacher/discussions/ShowReplies.vue';

@Component({
  components: {
    'show-question': ShowQuestion,
    'show-replies': ShowReplies
  }
})
export default class TeacherShowDiscussionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Discussion, required: true }) readonly discussion!: Discussion;
}
</script>

<style scoped></style>
