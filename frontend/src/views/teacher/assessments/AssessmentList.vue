<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="assessments"
      :search="search"
      :sort-by="[{ key: 'sequence' }]"
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-4"
          />

          <v-spacer />
          <v-btn color="primary" class="text-white" @click="$emit('newAssessment')"
            >New Assessment</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:[`item.status`]="{ item }">
        <v-select
          v-model="item.status"
          :items="statusList"
          dense
          hide-details
          variant="outlined"
          @update:model-value="setStatus((item as any).raw.id as number, item.status)"
        >
          <template v-slot:selection="{ item: selectionItem }">
            <v-chip :color="getStatusColor((selectionItem as any).title)" small>
              <span>{{ (selectionItem as any).title }}</span>
            </v-chip>
          </template>
        </v-select>
      </template>
      <template v-slot:[`item.action`]="{ item }">
        <v-icon
          icon="mdi-eye"
          class="mr-2 action-button"
          @click="showQuestionsDialog((item as any).raw.id as number)"
        >
          <v-tooltip activator="parent" location="bottom">Show Questions</v-tooltip>
        </v-icon>
        <v-icon
          icon="mdi-pencil"
          class="mr-2 action-button"
          @click="editAssessment((item as any).raw.id as number)"
        >
          <v-tooltip activator="parent" location="bottom">Edit Assessment</v-tooltip>
        </v-icon>
        <v-icon
          icon="mdi-delete"
          class="mr-2 action-button"
          @click="deleteAssessment((item as any).raw.id as number)"
          color="red"
        >
          <v-tooltip activator="parent" location="bottom">Delete Assessment</v-tooltip>
        </v-icon>
      </template>
      <template v-slot:[`item.title`]="{ item }">
        <div
          @click="showQuestionsDialog((item as any).raw.id as number)"
          @contextmenu="editAssessment((item as any).raw.id as number, $event)"
          class="clickableTitle"
        >
          {{ item.title }}
        </div>
      </template>
    </v-data-table>
    <footer>
      <v-icon icon="mdi-mouse" class="mr-2 action-button" />Right-click on
      assessment's title to edit it.
    </footer>

    <show-question-list-dialog
      :dialog="questionsDialog"
      :questions="questionsToShow"
      v-on:close="onCloseQuestionsDialog"
    ></show-question-list-dialog>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDown as convertMarkDownService } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import Assessment from '@/models/management/Assessment';
import Question from '@/models/management/Question';
import ShowQuestionListDialog from '@/views/teacher/questions/ShowQuestionListDialog.vue';

const props = defineProps<{
  assessments: Assessment[];
}>();

const emit = defineEmits(['editAssessment', 'deleteAssessment', 'newAssessment']);
const store = useStore();

const search = ref('');
const statusList = ['DISABLED', 'AVAILABLE', 'REMOVED'];
const questionsDialog = ref(false);
const questionsToShow = ref<Question[]>([]);

const headers = [
  {
    title: 'Actions',
    key: 'action',
    align: 'start',
    sortable: false,
    width: '10%',
  },
  { title: 'Order', key: 'sequence', align: 'center', width: '10%' },
  { title: 'Title', key: 'title', width: '50%', align: 'start' },
  {
    title: 'Number of questions',
    key: 'numberOfQuestions',
    align: 'center',
    width: '15%',
  },
  { title: 'Status', key: 'status', align: 'center', width: '15%' },
] as any;

const setStatus = async (assessmentId: number, status: string) => {
  try {
    await RemoteServices.setAssessmentStatus(assessmentId, status);
    let assessment = props.assessments.find(
      (a) => a.id === assessmentId
    );
    if (assessment) {
      assessment.status = status;
    }
  } catch (error) {
    store.setError(error as string);
  }
};

const editAssessment = (assessmentId: number, e?: Event) => {
  if (e) e.preventDefault();
  emit('editAssessment', assessmentId);
};

const deleteAssessment = async (assessmentId: number) => {
  if (confirm('Are you sure you want to delete this assessment?')) {
    try {
      await RemoteServices.deleteAssessment(assessmentId);
      emit('deleteAssessment', assessmentId);
    } catch (error) {
      store.setError(error as string);
    }
  }
};

const getStatusColor = (status: string) => {
  if (status === 'REMOVED') return 'red';
  else if (status === 'DISABLED') return 'orange';
  else return 'green';
};

const convertMarkDown = (title: string, image: Image | null = null): string => {
  return convertMarkDownService(title, image);
};

const showQuestionsDialog = async (assessmentId: number) => {
  store.setLoading();
  try {
    questionsToShow.value = await RemoteServices.getAssessmentQuestions(
      assessmentId
    );
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
  questionsDialog.value = true;
};

const onCloseQuestionsDialog = () => {
  questionsDialog.value = false;
  questionsToShow.value = [];
};
</script>

<style lang="scss" scoped />
