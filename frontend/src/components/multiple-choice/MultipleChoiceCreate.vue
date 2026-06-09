<template>
  <div class="multiple-choice-options">
    <v-row>
      <v-col cols="1" offset="10"> Correct </v-col>
    </v-row>

    <v-row
      v-for="(option, index) in sQuestionDetails.options"
      :key="index"
      data-cy="questionOptionsInput"
    >
      <v-col cols="10">
        <v-textarea
          v-model="option.content"
          :label="`Option ${index + 1}`"
          :data-cy="`Option${index + 1}`"
          rows="1"
          auto-grow
        ></v-textarea>
      </v-col>
      <v-col cols="1">
        <v-switch
          v-model="option.correct"
          inset
          :data-cy="`Switch${index + 1}`"
        />
      </v-col>
      <v-col v-if="sQuestionDetails.options.length > 2">
        <v-tooltip bottom>
          <template v-slot:activator="{ props }">
            <span :data-cy="`Delete${index + 1}`" v-bind="props" @click="removeOption(index)">
              <v-icon
                small
                class="ma-1 action-button"
                color="red"
                >close</v-icon
              >
            </span>
          </template>
          <span>Remove Option</span>
        </v-tooltip>
      </v-col>
    </v-row>

    <v-row>
      <v-btn
        class="ma-auto"
        color="blue darken-1"
        @click="addOption"
        data-cy="addOptionMultipleChoice"
        >Add Option</v-btn
      >
    </v-row>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import MultipleChoiceQuestionDetails from '@/models/management/questions/MultipleChoiceQuestionDetails';
import Option from '@/models/management/Option';

const props = defineProps<{
  questionDetails: MultipleChoiceQuestionDetails;
}>();

const emit = defineEmits(['update:questionDetails']);

const sQuestionDetails = computed({
  get: () => props.questionDetails,
  set: (val) => emit('update:questionDetails', val),
});

const addOption = () => {
  sQuestionDetails.value.options.push(new Option());
};

const removeOption = (index: number) => {
  sQuestionDetails.value.options.splice(index, 1);
};
</script>
