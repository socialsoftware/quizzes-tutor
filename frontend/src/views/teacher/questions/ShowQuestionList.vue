<template>
  <ol>
    <li
      v-for="question in questions"
      :key="question.sequence"
      class="text-left"
    >
      <show-question :question="question" />
    </li>
  </ol>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';

@Component({
  components: { 'show-question': ShowQuestion },
})
export default class ShowQuestionList extends Vue {
  @Prop({ type: Array, required: true }) readonly questions!: Question[];
}
</script>
