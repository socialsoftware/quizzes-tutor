<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="questionSubmissions"
      :search="search"
      :sort-by="[{ key: 'question.creationDate', order: 'desc' }]"
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
            data-cy="Search"
            class="mx-2"
          />

          <v-spacer />
          <v-btn color="primary" dark @click="getQuestionSubmissions"
            >Refresh List</v-btn
          ><v-btn
            v-if="store.isTeacher"
            color="primary"
            dark
            to="/management/submissions/students"
            >Sort by Students</v-btn
          ><v-btn
            v-if="store.isStudent"
            color="primary"
            dark
            @click="newSubmission"
            data-cy="NewSubmission"
            >New Submission</v-btn
          >
        </v-card-title>
      </template>

      <template v-slot:item="{ item: displayItem }">
        <tr v-bind:class="{ unread: hasUnreadReviews(getRaw(displayItem)) }">
          <td id="actions">
            <v-tooltip bottom>
              <template v-slot:activator="{ props }">
                <v-icon
                  v-if="hasUnreadReviews(getRaw(displayItem))"
                  class="unread-icon action-button"
                  v-bind="props"
                  @click="showQuestionSubmissionDialog(getRaw(displayItem))"
                  data-cy="ViewSubmission"
                  color="white"
                  >fa-comment-dots</v-icon
                ><v-icon
                  v-else
                  class="mr-2 action-button"
                  v-bind="props"
                  @click="showQuestionSubmissionDialog(getRaw(displayItem))"
                  data-cy="ViewSubmission"
                  >fa-comments</v-icon
                >
              </template>
              <span>View Submission</span>
            </v-tooltip>
            <v-tooltip
              bottom
              v-if="store.isStudent && getRaw(displayItem).isInRevision()"
            >
              <template v-slot:activator="{ props }">
                <v-icon
                  :class="[
                    'action-button',
                    { 'unread-icon': hasUnreadReviews(getRaw(displayItem)) },
                  ]"
                  v-bind="props"
                  @click="editQuestionSubmission(getRaw(displayItem))"
                  data-cy="EditSubmission"
                  :color="hasUnreadReviews(getRaw(displayItem)) ? 'white' : undefined"
                  >edit</v-icon
                >
              </template>
              <span>Edit Submission</span>
            </v-tooltip>
            <v-tooltip
              bottom
              v-if="store.isStudent && getRaw(displayItem).isInRevision()"
            >
              <template v-slot:activator="{ props }">
                <v-icon
                  v-bind:class="[
                    'action-button',
                    { 'unread-icon': hasUnreadReviews(getRaw(displayItem)) },
                  ]"
                  v-bind="props"
                  color="red"
                  @click="deleteQuestionSubmission(getRaw(displayItem))"
                  data-cy="DeleteSubmission"
                  >delete</v-icon
                >
              </template>
              <span>Delete Submission</span>
            </v-tooltip>
          </td>
          <td id="id">
            <div
              @click="showQuestionSubmissionDialog(getRaw(displayItem))"
              class="clickableId"
            >
              <v-layout class="d-flex justify-center">
                {{ getRaw(displayItem).id }}
              </v-layout>
            </div>
          </td>
          <td id="title">
            <div
              @click="showQuestionSubmissionDialog(getRaw(displayItem))"
              class="clickableTitle"
            >
              <v-layout class="d-flex justify-center">
                {{ getRaw(displayItem).question.title }}
              </v-layout>
            </div>
          </td>
          <td id="submittedBy" v-if="store.isTeacher">
            {{ getRaw(displayItem).name }}
          </td>
          <td id="status">
            <v-chip :color="getRaw(displayItem).getStatusColor()" small>
              <span>{{ getRaw(displayItem).getStatus() }}</span>
            </v-chip>
          </td>
          <td id="topics">
            <edit-question-submission-topics
              :questionSubmission="getRaw(displayItem)"
              :topics="topics"
              :key="topicsComponentKey"
              v-on:submission-changed-topics="onQuestionSubmissionChangedTopics"
            />
          </td>
          <td id="creationDate">{{ getRaw(displayItem).question.creationDate }}</td>
        </tr>
      </template>
    </v-data-table>
    <edit-question-submission-dialog
      v-if="currentQuestionSubmission && editQuestionSubmissionDialog"
      :dialog="editQuestionSubmissionDialog"
      @update:dialog="editQuestionSubmissionDialog = $event"
      :questionSubmission="currentQuestionSubmission"
      v-on:save-submission="onSaveQuestionSubmission"
      v-on:submit-submission="onSubmitQuestionSubmission"
    />
    <show-question-submission-dialog
      v-if="currentQuestionSubmission && questionSubmissionDialog"
      :dialog="questionSubmissionDialog"
      @update:dialog="questionSubmissionDialog = $event"
      :questionSubmission="currentQuestionSubmission"
      v-on:close-show-question-dialog="onCloseShowQuestionSubmissionDialog"
    />
    <footer>
      <v-icon class="mr-2 action-button">mouse</v-icon>Left-click on question's
      title to view submitted question and submission status.
    </footer>
  </v-card>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import Question from '@/models/management/Question';
import QuestionSubmission from '@/models/management/QuestionSubmission';
import Topic from '@/models/management/Topic';
import ShowQuestionSubmissionDialog from '@/views/questionsubmission/ShowQuestionSubmissionDialog.vue';
import EditQuestionSubmissionDialog from '@/views/questionsubmission/EditQuestionSubmissionDialog.vue';
import EditQuestionSubmissionTopics from '@/views/questionsubmission/EditQuestionSubmissionTopics.vue';
import Review from '@/models/management/Review';

const store = useStore();

const questionSubmissions = ref<QuestionSubmission[]>([]);
const topics = ref<Topic[]>([]);
const currentQuestionSubmission = ref<QuestionSubmission | null>(null);
const editQuestionSubmissionDialog = ref<boolean>(false);
const questionSubmissionDialog = ref<boolean>(false);
const search = ref<string>('');
const topicsComponentKey = ref<number>(0);

const getRaw = (item: any): QuestionSubmission => {
  return item.raw || item;
};

const processHeaders = () => {
  let mapped = QuestionSubmission.questionSubmissionHeader.map((h: any) => ({
    ...h,
    title: h.text || h.title,
    align: h.align === 'left' ? 'start' : h.align === 'right' ? 'end' : h.align
  }));
  
  if (store.isTeacher) {
    mapped.splice(3, 0, {
      title: 'Submitted by',
      value: 'name',
      align: 'center',
      width: '10%',
      sortable: true
    });
  }
  return mapped;
};

const headers = ref<any[]>(processHeaders());

watch(editQuestionSubmissionDialog, (newVal) => {
  if (!newVal) {
    currentQuestionSubmission.value = null;
  }
});

watch(questionSubmissionDialog, async (newVal) => {
  if (!newVal) {
    await getQuestionSubmissions();
  }
});

const getQuestionSubmissions = async () => {
  store.setLoading();
  try {
    if (store.isStudent) {
      const [_submissions, _topics] = await Promise.all([
        RemoteServices.getStudentQuestionSubmissions(),
        RemoteServices.getTopics(),
      ]);
      questionSubmissions.value = _submissions;
      topics.value = _topics;
    } else {
      const [_submissions, _topics] = await Promise.all([
        RemoteServices.getCourseExecutionQuestionSubmissions(),
        RemoteServices.getTopics(),
      ]);
      questionSubmissions.value = _submissions;
      topics.value = _topics;
    }
    topicsComponentKey.value += 1;
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
}

onMounted(async () => {
  await getQuestionSubmissions();
});

const customFilter = (value: any, query: string, item?: any) => {
  return (
    query != null &&
    JSON.stringify(item?.raw || item).toLowerCase().indexOf(query.toLowerCase()) !==
      -1
  );
};

const newSubmission = () => {
  let question = new Question();
  question.status = 'SUBMITTED';
  let submission = new QuestionSubmission();
  submission.prepareQuestionSubmission(
    store.getCurrentCourse!.courseExecutionId as number,
    store.getUser!.id as number,
    question
  );
  currentQuestionSubmission.value = submission;
  editQuestionSubmissionDialog.value = true;
};

const editQuestionSubmission = (submission: QuestionSubmission, e?: Event) => {
  if (e) e.preventDefault();
  currentQuestionSubmission.value = submission;
  editQuestionSubmissionDialog.value = true;
};

const onSaveQuestionSubmission = (questionSubmission: QuestionSubmission) => {
  questionSubmissions.value = questionSubmissions.value.filter(
    (qs) => qs.id !== questionSubmission.id
  );
  questionSubmissions.value.unshift(questionSubmission);
  editQuestionSubmissionDialog.value = false;
  currentQuestionSubmission.value = null;
};

const onSubmitQuestionSubmission = async (
  comment: string,
  questionSubmission: QuestionSubmission
) => {
  onSaveQuestionSubmission(questionSubmission);
  store.setLoading();
  try {
    let review = new Review();
    review.prepareReview(
      questionSubmission.id as number,
      'REQUEST_REVIEW',
      comment,
      store.getUser!.id as number
    );
    await RemoteServices.createReview(review);
    await RemoteServices.toggleTeacherNotificationRead(
      questionSubmission.id!,
      false
    );
    await getQuestionSubmissions();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const onQuestionSubmissionChangedTopics = (
  questionId: number,
  changedTopics: Topic[]
) => {
  let questionSubmission = questionSubmissions.value.find(
    (questionSubmission: QuestionSubmission) =>
      questionSubmission.question.id == questionId
  );
  if (questionSubmission) {
    questionSubmission.question.topics = changedTopics;
  }
};

const showQuestionSubmissionDialogAction = async (questionSubmission: QuestionSubmission) => {
  currentQuestionSubmission.value = questionSubmission;
  questionSubmissionDialog.value = true;

  try {
    if (store.isStudent) {
      await RemoteServices.toggleStudentNotificationRead(
        questionSubmission.id!,
        true
      );
    } else if (store.isTeacher) {
      await RemoteServices.toggleTeacherNotificationRead(
        questionSubmission.id!,
        true
      );
    }
  } catch (error) {
    store.setError(error as string);
  }
};
// Use alias to not conflict with component ref
const showQuestionSubmissionDialog = showQuestionSubmissionDialogAction;

const onCloseShowQuestionSubmissionDialog = () => {
  currentQuestionSubmission.value = null;
  questionSubmissionDialog.value = false;
};

const deleteQuestionSubmission = async (
  toDeleteQuestionSubmission: QuestionSubmission
) => {
  if (
    toDeleteQuestionSubmission.id &&
    confirm('Are you sure you want to delete this submission?')
  ) {
    try {
      await RemoteServices.deleteSubmittedQuestion(
        toDeleteQuestionSubmission.id
      );
      questionSubmissions.value = questionSubmissions.value.filter(
        (questionSubmission) =>
          questionSubmission.question.id !=
          toDeleteQuestionSubmission.question.id
      );
    } catch (error) {
      store.setError(error as string);
    }
  }
};

const hasUnreadReviews = (questionSubmission: QuestionSubmission) => {
  return (
    (store.isStudent && !questionSubmission.studentRead) ||
    (store.isTeacher && !questionSubmission.teacherRead)
  );
};
</script>

<style lang="scss">
.unread {
  background-color: rgba(51, 153, 255, 0.2);
}
.unread-icon {
  background-color: dodgerblue;
}
</style>
