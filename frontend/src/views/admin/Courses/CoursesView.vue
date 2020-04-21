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

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
              v-on="on"
              @click="createFromCourse(item)"
              data-cy="createFromCourse"
              >cached</v-icon
            >
          </template>
          <span>Create from Course</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
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
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Course from '@/models/user/Course';
import RemoteServices from '@/services/RemoteServices';
import EditCourseDialog from '@/views/admin/Courses/EditCourseDialog.vue';

@Component({
  components: {
    'edit-course-dialog': EditCourseDialog
  }
})
export default class CoursesView extends Vue {
  courses: Course[] = [];
  currentCourse: Course | null = null;
  editCourseDialog: boolean = false;
  search: string = '';
  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '15%'
    },
    {
      text: 'Course Type',
      value: 'courseType',
      align: 'center',
      width: '10%'
    },
    { text: 'Name', value: 'name', align: 'left', width: '30%' },
    {
      text: 'Execution Type',
      value: 'courseExecutionType',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Acronym',
      value: 'acronym',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Academic Term',
      value: 'academicTerm',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Status',
      value: 'status',
      align: 'center',
      width: '10%'
    }
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

  async onCreateCourse(course: Course) {
    this.courses.unshift(course);
    this.editCourseDialog = false;
    this.currentCourse = null;
  }

  onCloseDialog() {
    this.editCourseDialog = false;
    this.currentCourse = null;
  }

  async deleteCourse(courseToDelete: Course) {
    if (confirm('Are you sure you want to delete this question?')) {
      try {
        await RemoteServices.deleteCourse(courseToDelete.courseExecutionId);
        this.courses = this.courses.filter(
          course => course.courseExecutionId != courseToDelete.courseExecutionId
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped></style>
