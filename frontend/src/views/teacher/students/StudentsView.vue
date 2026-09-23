<template>
  <v-card class="table">
    <v-card-title>{{ students.length }} Students</v-card-title>
    <v-data-table
      :headers="headers"
      hide-default-footer
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
          class="text-white"
          >{{ item.percentageOfCorrectAnswers + '%' }}
        </v-chip>
      </template>

      <template v-slot:[`item.percentageOfCorrectTeacherAnswers`]="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectTeacherAnswers)"
          class="text-white"
          >{{ item.percentageOfCorrectTeacherAnswers + '%' }}
        </v-chip>
      </template>

      <template v-slot:[`item.percentageOfCorrectStudentAnswers`]="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectStudentAnswers)"
          class="text-white"
          >{{ item.percentageOfCorrectStudentAnswers + '%' }}
        </v-chip>
      </template>

      <template v-slot:[`item.percentageOfCorrectInClassAnswers`]="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectInClassAnswers)"
          class="text-white"
          >{{ item.percentageOfCorrectInClassAnswers + '%' }}
        </v-chip>
      </template>
    </v-data-table>
  </v-card>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import Course from '@/models/user/Course';
import { Student } from '@/models/user/Student';

const store = useStore();
const course = ref<Course | null>(null);
const students = ref<Student[]>([]);
const search = ref('');

const headers: any[] = [
  { title: 'Username', key: 'username', align: 'start', width: '10%' },
  { title: 'Name', key: 'name', align: 'start', width: '40%' },
  { title: 'Proposed Quizzes', key: 'numberOfTeacherQuizzes', align: 'center', width: '10%' },
  { title: 'Answers Proposed Quizzes', key: 'numberOfTeacherAnswers', align: 'center', width: '10%' },
  { title: 'Correct Answers Proposed Quizzes', key: 'percentageOfCorrectTeacherAnswers', align: 'center', width: '10%' },
  { title: 'Generated Quizzes', key: 'numberOfStudentQuizzes', align: 'center', width: '10%' },
  { title: 'Answers Generated Quizzes', key: 'numberOfStudentAnswers', align: 'center', width: '10%' },
  { title: 'Correct Answers Generated Quizzes', key: 'percentageOfCorrectStudentAnswers', align: 'center', width: '10%' },
  { title: 'InClass Quizzes', key: 'numberOfInClassQuizzes', align: 'center', width: '10%' },
  { title: 'Answers InClass Quizzes', key: 'numberOfInClassAnswers', align: 'center', width: '10%' },
  { title: 'Correct Answers InClass Quizzes', key: 'percentageOfCorrectInClassAnswers', align: 'center', width: '10%' },
  { title: 'Total Answers', key: 'numberOfAnswers', align: 'center', width: '10%' },
  { title: 'Correct Answers', key: 'percentageOfCorrectAnswers', align: 'center', width: '10%' },
];

onMounted(async () => {
  store.setLoading();
  try {
    course.value = store.getCurrentCourse;
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

watch(course, async () => {
  store.setLoading();
  try {
    if (course.value) {
      students.value = await RemoteServices.getCourseStudents(course.value);
    }
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

const getPercentageColor = (percentage: number) => {
  if (percentage < 25) return 'error';  // mapeado para #7f0000 no tema Vuetify
  else if (percentage < 50) return 'orange';
  else if (percentage < 75) return 'lime';
  else return 'green';
};
</script>

<style lang="scss" scoped />
