<template>
    <v-card class="table">
        <v-data-table
                :headers="headers"
                :custom-filter="customFilter"
                :items="submissions"
                :search="search"
                :sort-by="['question.creationDate']"
                sort-desc
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
                            class="mx-2"
                    />

                    <v-spacer />
                    <v-btn-toggle
                            dense
                            borderless
                            mandatory
                            background-color="primary"
                            color="white"
                    >
                        <v-btn
                                color="primary"
                                @click="filterSubmissions('ALL')"
                        >{{ 'All' }}</v-btn
                        >
                        <v-btn
                                color="primary"
                                @click="filterSubmissions('AVAILABLE')"
                        >{{ 'Available' }}</v-btn
                        >
                        <v-btn
                                color="primary"
                                @click="filterSubmissions('DISABLED')"
                        >{{ 'Disabled' }}</v-btn
                        >
                        <v-btn
                                color="primary"
                                @click="filterSubmissions('REJECTED')"
                        >{{ 'Rejected' }}</v-btn
                        >
                        <v-btn
                                color="primary"
                                @click="filterSubmissions('SUBMITTED')"
                        >{{ 'Submitted' }}</v-btn
                        >
                        <v-btn
                                color="primary"
                                @click="filterSubmissions('IN_REVIEW')"
                        >{{ 'In Review' }}</v-btn
                        >
                    </v-btn-toggle>
                </v-card-title>
            </template>

            <template v-slot:item.question.title="{ item }">
                <div @click="showSubmissionDialog(item)" class="clickableTitle">
                    {{ item.question.title }}
                </div>
            </template>
            <template v-slot:item.name="{ item }">
                <span v-if="!item.name">Anonymous user</span>
                <span v-else>{{ item.name }}</span>
            </template>
            <template v-slot:item.question.status="{ item }">
                <v-chip :color="getStatusColor(item.question.status)" small>
                    <span>{{ item.question.status }}</span>
                </v-chip>
            </template>
            <template v-slot:item.question.topics="{ item }">
                <view-submission-topics :submission="item" :topics="item.question.topics" />
            </template>
            <template v-slot:item.action="{ item }">
                <v-tooltip bottom>
                    <template v-slot:activator="{ on }">
                        <v-icon class="mr-2" v-on="on" @click="showSubmissionDialog(item)"
                        >visibility</v-icon
                        >
                    </template>
                    <span>Show Submission</span>
                </v-tooltip>
            </template>
        </v-data-table>
        <show-submission-dialog
                v-if="currentSubmission"
                v-model="submissionDialog"
                :submission="currentSubmission"
                v-on:close-show-question-dialog="onCloseShowSubmissionDialog"
        />
        <footer>
            <v-icon class="mr-2">mouse</v-icon>Left-click on question's title to view
            submitted question and submission status.
        </footer>
    </v-card>
</template>

<script lang="ts">
  import { Component, Vue, Watch } from 'vue-property-decorator';
  import RemoteServices from '@/services/RemoteServices';
  import ShowSubmissionDialog from '@/views/student/submissions/ShowSubmissionDialog.vue';
  import EditSubmissionTopics from '@/views/student/submissions/EditSubmissionTopics.vue';
  import ViewSubmissionTopics from '@/views/student/submissions/ViewSubmissionTopics.vue';
  import EditSubmissionDialog from '@/views/student/submissions/EditSubmissionDialog.vue';
  import Question from '@/models/management/Question';
  import Submission from '@/models/management/Submission';
  import Topic from '@/models/management/Topic';

  @Component({
    components: {
      'show-submission-dialog': ShowSubmissionDialog,
      'view-submission-topics': ViewSubmissionTopics
    }
  })
  export default class AllSubmissionsView extends Vue {
    submissions: Submission[] = [];
    allSubmissions: Submission[] = [];
    currentSubmission: Submission | null = null;
    submissionDialog: boolean = false;
    search: string = '';

    headers: object = [
      {
        text: 'Actions',
        value: 'action',
        align: 'left',
        width: '5px',
        sortable: false
      },
      { text: 'Title', value: 'question.title', align: 'center', width: '50%' },
      {
        text: 'Submitted by',
        value: 'name',
        align: 'center',
        width: '10%'
      },
      {
        text: 'Status',
        value: 'question.status',
        align: 'center',
        width: '150px'
      },
      {
        text: 'Topics',
        value: 'question.topics',
        align: 'center',
        sortable: false,
        width: '50%%'
      },
      {
        text: 'Creation Date',
        value: 'question.creationDate',
        width: '150px',
        align: 'center'
      }
    ];

    async created() {
      await this.$store.dispatch('loading');
      try {
        [this.submissions] = await Promise.all([
          RemoteServices.getAllStudentsSubmissions()
        ]);
        this.allSubmissions = this.submissions;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }

    customFilter(value: string, search: string, question: Question) {
      // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
      return (
        search != null &&
        JSON.stringify(question)
          .toLowerCase()
          .indexOf(search.toLowerCase()) !== -1
      );
    }

    getStatusColor(status: string) {
      if (status === 'AVAILABLE') return 'green';
      else if (status === 'DISABLED') return 'orange';
      else if (status === 'REJECTED') return 'red';
      else if (status === 'SUBMITTED') return 'yellow';
      else if (status === 'IN_REVIEW') return 'blue';
    }

    showSubmissionDialog(submission: Submission) {
      this.currentSubmission = submission;
      this.submissionDialog = true;
    }

    onCloseShowSubmissionDialog() {
      this.submissionDialog = false;
    }

    filterSubmissions(status: string) {
      this.submissions = status === 'ALL' ?
        this.allSubmissions :
        this.allSubmissions.filter(s => {
        return s.question.status === status;
      });
    }
  }
</script>
