<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close')"
    @keydown.esc="$emit('close')"
    max-width="75%"
  >
    <v-card>
      <v-card-text>
        <ol>
          <li
            v-for="question in questions"
            :key="question.id"
            class="text-left"
          >
            {{ question.status }}
            <v-card-text
              class="text-left"
              :class="{ red: question.status !== 'AVAILABLE' }"
            >
              <show-question :question="question" />
            </v-card-text>
          </li>
        </ol>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn dark color="blue darken-1" @click="$emit('close')">close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';

@Component({
  components: {
    'show-question': ShowQuestion,
  },
})
export default class ShowQuestionListDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Array, required: true }) readonly questions!: Question[];
}
</script>
