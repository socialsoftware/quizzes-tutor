<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="courses"
      :search="search"
      :items-per-page="-1"
      :mobile-breakpoint="0"
    >
      <template #bottom></template>
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
        <div class="d-flex flex-row flex-wrap align-center" style="max-width: 120px;">
          <v-tooltip location="bottom">
            <template v-slot:activator="{ props }">
              <span
                class="mr-2 action-button"
                v-bind="props"
                @click="createFromCourse(getRaw(item))"
                data-cy="createFromCourse"
              >
                <v-icon>cached</v-icon>
              </span>
            </template>
            <span>Create from Course</span>
          </v-tooltip>
          <v-tooltip location="bottom" v-if="isExternalCourse(getRaw(item))">
            <template v-slot:activator="{ props }">
              <span
                class="mr-2 action-button"
                v-bind="props"
                @click="viewCourseExecutionUsers(getRaw(item))"
                data-cy="viewUsersButton"
              >
                <v-icon>fas fa-user</v-icon>
              </span>
            </template>
            <span>View Users</span>
          </v-tooltip>
          <v-tooltip location="bottom" v-if="isExternalCourse(getRaw(item))">
            <template v-slot:activator="{ props }">
              <span
                class="mr-2 action-button"
                v-bind="props"
                @click="uploadUsersHandler(getRaw(item))"
                data-cy="uploadUsersHandler"
              >
                <v-icon>attach_file</v-icon>
              </span>
            </template>
            <span>Upload External Users</span>
          </v-tooltip>
          <v-tooltip location="bottom" v-if="isExternalCourse(getRaw(item))">
            <template v-slot:activator="{ props }">
              <span
                class="mr-2 action-button"
                v-bind="props"
                @click="addExternalUser(getRaw(item))"
                data-cy="addExternalUser"
              >
                <v-icon>person_add</v-icon>
              </span>
            </template>
            <span>Add Student/Teacher</span>
          </v-tooltip>
          <v-tooltip location="bottom" v-if="hasCourseSemesterFinished(getRaw(item))">
            <template v-slot:activator="{ props }">
              <span
                class="mr-2 action-button"
                v-bind="props"
                @click="anonymizeCourse(getRaw(item))"
                data-cy="anonymizeCourse"
              >
                <v-icon color="red">lock</v-icon>
              </span>
            </template>
            <span>Anonymize Course's Users</span>
          </v-tooltip>
          <v-tooltip location="bottom">
            <template v-slot:activator="{ props }">
              <span
                class="mr-2 action-button"
                v-bind="props"
                @click="exportCourseExecutionInfo(getRaw(item))"
                data-cy="exportCourse"
              >
                <v-icon>fas fa-download</v-icon>
              </span>
            </template>
            <span>Export</span>
          </v-tooltip>
          <v-tooltip location="bottom">
            <template v-slot:activator="{ props }">
              <span
                class="mr-2 action-button"
                v-bind="props"
                @click="removeCourseNonQuizQuestions(getRaw(item))"
              >
                <v-icon color="red">fas fa-eraser</v-icon>
              </span>
            </template>
            <span>Delete Course Non Quiz Questions</span>
          </v-tooltip>
          <v-tooltip location="bottom">
            <template v-slot:activator="{ props }">
              <span
                class="mr-2 action-button"
                v-bind="props"
                @click="deleteCourse(getRaw(item))"
                data-cy="deleteCourse"
              >
                <v-icon color="red">delete</v-icon>
              </span>
            </template>
            <span>Delete Course</span>
          </v-tooltip>
        </div>
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

const getRaw = (item: any): Course => (item as any).raw || item;

const headers = [
  { title: 'Actions', key: 'action', align: 'start', sortable: false, width: '120px' },
  { title: 'Course Type', key: 'courseType', align: 'center', width: '110px' },
  { title: 'Name', key: 'name', align: 'start', width: '220px' },
  { title: 'Execution Type', key: 'courseExecutionType', align: 'center', width: '120px' },
  { title: 'Acronym', key: 'acronym', align: 'center', width: '100px' },
  { title: 'Academic Term', key: 'academicTerm', align: 'center', width: '120px' },
  { title: 'Number of Active Teachers', key: 'numberOfActiveTeachers', align: 'center', width: '110px' },
  { title: 'Number of Inactive Teachers', key: 'numberOfInactiveTeachers', align: 'center', width: '110px' },
  { title: 'Number of Active Students', key: 'numberOfActiveStudents', align: 'center', width: '110px' },
  { title: 'Number of Inactive Students', key: 'numberOfInactiveStudents', align: 'center', width: '110px' },
  { title: 'Number of Questions', key: 'numberOfQuestions', align: 'center', width: '100px' },
  { title: 'Number of Quizzes', key: 'numberOfQuizzes', align: 'center', width: '100px' },
  { title: 'Status', key: 'status', align: 'center', width: '100px' }
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
