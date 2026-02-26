<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="courses"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
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
          <v-btn color="primary" dark @click="newCourse" data-cy="createButton"
            >New Course</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ props }">
            <v-icon
              class="mr-2 action-button"
              v-bind="props"
              @click="createFromCourse(item)"
              data-cy="createFromCourse"
              >cached</v-icon
            >
          </template>
          <span>Create from Course</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isExternalCourse(item)">
          <template v-slot:activator="{ props }">
            <v-icon
              class="mr-2 action-button"
              v-bind="props"
              @click="viewCourseExecutionUsers(item)"
              data-cy="viewUsersButton"
              >fas fa-user</v-icon
            >
          </template>
          <span>View Users</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isExternalCourse(item)">
          <template v-slot:activator="{ props }">
            <v-icon
              class="mr-2 action-button"
              v-bind="props"
              @click="uploadUsersHandler(item)"
              data-cy="uploadUsersHandler"
              >attach_file</v-icon
            >
          </template>
          <span>Upload External Users</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isExternalCourse(item)">
          <template v-slot:activator="{ props }">
            <v-icon
              class="mr-2 action-button"
              v-bind="props"
              @click="addExternalUser(item)"
              data-cy="addExternalUser"
              >person_add</v-icon
            >
          </template>
          <span>Add Student/Teacher</span>
        </v-tooltip>
        <v-tooltip bottom v-if="hasCourseSemesterFinished(item)">
          <template v-slot:activator="{ props }">
            <v-icon
              class="mr-2 action-button"
              v-bind="props"
              @click="anonymizeCourse(item)"
              color="red"
              data-cy="anonymizeCourse"
              >lock</v-icon
            >
          </template>
          <span>Anonymize Course's Users</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ props }">
            <v-icon
              class="mr-2"
              v-bind="props"
              @click="exportCourseExecutionInfo(item)"
              data-cy="exportCourse"
              >fas fa-download</v-icon
            >
          </template>
          <span>Export</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ props }">
            <v-icon
              class="mr-2 action-button"
              v-bind="props"
              @click="removeCourseNonQuizQuestions(item)"
              color="red"
              >fas fa-eraser</v-icon
            >
          </template>
          <span>Delete Course Non Quiz Questions</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ props }">
            <v-icon
              class="mr-2 action-button"
              v-bind="props"
              @click="deleteCourse(item)"
              color="red"
              data-cy="deleteCourse"
              >delete</v-icon
            >
          </template>
          <span>Delete Course</span>
        </v-tooltip>
      </template>
    </v-data-table>

    <edit-course-dialog
      v-if="currentCourse"
      v-model:dialog="editCourseDialog"
      :course="currentCourse"
      v-on:new-course="onCreateCourse"
      v-on:close-dialog="onCloseDialog"
    />
    <upload-users-dialog
      v-if="uploadUsersCourse"
      v-model:dialog="uploadUsersDialog"
      :course="uploadUsersCourse"
      v-on:users-uploaded="closeUploadUsersDialog"
      v-on:close-dialog="onCloseDialog"
    />
    <add-user-dialog
      v-if="currentCourse"
      v-model:dialog="addUserDialog"
      :course="currentCourse"
      v-on:user-created="onCreateUser"
      v-on:close-dialog="onCloseDialog"
    />
    <view-users-dialog
      v-if="currentCourse"
      v-model:dialog="viewUsersDialog"
      :course="currentCourse"
      v-on:delete-users="onDeleteUsers"
      v-on:close-dialog="onCloseDialog"
    />
  </v-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import Course from '@/models/user/Course';
import RemoteServices from '@/services/RemoteServices';
import EditCourseDialog from '@/views/admin/courses/EditCourseDialog.vue';
import AddUserDialog from '@/views/admin/courses/AddUserDialog.vue';
import UploadUsersDialog from '@/views/admin/courses/UploadUsersDialog.vue';
import ViewUsersDialog from '@/views/admin/courses/ViewUsersDialog.vue';
import ExternalUser from '@/models/user/ExternalUser';
import User from '@/models/user/User';

const store = useStore();

const courses = ref<Course[]>([]);
const uploadUsersCourse = ref<Course | null>(null);
const currentCourse = ref<Course | null>(null);
const editCourseDialog = ref(false);
const uploadUsersDialog = ref(false);
const addUserDialog = ref(false);
const viewUsersDialog = ref(false);
const search = ref('');

const headers = [
  { title: 'Actions', value: 'action', align: 'start', sortable: false, width: '25%' },
  { title: 'Course Type', value: 'courseType', align: 'center', width: '10%' },
  { title: 'Name', value: 'name', align: 'start', width: '25%' },
  { title: 'Execution Type', value: 'courseExecutionType', align: 'center', width: '10%' },
  { title: 'Acronym', value: 'acronym', align: 'center', width: '10%' },
  { title: 'Academic Term', value: 'academicTerm', align: 'center', width: '10%' },
  { title: 'Number of Active Teachers', value: 'numberOfActiveTeachers', align: 'center', width: '5%' },
  { title: 'Number of Inactive Teachers', value: 'numberOfInactiveTeachers', align: 'center', width: '5%' },
  { title: 'Number of Active Students', value: 'numberOfActiveStudents', align: 'center', width: '5%' },
  { title: 'Number of Inactive Students', value: 'numberOfInactiveStudents', align: 'center', width: '5%' },
  { title: 'Number of Questions', value: 'numberOfQuestions', align: 'center', width: '5%' },
  { title: 'Number of Quizzes', value: 'numberOfQuizzes', align: 'center', width: '5%' },
  { title: 'Status', value: 'status', align: 'center', width: '5%' }
] as any;

onMounted(async () => {
  store.setLoading();
  try {
    courses.value = await RemoteServices.getCourses();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

const newCourse = () => {
  currentCourse.value = new Course();
  editCourseDialog.value = true;
};

const createFromCourse = (course: Course) => {
  currentCourse.value = new Course(course);
  currentCourse.value.courseExecutionId = undefined;
  currentCourse.value.courseExecutionType = 'EXTERNAL';
  currentCourse.value.acronym = undefined;
  currentCourse.value.academicTerm = undefined;
  editCourseDialog.value = true;
};

const viewCourseExecutionUsers = (course: Course) => {
  currentCourse.value = course;
  viewUsersDialog.value = true;
};

const onCreateCourse = async (course: Course) => {
  courses.value.unshift(course);
  editCourseDialog.value = false;
  currentCourse.value = null;
};

const updateUserNumbers = (course: Course) => {
  if (!!course && !!course.courseExecutionUsers) {
    course.numberOfInactiveTeachers = course.courseExecutionUsers.filter(
      (user) => user.role === 'TEACHER' && !user.active
    ).length;
    course.numberOfInactiveStudents = course.courseExecutionUsers.filter(
      (user) => user.role === 'STUDENT' && !user.active
    ).length;
  }
};

const onCloseDialog = () => {
  editCourseDialog.value = false;
  currentCourse.value = null;
  uploadUsersCourse.value = null;
  addUserDialog.value = false;
  viewUsersDialog.value = false;
};

const addExternalUser = (course: Course) => {
  addUserDialog.value = true;
  currentCourse.value = course;
};

const onCreateUser = (user: ExternalUser) => {
  if (!!currentCourse.value && !!currentCourse.value.courseExecutionUsers) {
    currentCourse.value.courseExecutionUsers.unshift(user);
    let index: number = courses.value.indexOf(
      courses.value.filter(
        (course) => course.courseExecutionId == currentCourse.value?.courseExecutionId
      )[0]
    );
    courses.value[index].courseExecutionUsers = currentCourse.value.courseExecutionUsers;
    updateUserNumbers(courses.value[index]);
  }
};

const isExternalCourse = (course: Course) => {
  return course.courseExecutionType === 'EXTERNAL';
};

const removeCourseNonQuizQuestions = async (course: Course) => {
  if (confirm('Are you sure you want to delete the questions that do not belong to any quiz?')) {
    try {
      await RemoteServices.removeCourseNonQuizQuestions(course.courseId);
    } catch (error) {
      store.setError(error as string);
    }
  }
};

const deleteCourse = async (courseToDelete: Course) => {
  if (confirm('Are you sure you want to delete this course execution?')) {
    try {
      await RemoteServices.deleteCourseExecution(courseToDelete.courseExecutionId);
      courses.value = courses.value.filter(
        (course) => course.courseExecutionId != courseToDelete.courseExecutionId
      );
    } catch (error) {
      store.setError(error as string);
    }
  }
};

const hasCourseSemesterFinished = (course: Course): boolean => {
  if (course.endDate) {
    return new Date(course.endDate) < new Date();
  } else if (
    course.academicTerm &&
    RegExp(/[1-2]º?\s?\w+\s[0-9]+\/[0-9]+/).test(course.academicTerm)
  ) {
    const termTokens = course.academicTerm.split(/º|\s|\//);
    const month = termTokens[0] === '1' ? 3 : 9; // march : september
    const year = parseInt(termTokens[termTokens.length - 1]);
    return new Date(`${year}-${month}-${1}`) < new Date();
  }
  return false;
};

const anonymizeCourse = async (courseToAnonymize: Course) => {
  if (confirm('Are you sure you want to anonymize the users of this course execution?')) {
    store.setLoading();
    try {
      await RemoteServices.anonymizeCourse(courseToAnonymize.courseExecutionId);
    } catch (error) {
      store.setError(error as string);
    }
    store.clearLoading();
  }
};

const uploadUsersHandler = (course: Course) => {
  uploadUsersCourse.value = course;
  uploadUsersDialog.value = true;
};

const closeUploadUsersDialog = async (updatedCourse: Course) => {
  uploadUsersDialog.value = false;
  store.setLoading();
  courses.value = courses.value.filter(
    (course) => course.courseExecutionId !== updatedCourse.courseExecutionId
  );
  courses.value.unshift(updatedCourse);
  store.clearLoading();
};

const onDeleteUsers = async (users: User[]) => {
  let course: Course;
  store.setLoading();
  if (!!currentCourse.value) {
    try {
      course = await RemoteServices.deleteExternalInactiveUsers(
        currentCourse.value,
        users.flatMap((user) => (user.id ? [user.id] : []))
      );
      let index: number = courses.value.indexOf(
        courses.value.filter((c) => c.courseExecutionId == course.courseExecutionId)[0]
      );
      currentCourse.value = course;
      courses.value[index].courseExecutionUsers = currentCourse.value.courseExecutionUsers;

      updateUserNumbers(courses.value[index]);
    } catch (error) {
      store.setError(error as string);
    }
  }
  store.clearLoading();
};

const exportCourseExecutionInfo = async (course: Course) => {
  let fileName = course.acronym + '.tar.gz';
  try {
    if (course.courseExecutionId != null) {
      let result = await RemoteServices.exportCourseExecutionInfo(course.courseExecutionId);
      const url = window.URL.createObjectURL(result);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
    }
  } catch (error) {
    store.setError(error as string);
  }
};
</script>

<style lang="scss" scoped></style>
