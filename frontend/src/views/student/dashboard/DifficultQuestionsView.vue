<template>
  <v-container v-if="difficultQuestions != null" fluid>
    <h3>Difficult Questions</h3>
    <v-card class="table">
      <v-data-table
        :headers="headers"
        :items="difficultQuestions"
        :sort-by="[{ key: 'percentage', order: 'asc' }]"
        class="elevation-1"
        data-cy="difficultQuestionsTable"
        multi-sort
      >
        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ props: activatorProps }">
              <span
                class="mr-2 action-button"
                data-cy="showStudentViewDialog"
                @click="showStudentViewDialog(item.raw || item)"
                v-bind="activatorProps"
              >
                <v-icon>school</v-icon>
              </span>
            </template>
            <span>Student View</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ props: activatorProps }">
              <span
                class="mr-2 action-button"
                data-cy="deleteDifficultQuestionButton"
                @click="deleteDifficultQuestion(item.raw || item)"
                v-bind="activatorProps"
              >
                <v-icon color="red">delete</v-icon>
              </span>
            </template>
            <span>Delete Difficult Question</span>
          </v-tooltip>
        </template>
      </v-data-table>
    </v-card>

    <student-view-dialog
      v-if="statementQuestion && studentViewDialog"
      v-model:dialog="studentViewDialog"
      :statementQuestion="statementQuestion"
      v-on:close-show-question-dialog="
        studentViewDialog = false;
        statementQuestion = null;
      "
    />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import DifficultQuestion from '@/models/dashboard/DifficultQuestion';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StudentViewDialog from '@/views/teacher/questions/StudentViewDialog.vue';

const props = defineProps<{
  dashboardId: number;
}>();

const store = useStore();

const difficultQuestions = ref<DifficultQuestion[]>([]);
const statementQuestion = ref<StatementQuestion | null>(null);
const studentViewDialog = ref(false);

const headers: any = [
  {
    title: 'Actions',
    key: 'action',
    align: 'start',
    width: '5px',
    sortable: false,
  },
  {
    title: 'Question',
    key: 'questionDto.content',
    align: 'start',
    width: '500px',
    sortable: false,
  },
  { title: 'Percentage', key: 'percentage', align: 'center', width: '5px' },
];

onMounted(async () => {
  store.setLoading();
  try {
    difficultQuestions.value = await RemoteServices.updateDifficultQuestions(
      props.dashboardId
    );
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

const showStudentViewDialog = async (difficultQuestion: DifficultQuestion) => {
  if (difficultQuestion.questionDto.id) {
    try {
      statementQuestion.value = await RemoteServices.getStatementQuestion(
        difficultQuestion.questionDto.id
      );
      studentViewDialog.value = true;
    } catch (error) {
      store.setError(error as string);
    }
  }
};

const deleteDifficultQuestion = async (toDeleteDifficultQuestion: DifficultQuestion) => {
  try {
    await RemoteServices.deleteDifficultQuestion(toDeleteDifficultQuestion.id);
    difficultQuestions.value = difficultQuestions.value.filter(
      (difficultQuestion) => difficultQuestion.id != toDeleteDifficultQuestion.id
    );
  } catch (error) {
    store.setError(error as string);
  }
};
</script>
