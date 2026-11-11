<template>
  <v-card class="table generation-usage" data-cy="GenerationUsage">
    <v-card-title>Question generation usage</v-card-title>
    <v-card-subtitle class="text-wrap">
      Requests made by each course on each day, and in brackets the questions they asked for.
    </v-card-subtitle>

    <v-card-text>
      <div class="d-flex align-center mb-3">
        <v-select
          v-model="days"
          :items="PERIODS"
          label="Period"
          density="compact"
          hide-details
          class="period"
          data-cy="UsagePeriod"
          @update:model-value="load"
        />
        <v-btn class="ml-2" icon="fas fa-sync" size="small" variant="text" title="Read again" :loading="loading" @click="load" />
      </div>

      <p v-if="usage && usage.courses.length === 0" class="text-medium-emphasis" data-cy="UsageEmpty">
        Nobody asked for questions in this period.
      </p>
      <v-table v-else-if="usage" density="compact" data-cy="UsageTable">
        <thead>
          <tr>
            <th class="text-left">Course</th>
            <th v-for="day in usage.days" :key="day" class="text-center day" :title="day">{{ shortDay(day) }}</th>
            <th class="text-center">Total</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in usage.courses" :key="row.courseExecutionId" data-cy="UsageRow">
            <td class="text-left">
              {{ row.course }}
              <span v-if="row.academicTerm" class="text-medium-emphasis">{{ row.academicTerm }}</span>
            </td>
            <td v-for="(count, index) in row.requests" :key="index" class="text-center" data-cy="UsageCell">
              <span v-if="count > 0">{{ count }} <span class="text-medium-emphasis">({{ row.questions[index] }})</span></span>
              <span v-else class="text-disabled">·</span>
            </td>
            <td class="text-center font-weight-bold" data-cy="UsageTotal">
              {{ row.totalRequests }} <span class="text-medium-emphasis">({{ row.totalQuestions }})</span>
            </td>
          </tr>
          <tr class="font-weight-bold">
            <td class="text-left">All courses</td>
            <td v-for="(total, index) in perDay" :key="index" class="text-center" data-cy="UsageDayTotal">{{ total }}</td>
            <td class="text-center">{{ perDay.reduce((sum, count) => sum + count, 0) }}</td>
          </tr>
        </tbody>
      </v-table>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import { GenerationUsage } from '@/models/admin/GenerationUsage';

const PERIODS = [
  { title: 'Last 7 days', value: 7 },
  { title: 'Last 10 days', value: 10 },
  { title: 'Last 30 days', value: 30 },
];

const store = useStore();
const days = ref(10);
const usage = ref<GenerationUsage | null>(null);
const loading = ref(false);

// 2026-11-05 -> 11-05
const shortDay = (day: string) => day.slice(5);

// Requests of all the courses on each day
const perDay = computed(() =>
  (usage.value?.days ?? []).map((_, index) => (usage.value?.courses ?? []).reduce((sum, row) => sum + row.requests[index], 0))
);

const load = async () => {
  loading.value = true;
  try {
    usage.value = await RemoteServices.getGenerationUsage(days.value);
  } catch (error) {
    store.setError(error as string);
  }
  loading.value = false;
};

onMounted(load);
</script>

<style lang="scss" scoped>
.generation-usage {
  text-align: left;
}

.period {
  max-width: 220px;
}

.day {
  white-space: nowrap;
}
</style>
