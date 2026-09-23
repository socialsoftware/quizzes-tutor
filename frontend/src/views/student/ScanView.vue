<template>
  <div class="container">
    <qrcode-stream v-if="!quizId" @decode="onDecode"></qrcode-stream>
    <v-card v-else>
      <v-card-title class="justify-center">
        Hold on and wait {{ timer }} to start the quiz!
      </v-card-title>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useStore } from '@/store';
import { useRouter, useRoute } from 'vue-router';
import RemoteServices from '@/services/RemoteServices';
import { QrcodeStream } from 'vue-qrcode-reader';
import StatementQuiz from '@/models/statement/StatementQuiz';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';

const store = useStore();
const router = useRouter();
const route = useRoute();

const quizId = ref<number | null>(null);
const quiz = ref<StatementQuiz | null>(null);
const timer = ref('');

const getQuizByQRCode = async () => {
  store.setLoading();
  if (quizId.value && route.name === 'scan') {
    try {
      quiz.value = await RemoteServices.getQuizByQRCode(quizId.value);

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

const onDecode = async (decodedString: string) => {
  quizId.value = Number(decodedString);
  await getQuizByQRCode();
};

watch(
  () => quiz.value?.timeToAvailability,
  (newVal) => {
    if (!!quiz.value && !newVal) {
      getQuizByQRCode();
    }
    timer.value = milisecondsToHHMMSS(newVal ?? 0);
  }
);
</script>

<style lang="scss" scoped></style>
