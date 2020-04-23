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
      <template v-slot:item.status="{ item }">
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
      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
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
              large
              class="mr-2"
              v-on="on"
              @click="deleteAssessment(item.id)"
              color="red"
              >delete</v-icon
            >
          </template>
          <span>Delete Assessment</span>
        </v-tooltip>
      </template>
      <template v-slot:item.title="{ item }">
        <p
          @contextmenu="editAssessment(item.id, $event)"
          style="cursor: pointer"
        >
          {{ item.title }}
        </p>
      </template>
    </v-data-table>
    <footer>
      <v-icon class="mr-2">mouse</v-icon>Right-click on assessment's title to
      edit it.
    </footer>

    <v-dialog
      v-model="dialog"
      @keydown.esc="closeAssessment"
      fullscreen
      hide-overlay
      max-width="1000px"
    >
      <v-card v-if="assessment">
        <v-toolbar dark color="primary">
          <v-toolbar-title>{{ assessment.title }}</v-toolbar-title>
          <div class="flex-grow-1"></div>
          <v-toolbar-items>
            <v-btn dark color="primary" @click="closeAssessment">Close</v-btn>
          </v-toolbar-items>
        </v-toolbar>

        <v-card-text>
          <ol>
            <li
              v-for="question in assessment.questions"
              :key="question.sequence"
              class="text-left"
            >
              <span
                v-html="convertMarkDown(question.content, question.image)"
              />
              <ul>
                <li v-for="option in question.options" :key="option.number">
                  <span
                    v-html="convertMarkDown(option.content)"
                    v-bind:class="[option.correct ? 'font-weight-bold' : '']"
                  />
                </li>
              </ul>
              <br />
            </li>
          </ol>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn dark color="primary" @click="closeAssessment">close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import Assessment from '@/models/management/Assessment';

@Component
export default class AssessmentList extends Vue {
  @Prop({ type: Array, required: true }) readonly assessments!: Assessment[];
  assessment: Assessment | null = null;
  search: string = '';
  statusList = ['DISABLED', 'AVAILABLE', 'REMOVED'];
  dialog: boolean = false;
  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '15%'
    },
    { text: 'Order', value: 'sequence', align: 'center', width: '95px' },
    { text: 'Title', value: 'title', align: 'left' },
    {
      text: 'Number of questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '7%'
    },
    { text: 'Status', value: 'status', align: 'center', width: '7%' }
  ];

  closeAssessment() {
    this.dialog = false;
    this.assessment = null;
  }

  async setStatus(assessmentId: number, status: string) {
    try {
      await RemoteServices.setAssessmentStatus(assessmentId, status);
      let assessment = this.assessments.find(
        assessment => assessment.id === assessmentId
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
}
</script>

<style lang="scss" scoped />
