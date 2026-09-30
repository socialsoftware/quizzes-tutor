<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="visibleGenerations"
      :search="search"
      :custom-filter="generationFilter"
      :sort-by="[{ key: 'id', order: 'desc' }]"
      :items-per-page="15"
      :items-per-page-options="[15, 30, 50, 100]"
      :mobile-breakpoint="0"
      data-cy="GeneratedQuestions"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-inner-icon="search"
            label="Search"
            class="mx-2"
            data-cy="Search"
          />
          <v-spacer />
          <v-switch
            v-model="onlyOpen"
            label="Only waiting for review"
            color="primary"
            hide-details
            class="mr-4"
            data-cy="OnlyOpen"
          />
          <v-btn color="primary" @click="load">Refresh List</v-btn>
        </v-card-title>
      </template>

      <template v-slot:[`item.question.title`]="{ item }">
        <span class="clickableTitle" data-cy="OpenGenerated" @click="open(getRaw(item))">
          {{ getRaw(item).question.title }}
        </span>
      </template>
      <template v-slot:[`item.status`]="{ item }">
        <v-chip :color="getRaw(item).getStatusColor()" size="small">
          {{ getRaw(item).getStatusName() }}
        </v-chip>
      </template>
      <template v-slot:[`item.needsHumanAttention`]="{ item }">
        <v-chip :color="getRaw(item).needsHumanAttention ? 'orange' : 'green'" size="small">
          {{ getRaw(item).needsHumanAttention ? 'NEEDS ATTENTION' : 'PASSED' }}
        </v-chip>
      </template>
      <template v-slot:no-data>
        No generated questions{{ onlyOpen ? ' waiting for review' : '' }}.
      </template>
    </v-data-table>

    <show-generated-question-dialog
      v-if="current && dialog"
      v-model:dialog="dialog"
      :questionGeneration="current"
      @reviewed="load"
    />
    <footer>
      <v-icon class="mr-2">mouse</v-icon>Left-click on a question's title to read it, review it and
      approve it for your students.
    </footer>
  </v-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import { withV2ColumnWidths } from '@/services/DataTableHeaders';
import QuestionGeneration from '@/models/management/generation/QuestionGeneration';
import ShowGeneratedQuestionDialog from '@/views/teacher/generation/ShowGeneratedQuestionDialog.vue';

const store = useStore();

const generations = ref<QuestionGeneration[]>([]);
const search = ref('');
const onlyOpen = ref(true);
const current = ref<QuestionGeneration | null>(null);
const dialog = ref(false);
const headers: any[] = withV2ColumnWidths(QuestionGeneration.headers);

const getRaw = (item: any): QuestionGeneration => (item as any).raw || item;

const visibleGenerations = computed(() =>
  onlyOpen.value ? generations.value.filter((generation) => generation.isOpen()) : generations.value
);

const generationFilter = (_value: unknown, query: string, item?: unknown) => {
  const generation = getRaw(item);
  const text = `${generation.question?.title} ${generation.question?.content} ${generation.status}`;
  return text.toLowerCase().includes(query.toLowerCase());
};

const load = async () => {
  store.setLoading();
  try {
    generations.value = await RemoteServices.getQuestionGenerations();
    // Keep an open dialog on the fresh copy of its question
    if (current.value) {
      current.value = generations.value.find((generation) => generation.id === current.value!.id) ?? null;
    }
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const open = (generation: QuestionGeneration) => {
  current.value = generation;
  dialog.value = true;
};

watch(dialog, (isOpen) => {
  if (!isOpen) current.value = null;
});

onMounted(load);

defineExpose({ load });
</script>

<style scoped>
.clickableTitle {
  cursor: pointer;
}
.clickableTitle:hover {
  text-decoration: underline;
}
</style>
