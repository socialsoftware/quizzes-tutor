<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="students"
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
        </v-card-title>
      </template>

      <template v-slot:[`item.percentageOfCorrectAnswers`]="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectAnswers)"
          dark
          >{{ item.percentageOfCorrectAnswers + '%' }}</v-chip
        >
      </template>

      <template v-slot:[`item.percentageOfCorrectTeacherAnswers`]="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectTeacherAnswers)"
          dark
          >{{ item.percentageOfCorrectTeacherAnswers + '%' }}</v-chip
        >
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Course from '@/models/user/Course';
import { Student } from '@/models/user/Student';

@Component
export default class StudentsView extends Vue {
  course: Course | null = null;
  students: Student[] = [];
  search: string = '';
  headers: object = [
    { text: 'Name', value: 'name', align: 'left', width: '40%' },
    {
      text: 'Teacher Quizzes',
      value: 'numberOfTeacherQuizzes',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Generated Quizzes',
      value: 'numberOfStudentQuizzes',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Total Answers',
      value: 'numberOfAnswers',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Correct Answers',
      value: 'percentageOfCorrectAnswers',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Answers Teacher Quiz',
      value: 'numberOfTeacherAnswers',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Correct Answers Teacher Quiz',
      value: 'percentageOfCorrectTeacherAnswers',
      align: 'center',
      width: '10%',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.course = this.$store.getters.getCurrentCourse;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  @Watch('course')
  async onAcademicTermChange() {
    await this.$store.dispatch('loading');
    try {
      if (this.course) {
        this.students = await RemoteServices.getCourseStudents(this.course);
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  getPercentageColor(percentage: number) {
    if (percentage < 25) return 'red';
    else if (percentage < 50) return 'orange';
    else if (percentage < 75) return 'lime';
    else return 'green';
  }
}
</script>

<style lang="scss" scoped />
