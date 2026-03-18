<template>
  <v-card class="table">
    <v-card-title>
      <v-text-field
        v-model="search"
        append-icon="search"
        label="Search Student"
        data-cy="Search"
        class="mx-2"
      />

      <v-spacer />
      <v-btn
        color="primary"
        class="mr-2"
        dark
        @click="getUserQuestionSubmissionsInfo"
        >Refresh List</v-btn
      >
      <v-btn
        v-if="store.isTeacher"
        color="primary"
        class="mr-2"
        dark
        to="/management/submissions"
        >Sort by Date</v-btn
      >
    </v-card-title>
    <v-data-table
      :headers="headers"
      :items="userQuestionSubmissionsInfo"
      :search="search"
      item-value="name"
      show-expand
      multi-sort
      :mobile-breakpoint="0"
      v-model:items-per-page="itemsPerPage"
      :items-per-page-options="[
        { key: 15, title: '15' },
        { key: 30, title: '30' },
        { key: 50, title: '50' },
        { key: 100, title: '100' },
        { key: -1, title: 'All' }
      ]"
    >
      <template v-slot:item="{ item: displayItem, toggleExpand, isExpanded }">
        <tr
          v-bind:class="{ clickableRow: hasSubmissions(displayItem) }"
          @click="() => { if (hasSubmissions(displayItem)) toggleExpand(displayItem as any) }"
        >
          <td>
            <v-icon v-if="!isExpanded(displayItem as any)">fa-angle-down</v-icon>
            <v-icon v-else>fa-angle-up</v-icon>
          </td>
          <td>{{ getRaw(displayItem)?.name }}</td>
          <td>
            <v-chip :color="getRaw(displayItem)?.numQuestionSubmissions?.approved?.color">{{
              getRaw(displayItem)?.numQuestionSubmissions?.approved?.num
            }}</v-chip>
          </td>
          <td>
            <v-chip :color="getRaw(displayItem)?.numQuestionSubmissions?.rejected?.color">{{
              getRaw(displayItem)?.numQuestionSubmissions?.rejected?.num
            }}</v-chip>
          </td>
          <td>
            <v-chip :color="getRaw(displayItem)?.numQuestionSubmissions?.in_review?.color">{{
              getRaw(displayItem)?.numQuestionSubmissions?.in_review?.num
            }}</v-chip>
          </td>
          <td>
            <v-chip :color="getRaw(displayItem)?.numQuestionSubmissions?.in_revision?.color">{{
              getRaw(displayItem)?.numQuestionSubmissions?.in_revision?.num
            }}</v-chip>
          </td>
          <td>
            <v-chip>{{ getRaw(displayItem)?.totalQuestionSubmissions }}</v-chip>
          </td>
        </tr>
      </template>
      <template v-slot:expanded-row="{ columns, item: displayItem }">
        <tr>
          <td :colspan="columns.length">
            <v-data-table
              :headers="studentHeaders"
              :items="getRaw(displayItem).questionSubmissions"
              :sort-by="[{ key: 'question.creationDate', order: 'desc' }]"
              hide-default-footer
              class="studentSubmissions"
            >
              <template v-slot:item="{ item: subItem }">
                <tr>
                  <td>
                    <v-tooltip bottom>
                      <template v-slot:activator="{ props }">
                        <v-icon
                          class="mr-2 action-button"
                          v-bind="props"
                          @click="showQuestionSubmissionDialogAction(getSubRaw(subItem))"
                          data-cy="ViewSubmission"
                          >fa-comments</v-icon
                        >
                      </template>
                      <span>View Submission</span>
                    </v-tooltip>
                  </td>
                  <td>
                    <div
                      @click="showQuestionSubmissionDialogAction(getSubRaw(subItem))"
                      class="clickableTitle"
                    >
                      {{ getSubRaw(subItem).question.title }}
                    </div>
                  </td>
                  <td>
                    <v-chip :color="getSubRaw(subItem).getStatusColor()" size="small">
                      <span>{{ getSubRaw(subItem).getStatus() }}</span>
                    </v-chip>
                  </td>
                  <td>
                    <edit-question-submission-topics
                      :questionSubmission="getSubRaw(subItem)"
                      :topics="topics"
                      :readOnly="true"
                    />
                  </td>
                  <td>{{ getSubRaw(subItem).question.creationDate }}</td>
                </tr>
              </template>
            </v-data-table>
          </td>
        </tr>
      </template>
    </v-data-table>
    <show-question-submission-dialog
      v-if="currentQuestionSubmission && questionSubmissionDialog"
      :dialog="questionSubmissionDialog"
      @update:dialog="questionSubmissionDialog = $event"
      :questionSubmission="currentQuestionSubmission"
    />
    <footer>
      <v-icon class="mr-2 action-button">mouse</v-icon>Left-click on student to
      see their question submissions
    </footer>
  </v-card>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { useStore } from '@/store';
import UserQuestionSubmissionInfo from '@/models/management/UserQuestionSubmissionInfo';
import RemoteServices from '@/services/RemoteServices';
import QuestionSubmission from '@/models/management/QuestionSubmission';
import ShowQuestionSubmissionDialog from '@/views/questionsubmission/ShowQuestionSubmissionDialog.vue';
import Topic from '@/models/management/Topic';
import EditQuestionSubmissionTopics from '@/views/questionsubmission/EditQuestionSubmissionTopics.vue';

const store = useStore();

const userQuestionSubmissionsInfo = ref<UserQuestionSubmissionInfo[]>([]);
const topics = ref<Topic[]>([]);
const currentQuestionSubmission = ref<QuestionSubmission | null>(null);
const questionSubmissionDialog = ref<boolean>(false);
const search = ref<string>('');

const getRaw = (item: any): UserQuestionSubmissionInfo => {
  return item.raw || item;
};

const hasSubmissions = (item: any): boolean => {
  return (getRaw(item)?.totalQuestionSubmissions || 0) > 0;
};

const getSubRaw = (item: any): QuestionSubmission => {
  return item.raw || item;
};

const processHeaders = (arr: any[]) => {
  return arr.map((h: any) => ({
    ...h,
    title: h.text || h.title,
    key: h.value || h.key,
    align: h.align === 'left' ? 'start' : h.align === 'right' ? 'end' : h.align
  }));
};

const studentHeaders = ref<any[]>(processHeaders(QuestionSubmission.questionSubmissionHeader.slice()));
const itemsPerPage = ref<number>(15);

const headers = ref<any[]>([
  {
    title: 'Student',
    key: 'name',
    align: 'center',
    width: '50%',
  },
  {
    title: 'Approved',
    key: 'numApprovedQuestionSubmissions',
    align: 'center',
    width: '10%',
  },
  {
    title: 'Rejected',
    key: 'numRejectedQuestionSubmissions',
    align: 'center',
    width: '10%',
  },
  {
    title: 'In Review',
    key: 'numInReviewQuestionSubmissions',
    align: 'center',
    width: '10%',
  },
  {
    title: 'In Revision',
    key: 'numInRevisionQuestionSubmissions',
    align: 'center',
    width: '10%',
  },
  {
    title: 'Total',
    key: 'totalQuestionSubmissions',
    align: 'center',
    width: '10%',
  },
]);

onMounted(async () => {
  await getUserQuestionSubmissionsInfo();
});

watch(questionSubmissionDialog, async (newVal) => {
  if (!newVal) {
    await getUserQuestionSubmissionsInfo();
  }
});

const getUserQuestionSubmissionsInfo = async () => {
  store.setLoading();
  try {
    const res = await Promise.all([
      RemoteServices.getAllStudentsSubmissionsInfo(),
      RemoteServices.getTopics(),
    ]);
    userQuestionSubmissionsInfo.value = res[0];
    topics.value = res[1];
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const showQuestionSubmissionDialogAction = async (questionSubmission: QuestionSubmission) => {
  currentQuestionSubmission.value = questionSubmission;
  questionSubmissionDialog.value = true;
};

</script>
<style lang="scss">
.clickableRow {
  cursor: pointer;
}
.studentSubmissions {
  border: 1px lightgrey solid;
}
</style>
