<template>
  <div>
    <span v-html="convertMarkDown(question.content, question.image)" />
    <br />
    <component
      :is="question.questionDetailsDto.type"
      :questionDetails="question.questionDetailsDto"
    />
    <br />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import MultipleChoiceView from '@/components/multiple-choice/MultipleChoiceView.vue';

@Component({
  components: {
    multiple_choice: MultipleChoiceView
  }
})
export default class ShowQuestion extends Vue {
  @Prop({ type: Question, required: true }) readonly question!: Question;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
