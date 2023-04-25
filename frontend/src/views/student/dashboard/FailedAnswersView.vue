<template>
  <v-container v-if="failedAnswers != null" fluid>
    <h3>Failed Answers</h3>
    <v-card class="table">
      <v-data-table
        :headers="headers"
        :items="failedAnswers"
        :sort-by="['collected']"
        :sort-desc="[false]"
        class="elevation-1"
        data-cy="failedAnswersTable"
        multi-sort
      >
        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                data-cy="showStudentViewDialog"
                @click="showStudentViewDialog(item)"
                v-on="on"
                >school
              </v-icon>
            </template>
            <span>Student View</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="red"
                data-cy="deleteFailedAnswerButton"
                @click="deleteFailedAnswer(item)"
                v-on="on"
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
      v-model="studentViewDialog"
      :statementQuestion="statementQuestion"
      v-on:close-show-question-dialog="
        studentViewDialog = false;
        statementQuestion = null;
      "
    />
  </v-container>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StudentViewDialog from '@/views/teacher/questions/StudentViewDialog.vue';
import FailedAnswer from '@/models/dashboard/FailedAnswer';

@Component({
  components: {
    'student-view-dialog': StudentViewDialog,
  },
})
export default class FailedAnswersView extends Vue {
  @Prop() dashboardId!: number;

  failedAnswers: FailedAnswer[] = [];
  statementQuestion: StatementQuestion | null = null;
  studentViewDialog: boolean = false;

  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false,
    },
    {
      text: 'Question',
      value: 'questionAnswerDto.question.content',
      align: 'start',
      sortable: false,
      width: '500px',
    },
    { text: 'Answered', value: 'answered', align: 'center', width: '5px' },
    { text: 'Collected', value: 'collected', align: 'center', width: '10px' },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.failedAnswers = await RemoteServices.updateFailedAnswers(
        this.dashboardId
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async showStudentViewDialog(failedAnswer: FailedAnswer) {
    if (failedAnswer.questionAnswerDto.question.id) {
      try {
        this.statementQuestion = await RemoteServices.getStatementQuestion(
          failedAnswer.questionAnswerDto.question.id
        );
        this.studentViewDialog = true;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async deleteFailedAnswer(toDeleteFailedAnswer: FailedAnswer) {
    try {
      await RemoteServices.deleteFailedAnswer(toDeleteFailedAnswer.id);
      this.failedAnswers = this.failedAnswers.filter(
        (failedAnswer) => failedAnswer.id != toDeleteFailedAnswer.id
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
