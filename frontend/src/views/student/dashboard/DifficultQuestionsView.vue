<template>
  <v-container v-if="difficultQuestions != null" fluid>
    <h3>Difficult Questions</h3>
    <v-card class="table">
      <v-container>
        <v-row>
          <v-col><h2>Difficult Questions</h2></v-col>
          <v-col class="text-right">
            <v-btn
              color="primary"
              dark
              data-cy="refreshDifficultQuestionsMenuButton"
              @click="refresh"
              >Refresh
            </v-btn>
          </v-col>
        </v-row>
      </v-container>
      <v-data-table
        :headers="headers"
        :items="difficultQuestions"
        :sort-by="['percentage']"
        :sort-desc="[false]"
        class="elevation-1"
        data-cy="difficultQuestionsTable"
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
                data-cy="deleteDifficultQuestionButton"
                @click="deleteDifficultQuestion(item)"
                v-on="on"
                >delete
              </v-icon>
            </template>
            <span>Delete Difficult Question</span>
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
import DifficultQuestion from '@/models/dashboard/DifficultQuestion';
import Question from '@/models/management/Question';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StudentViewDialog from '@/views/teacher/questions/StudentViewDialog.vue';

@Component({
  components: {
    'student-view-dialog': StudentViewDialog,
  },
})
export default class DifficultQuestionsView extends Vue {
  @Prop() readonly dashboardId!: number;

  difficultQuestions: DifficultQuestion[] = [];
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
      value: 'questionDto.content',
      align: 'start',
      width: '500px',
      sortable: false,
    },
    { text: 'Percentage', value: 'percentage', align: 'center', width: '5px' },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.difficultQuestions = await RemoteServices.getDifficultQuestions(
        this.dashboardId
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async refresh() {
    await this.$store.dispatch('loading');
    try {
      let result = await RemoteServices.updateDifficultQuestions(
        this.dashboardId
      );
      this.difficultQuestions = result.difficultQuestions;
      this.$emit('refresh', result.lastCheckDifficultQuestions);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async showStudentViewDialog(difficultQuestion: DifficultQuestion) {
    if (difficultQuestion.questionDto.id) {
      try {
        this.statementQuestion = await RemoteServices.getStatementQuestion(
          difficultQuestion.questionDto.id
        );
        this.studentViewDialog = true;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async deleteDifficultQuestion(toDeleteDifficultQuestion: DifficultQuestion) {
    try {
      await RemoteServices.deleteDifficultQuestion(
        toDeleteDifficultQuestion.id
      );
      this.difficultQuestions = this.difficultQuestions.filter(
        (difficultQuestion) =>
          difficultQuestion.id != toDeleteDifficultQuestion.id
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
