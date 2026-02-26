<template>
  <v-container v-if="failedAnswers != null" fluid>
    <h3>Failed Answers</h3>
    <v-card class="table">
      <v-data-table
        :headers="headers"
        :items="failedAnswers"
        :sort-by="[{ key: 'collected', order: 'asc' }]"
        class="elevation-1"
        data-cy="failedAnswersTable"
        multi-sort
      >
        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ props: activatorProps }">
              <v-icon
                class="mr-2 action-button"
                data-cy="showStudentViewDialog"
                @click="showStudentViewDialog(item)"
                v-bind="activatorProps"
                >school
              </v-icon>
            </template>
            <span>Student View</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ props: activatorProps }">
              <v-icon
                class="mr-2 action-button"
                color="red"
                data-cy="deleteFailedAnswerButton"
                @click="deleteFailedAnswer(item)"
                v-bind="activatorProps"
                >delete
              </v-icon>
            </template>
            <span>Delete Failed Answer</span>
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
import StatementQuestion from '@/models/statement/StatementQuestion';
import StudentViewDialog from '@/views/teacher/questions/StudentViewDialog.vue';
import FailedAnswer from '@/models/dashboard/FailedAnswer';

const props = defineProps<{
  dashboardId: number;
}>();

const store = useStore();

const failedAnswers = ref<FailedAnswer[]>([]);
const statementQuestion = ref<StatementQuestion | null>(null);
const studentViewDialog = ref(false);

const headers: any = [
  {
    title: 'Actions',
    value: 'action',
    align: 'start',
    width: '5px',
    sortable: false,
  },
  {
    title: 'Question',
    value: 'questionAnswerDto.question.content',
    align: 'start',
    sortable: false,
    width: '500px',
  },
  { title: 'Answered', value: 'answered', align: 'center', width: '5px' },
  { title: 'Collected', value: 'collected', align: 'center', width: '10px' },
];

onMounted(async () => {
  store.setLoading();
  try {
    failedAnswers.value = await RemoteServices.updateFailedAnswers(props.dashboardId);
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

const showStudentViewDialog = async (failedAnswer: FailedAnswer) => {
  if (failedAnswer.questionAnswerDto.question.id) {
    try {
      statementQuestion.value = await RemoteServices.getStatementQuestion(
        failedAnswer.questionAnswerDto.question.id
      );
      studentViewDialog.value = true;
    } catch (error) {
      store.setError(error as string);
    }
  }
};

const deleteFailedAnswer = async (toDeleteFailedAnswer: FailedAnswer) => {
  try {
    await RemoteServices.deleteFailedAnswer(toDeleteFailedAnswer.id);
    failedAnswers.value = failedAnswers.value.filter(
      (failedAnswer) => failedAnswer.id != toDeleteFailedAnswer.id
    );
  } catch (error) {
    store.setError(error as string);
  }
};
</script>
