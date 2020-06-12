<template>
  <ol>
    <li
      v-for="question in questions"
      :key="question.sequence"
      class="text-left"
    >
        <show-multiple-choice-question 
          v-if="question.type === 'multiple_choice'" 
          :question="question" />
        <show-code-fill-in-question 
          v-if="question.type === 'code_fill_in'" 
          :question="question" />
    </li>
  </ol>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';
import ShowCodeFillInQuestion from '@/views/teacher/questions/ShowCodeFillInQuestion.vue';

@Component({
  components: {
    'show-multiple-choice-question': ShowQuestion,
    'show-code-fill-in-question': ShowCodeFillInQuestion
   }
})
export default class ShowQuestionList extends Vue {
  @Prop({ type: Array, required: true }) readonly questions!: Question[];
}
</script>
