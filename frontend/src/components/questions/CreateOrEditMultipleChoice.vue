<template>
  <v-card-text class="text-left">
    <div v-for="index in value.options.length" :key="index">
      <v-switch
        v-model="value.options[index - 1].correct"
        class="ma-4"
        label="Correct"
      />
      <v-textarea
        outline
        rows="10"
        v-model="value.options[index - 1].content"
        :label="`Option ${index}`"
      ></v-textarea>
    </div>
  </v-card-text>
</template>

<script lang="ts">
import { Component, Vue, Watch, Prop } from 'vue-property-decorator';
import Question from '@/models/management/Question';

@Component
export default class CreateOrEditMultipleChoice extends Vue {
    @Prop({ type: Question }) readonly value!: Question;
}
</script>
