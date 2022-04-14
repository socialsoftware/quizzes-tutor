<template>
  <v-card class="table">
    <v-card-title>{{ students.length }} Students</v-card-title>
    <v-data-table
      :headers="headers"
      :hide-default-footer="true"
      :items="students"
      :mobile-breakpoint="0"
      :search="search"
      disable-pagination
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            class="mx-2"
            label="Search"
          />

          <v-spacer />
        </v-card-title>
      </template>

      <template v-slot:[`item.percentageOfCorrectAnswers`]="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectAnswers)"
          dark
          >{{ item.percentageOfCorrectAnswers + '%' }}
        </v-chip>
      </template>

      <template v-slot:[`item.percentageOfCorrectTeacherAnswers`]="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectTeacherAnswers)"
          dark
          >{{ item.percentageOfCorrectTeacherAnswers + '%' }}
        </v-chip>
      </template>

      <template v-slot:[`item.percentageOfCorrectStudentAnswers`]="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectStudentAnswers)"
          dark
          >{{ item.percentageOfCorrectStudentAnswers + '%' }}
        </v-chip>
      </template>

      <template v-slot:[`item.percentageOfCorrectInClassAnswers`]="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectInClassAnswers)"
          dark
          >{{ item.percentageOfCorrectInClassAnswers + '%' }}
        </v-chip>
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
    { text: 'Username', value: 'username', align: 'left', width: '10%' },
    { text: 'Name', value: 'name', align: 'left', width: '40%' },
    {
      text: 'Proposed Quizzes',
      value: 'numberOfTeacherQuizzes',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Answers Proposed Quizzes',
      value: 'numberOfTeacherAnswers',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Correct Answers Proposed Quizzes',
      value: 'percentageOfCorrectTeacherAnswers',
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
      text: 'Answers Generated Quizzes',
      value: 'numberOfStudentAnswers',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Correct Answers Generated Quizzes',
      value: 'percentageOfCorrectStudentAnswers',
      align: 'center',
      width: '10%',
    },
    {
      text: 'InClass Quizzes',
      value: 'numberOfInClassQuizzes',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Answers InClass Quizzes',
      value: 'numberOfInClassAnswers',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Correct Answers InClass Quizzes',
      value: 'percentageOfCorrectInClassAnswers',
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
