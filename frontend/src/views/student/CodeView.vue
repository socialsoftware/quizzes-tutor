<template>
  <v-card class="mx-auto my-12" max-width="374">
    <v-card-title>Quiz by Code</v-card-title>
    <v-card-text>
      <v-row align="center" class="mx-0">
        <v-text-field label="Code" v-model="code" required></v-text-field>
      </v-row>
      <v-btn :disabled="code == null" color="primary" @click="setCode"
        >Send</v-btn
      >
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useStore } from '@/store';
import { useRouter, useRoute } from 'vue-router';
import RemoteServices from '@/services/RemoteServices';
import StatementQuiz from '@/models/statement/StatementQuiz';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';

const store = useStore();
const router = useRouter();
const route = useRoute();

const hasCode = ref(false);
const code = ref<number | null>(null);
const quiz = ref<StatementQuiz | null>(null);
const timer = ref('');

const getQuizByCode = async () => {
  store.setLoading();
  if (code.value && route.name === 'code') {
    try {
      quiz.value = await RemoteServices.getQuizByCode(
        store.currentCourse!.courseExecutionId as number,
        code.value as number
      );

      if (!quiz.value.timeToAvailability) {
        store.setStatementQuiz(quiz.value);
        await router.push({ name: 'solve-quiz' });
      }
    } catch (error) {
      store.setError(error as string);
      await router.push({ name: 'home' });
    }
  }
  store.clearLoading();
};

const setCode = async () => {
  hasCode.value = true;
  await getQuizByCode();
};

watch(
  () => quiz.value?.timeToAvailability,
  (newVal, oldVal) => {
    // Ask again only when the countdown to the start has run out. Asking for a quiz that has started is
    // what opens it, and a quiz that can be opened once must not be opened a second time here
    if (!!quiz.value && !newVal && !!oldVal) {
      getQuizByCode();
    }
    timer.value = milisecondsToHHMMSS(newVal ?? 0);
  }
);
</script>

<style lang="scss" scoped>
.input-box {
  width: 40% !important;
  background-color: white;
  border-width: 10px;
  border-style: solid;
  border-color: #818181;
}
</style>
