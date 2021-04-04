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
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              v-on="on"
              @click="createFromCourse(item)"
              data-cy="createFromCourse"
              >cached</v-icon
            >
          </template>
          <span>Create from Course</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isExternalCourse(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              v-on="on"
              @click="viewCourseExecutionUsers(item)"
              data-cy="viewUsersButton"
              >fas fa-user</v-icon
            >
          </template>
          <span>View Users</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isExternalCourse(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              v-on="on"
              @click="uploadUsersHandler(item)"
              data-cy="uploadUsersHandler"
              >attach_file</v-icon
            >
          </template>
          <span>Upload External Users</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isExternalCourse(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              v-on="on"
              @click="addExternalUser(item)"
              data-cy="addExternalUser"
              >person_add</v-icon
            >
          </template>
          <span>Add Student/Teacher</span>
        </v-tooltip>
        <v-tooltip bottom v-if="hasCourseSemesterFinished(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              v-on="on"
              @click="anonymizeCourse(item)"
              color="red"
              data-cy="anonymizeCourse"
              >lock</v-icon
            >
          </template>
          <span>Anonymize Course's Users</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="exportCourseExecutionInfo(item)"
              data-cy="exportCourse"
              >fas fa-download</v-icon
            >
          </template>
          <span>Export</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              v-on="on"
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
      v-model="editCourseDialog"
      :course="currentCourse"
      v-on:new-course="onCreateCourse"
      v-on:close-dialog="onCloseDialog"
    />
    <upload-users-dialog
      v-if="uploadUsersCourse"
      v-model="uploadUsersDialog"
      :course="uploadUsersCourse"
      v-on:users-uploaded="closeUploadUsersDialog"
      v-on:close-dialog="onCloseDialog"
    />
    <add-user-dialog
      v-if="currentCourse"
      v-model="addUserDialog"
      :course="currentCourse"
      v-on:user-created="onCreateUser"
      v-on:close-dialog="onCloseDialog"
    />
    <view-users-dialog
      v-if="currentCourse"
      v-model="viewUsersDialog"
      :course="currentCourse"
      v-on:delete-users="onDeleteUsers"
      v-on:close-dialog="onCloseDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Course from '@/models/user/Course';
import RemoteServices from '@/services/RemoteServices';
import EditCourseDialog from '@/views/admin/courses/EditCourseDialog.vue';
import AddUserDialog from '@/views/admin/courses/AddUserDialog.vue';
import UploadUsersDialog from '@/views/admin/courses/UploadUsersDialog.vue';
import ViewUsersDialog from '@/views/admin/courses/ViewUsersDialog.vue';
import ExternalUser from '@/models/user/ExternalUser';
import User from '@/models/user/User';

@Component({
  components: {
    'upload-users-dialog': UploadUsersDialog,
    'edit-course-dialog': EditCourseDialog,
    'add-user-dialog': AddUserDialog,
    'view-users-dialog': ViewUsersDialog,
  },
})
export default class CoursesView extends Vue {
  courses: Course[] = [];
  uploadUsersCourse: Course | null = null;
  currentCourse: Course | null = null;
  editCourseDialog: boolean = false;
  uploadUsersDialog: boolean = false;
  addUserDialog: boolean = false;
  viewUsersDialog: boolean = false;
  search: string = '';
  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '25%',
    },
    {
      text: 'Course Type',
      value: 'courseType',
      align: 'center',
      width: '10%',
    },
    { text: 'Name', value: 'name', align: 'left', width: '25%' },
    {
      text: 'Execution Type',
      value: 'courseExecutionType',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Acronym',
      value: 'acronym',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Academic Term',
      value: 'academicTerm',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Number of Active Teachers',
      value: 'numberOfActiveTeachers',
      align: 'center',
      width: '5%',
    },
    {
      text: 'Number of Inactive Teachers',
      value: 'numberOfInactiveTeachers',
      align: 'center',
      width: '5%',
    },
    {
      text: 'Number of Active Students',
      value: 'numberOfActiveStudents',
      align: 'center',
      width: '5%',
    },
    {
      text: 'Number of Inactive Students',
      value: 'numberOfInactiveStudents',
      align: 'center',
      width: '5%',
    },
    {
      text: 'Number of Questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '5%',
    },
    {
      text: 'Number of Quizzes',
      value: 'numberOfQuizzes',
      align: 'center',
      width: '5%',
    },
    {
      text: 'Status',
      value: 'status',
      align: 'center',
      width: '5%',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.courses = await RemoteServices.getCourses();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  newCourse() {
    this.currentCourse = new Course();
    this.editCourseDialog = true;
  }

  createFromCourse(course: Course) {
    this.currentCourse = new Course(course);
    this.currentCourse.courseExecutionId = undefined;
    this.currentCourse.courseExecutionType = 'EXTERNAL';
    this.currentCourse.acronym = undefined;
    this.currentCourse.academicTerm = undefined;
    this.editCourseDialog = true;
  }

  viewCourseExecutionUsers(course: Course) {
    this.currentCourse = course;
    this.viewUsersDialog = true;
  }

  async onCreateCourse(course: Course) {
    this.courses.unshift(course);
    this.editCourseDialog = false;
    this.currentCourse = null;
  }

  updateUserNumbers(course: Course) {
    if (!!course && !!course.courseExecutionUsers) {
      course.numberOfInactiveTeachers = course.courseExecutionUsers.filter(
        (user) => user.role === 'TEACHER' && !user.active
      ).length;
      course.numberOfInactiveStudents = course.courseExecutionUsers.filter(
        (user) => user.role === 'STUDENT' && !user.active
      ).length;
    }
  }

  onCloseDialog() {
    this.editCourseDialog = false;
    this.currentCourse = null;
    this.uploadUsersCourse = null;
    this.addUserDialog = false;
    this.viewUsersDialog = false;
  }

  addExternalUser(course: Course) {
    this.addUserDialog = true;
    this.currentCourse = course;
  }

  onCreateUser(user: ExternalUser) {
    if (!!this.currentCourse && !!this.currentCourse.courseExecutionUsers) {
      this.currentCourse.courseExecutionUsers.unshift(user);
      let index: number = this.courses.indexOf(
        this.courses.filter(
          (course) =>
            course.courseExecutionId == this.currentCourse?.courseExecutionId
        )[0]
      );
      this.courses[
        index
      ].courseExecutionUsers = this.currentCourse.courseExecutionUsers;
      this.updateUserNumbers(this.courses[index]);
    }
  }

  isExternalCourse(course: Course) {
    return course.courseExecutionType === 'EXTERNAL';
  }

  async deleteCourse(courseToDelete: Course) {
    if (confirm('Are you sure you want to delete this course execution?')) {
      try {
        await RemoteServices.deleteCourseExecution(
          courseToDelete.courseExecutionId
        );
        this.courses = this.courses.filter(
          (course) =>
            course.courseExecutionId != courseToDelete.courseExecutionId
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  hasCourseSemesterFinished(course: Course): boolean {
    if (course.endDate) {
      return new Date(course.endDate) < new Date();
    } else if (
      course.academicTerm &&
      RegExp(/[1-2]º?\s?\w+\s[0-9]+\/[0-9]+/).test(course.academicTerm)
    ) {
      const termTokens = course.academicTerm.split(/º|\s|\//);
      const month = termTokens[0] === '1' ? 3 : 9; // march : september
      const year = termTokens[termTokens.length - 1];
      return new Date(`${year}-${month}-${1}`) < new Date();
    }
    return false;
  }

  async anonymizeCourse(courseToAnonymize: Course) {
    if (
      confirm(
        'Are you sure you want to anonymize the users of this course execution?'
      )
    ) {
      try {
        await RemoteServices.anonymizeCourse(
          courseToAnonymize.courseExecutionId
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  uploadUsersHandler(course: Course) {
    this.uploadUsersCourse = course;
    this.uploadUsersDialog = true;
  }

  async closeUploadUsersDialog(updatedCourse: Course) {
    this.uploadUsersDialog = false;
    await this.$store.dispatch('loading');
    this.courses = this.courses.filter(
      (course) => course.courseExecutionId !== updatedCourse.courseExecutionId
    );
    this.courses.unshift(updatedCourse);
    await this.$store.dispatch('clearLoading');
  }

  async onDeleteUsers(users: User[]) {
    let course: Course;
    await this.$store.dispatch('loading');
    if (!!this.currentCourse) {
      try {
        course = await RemoteServices.deleteExternalInactiveUsers(
          this.currentCourse,
          users.flatMap((user) => (user.id ? [user.id] : []))
        );
        let index: number = this.courses.indexOf(
          this.courses.filter(
            (c) => c.courseExecutionId == course.courseExecutionId
          )[0]
        );
        this.currentCourse = course;
        this.courses[
          index
        ].courseExecutionUsers = this.currentCourse.courseExecutionUsers;

        this.updateUserNumbers(this.courses[index]);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
    await this.$store.dispatch('clearLoading');
  }

  async exportCourseExecutionInfo(course: Course) {
    let fileName = course.acronym + '.tar.gz';
    try {
      if (course.courseExecutionId != null) {
        let result = await RemoteServices.exportCourseExecutionInfo(
          course.courseExecutionId
        );
        const url = window.URL.createObjectURL(result);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', fileName);
        document.body.appendChild(link);
        link.click();
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style lang="scss" scoped></style>
