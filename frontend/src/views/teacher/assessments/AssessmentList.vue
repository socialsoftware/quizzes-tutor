<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="assessments"
      :search="search"
      :sort-by="['sequence']"
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
          <v-btn color="primary" dark @click="$emit('newAssessment')"
            >New Assessment</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:[`item.status`]="{ item }">
        <v-select
          v-model="item.status"
          :items="statusList"
          dense
          @change="setStatus(item.id, item.status)"
        >
          <template v-slot:selection="{ item }">
            <v-chip :color="getStatusColor(item)" small>
              <span>{{ item }}</span>
            </v-chip>
          </template>
        </v-select>
      </template>
      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              v-on="on"
              @click="showQuestionsDialog(item.id)"
              >visibility</v-icon
            >
          </template>
          <span>Show Questions</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              v-on="on"
              @click="editAssessment(item.id)"
              >edit</v-icon
            >
          </template>
          <span>Edit Assessment</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              v-on="on"
              @click="deleteAssessment(item.id)"
              color="red"
              >delete</v-icon
            >
          </template>
          <span>Delete Assessment</span>
        </v-tooltip>
      </template>
      <template v-slot:[`item.title`]="{ item }">
        <div
          @click="showQuestionsDialog(item.id)"
          @contextmenu="editAssessment(item.id, $event)"
          class="clickableTitle"
        >
          {{ item.title }}
        </div>
      </template>
    </v-data-table>
    <footer>
      <v-icon class="mr-2 action-button">mouse</v-icon>Right-click on
      assessment's title to edit it.
    </footer>

    <show-question-list-dialog
      :dialog="questionsDialog"
      :questions="questionsToShow"
      v-on:close="onCloseQuestionsDialog"
    ></show-question-list-dialog>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import Assessment from '@/models/management/Assessment';
import TopicConjunction from '@/models/management/TopicConjunction';
import { _ } from 'vue-underscore';
import Question from '@/models/management/Question';
import ShowQuestionListDialog from '@/views/teacher/questions/ShowQuestionListDialog.vue';

@Component({
  components: { ShowQuestionListDialog },
})
export default class AssessmentList extends Vue {
  @Prop({ type: Array, required: true }) readonly assessments!: Assessment[];
  assessment: Assessment | null = null;
  search: string = '';
  statusList = ['DISABLED', 'AVAILABLE', 'REMOVED'];
  questionsDialog: boolean = false;
  questionsToShow: Question[] = [];
  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '5px',
    },
    { text: 'Order', value: 'sequence', align: 'center', width: '5px' },
    { text: 'Title', value: 'title', width: '80%', align: 'left' },
    {
      text: 'Number of questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '5px',
    },
    { text: 'Status', value: 'status', align: 'center', width: '5px' },
  ];

  async setStatus(assessmentId: number, status: string) {
    try {
      await RemoteServices.setAssessmentStatus(assessmentId, status);
      let assessment = this.assessments.find(
        (assessment) => assessment.id === assessmentId
      );
      if (assessment) {
        assessment.status = status;
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  editAssessment(assessmentId: number, e?: Event) {
    if (e) e.preventDefault();
    this.$emit('editAssessment', assessmentId);
  }

  async deleteAssessment(assessmentId: number) {
    if (confirm('Are you sure you want to delete this assessment?')) {
      try {
        await RemoteServices.deleteAssessment(assessmentId);
        this.$emit('deleteAssessment', assessmentId);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  getStatusColor(status: string) {
    if (status === 'REMOVED') return 'red';
    else if (status === 'DISABLED') return 'orange';
    else return 'green';
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  async showQuestionsDialog(assessmentId: number) {
    try {
      this.questionsToShow = await RemoteServices.getAssessmentQuestions(
        assessmentId
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    this.questionsDialog = true;
  }

  onCloseQuestionsDialog() {
    this.questionsDialog = false;
    this.questionsToShow = [];
  }
}
</script>

<style lang="scss" scoped />
