<template>
  <v-dialog :model-value="dialog" max-width="960" scrollable @update:model-value="$emit('update:dialog', $event)">
    <v-card class="speed-report" data-cy="SpeedReportDialog">
      <v-card-title>Answer times over the quizzes of the course</v-card-title>
      <v-card-text>
        <p class="text-body-2 mb-3">
          Students whose answer times look unlike a person reading: too fast to read the question, almost the same time on
          every question, or one long wait followed by quick answers. A pattern that <b>repeats from quiz to quiz</b> weighs
          much more than one quiz. These are signs to look at, <b>never proof</b>: talk to the student before concluding anything.
        </p>

        <div v-if="loading" class="text-center py-6"><v-progress-circular indeterminate color="primary" /></div>
        <template v-else-if="report">
          <p class="text-caption text-medium-emphasis mb-2" data-cy="SpeedReportSummary">
            {{ report.quizzesAnalysed }} quiz(zes) analysed, {{ report.users.length }} student(s) listed.
          </p>
          <p v-if="report.users.length === 0" class="text-medium-emphasis" data-cy="SpeedReportEmpty">
            No student has answer times that stand out.
          </p>
          <v-expansion-panels v-else variant="accordion">
            <v-expansion-panel v-for="student in report.users" :key="student.userInfo.user_id" data-cy="SpeedStudent">
              <v-expansion-panel-title>
                <div class="d-flex align-center flex-wrap ga-2 w-100">
                  <span class="font-weight-medium">{{ student.userInfo.name }}</span>
                  <span class="text-caption text-medium-emphasis">{{ student.userInfo.username }}</span>
                  <v-spacer />
                  <v-chip size="small" :color="student.flaggedQuizzes >= 2 ? 'error' : student.flaggedQuizzes === 1 ? 'warning' : undefined" data-cy="FlaggedQuizzes">
                    flagged in {{ student.flaggedQuizzes }} of {{ student.quizzesAnalysed }} quizzes
                  </v-chip>
                  <v-chip size="small" data-cy="TotalPoints">{{ student.totalPoints }} point(s)</v-chip>
                  <v-chip v-if="student.blockedOpenings > 0" size="small" color="warning" data-cy="RefusedOpenings">
                    {{ student.blockedOpenings }} refused opening(s)
                  </v-chip>
                </div>
              </v-expansion-panel-title>
              <v-expansion-panel-text>
                <div v-for="quiz in student.quizzes" :key="quiz.quizId" class="quiz-result" data-cy="SpeedQuiz">
                  <div class="d-flex align-center flex-wrap ga-2">
                    <span class="font-weight-medium">{{ quiz.title }}</span>
                    <v-chip size="x-small" :color="colorOf(quiz.level)">{{ quiz.level }}</v-chip>
                    <span class="text-caption text-medium-emphasis">
                      {{ quiz.answered }} answers, {{ quiz.tooFast }} too fast to read<span v-if="quiz.meanSeconds !== null">, {{ quiz.meanSeconds }} s on average</span>
                    </span>
                  </div>
                  <ul v-if="quiz.reasons.length > 0" class="text-body-2">
                    <li v-for="reason in quiz.reasons" :key="reason">{{ reason }}</li>
                  </ul>
                </div>
              </v-expansion-panel-text>
            </v-expansion-panel>
          </v-expansion-panels>
        </template>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" data-cy="CloseSpeedReport" @click="$emit('update:dialog', false)">Close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import { ExecutionFraudSpeed } from '@/models/management/fraud/ExecutionFraudSpeed';

const props = defineProps<{ dialog: boolean }>();
const emit = defineEmits<{ (e: 'update:dialog', value: boolean): void }>();

const store = useStore();
const report = ref<ExecutionFraudSpeed | null>(null);
const loading = ref(false);

const colorOf = (level: string) => (level === 'HIGH' ? 'error' : level === 'MEDIUM' ? 'warning' : undefined);

const load = async () => {
  loading.value = true;
  report.value = null;
  try {
    report.value = await RemoteServices.getExecutionSpeedReport();
  } catch (error) {
    store.setError(error as string);
    emit('update:dialog', false);
  }
  loading.value = false;
};

watch(
  () => props.dialog,
  (open) => {
    if (open) load();
  },
  { immediate: true }
);
</script>

<style lang="scss" scoped>
.speed-report {
  text-align: left;
}

.quiz-result {
  padding: 6px 0;
  border-bottom: 1px dashed rgba(0, 0, 0, 0.1);
}
</style>
