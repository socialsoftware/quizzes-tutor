<template>
  <ul>
    <li v-for="option in questionDetails.options" :key="option.number">
      <span
        v-if="option.correct"
        v-html="convertMarkDown('**[★]** ' + option.content)"
        v-bind:class="[option.correct ? 'font-weight-bold' : '']"
      />
      <span v-else v-html="convertMarkDown(option.content)" />
    </li>
  </ul>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import MultipleChoiceQuestionDetails from '@/models/management/questions/MultipleChoiceQuestionDetails';

@Component
export default class MultipleChoiceView extends Vue {
  @Prop() readonly questionDetails!: MultipleChoiceQuestionDetails;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
