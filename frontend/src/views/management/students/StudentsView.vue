<template v-if="year">
  <v-container fluid>
    <v-card class="table">
      <v-card-title>
        <v-row align="center">
          <v-col class="d-flex" cols="12" sm="6">
            <v-select
              v-model="year"
              :items="courseExecutions"
              item-text="year"
              item-value="year"
              label="Year"
            ></v-select>
          </v-col>
        </v-row>
      </v-card-title>
      <v-data-table
        :headers="headers"
        :items="students"
        :search="search"
        multi-sort
        disable-pagination
        class="elevation-1"
      >
        <template v-slot:top>
          <v-text-field
            v-model="search"
            label="Search"
            class="mx-4"
          ></v-text-field>
        </template>
      </v-data-table>
    </v-card>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue, Watch } from "vue-property-decorator";
import RemoteServices from "@/services/RemoteServices";
import { CourseExecution } from "@/models/management/CourseExecution";
import { Student } from "@/models/management/Student";

@Component
export default class StudentsView extends Vue {
  year: number | null = null;
  courseExecutions: CourseExecution[] = [];
  students: Student[] = [];
  search: string = "";
  headers: object = [
    { text: "Name", value: "name", align: "left", width: "40%" },
    {
      text: "Teacher Quizzes",
      value: "numberOfTeacherQuizzes",
      align: "center",
      width: "10%"
    },
    {
      text: "Student Quizzes",
      value: "numberOfStudentQuizzes",
      align: "center",
      width: "10%"
    },
    {
      text: "Total Answers",
      value: "numberOfAnswers",
      align: "center",
      width: "10%"
    },
    {
      text: "Correct Answers",
      value: "percentageOfCorrectAnswers",
      align: "center",
      width: "10%"
    },
    {
      text: "Answers Teacher Quiz",
      value: "numberOfTeacherAnswers",
      align: "center",
      width: "10%"
    },
    {
      text: "Correct Answers Teacher Quiz",
      value: "percentageOfCorrectTeacherAnswers",
      align: "center",
      width: "10%"
    }
  ];

  // noinspection JSUnusedGlobalSymbols
  async created() {
    try {
      this.courseExecutions = await RemoteServices.getCourseExecutions();
      this.year = this.courseExecutions[0].year;
      this.students = await RemoteServices.getCourseExecutionStudents(
        this.year
      );
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }

  @Watch("year")
  async onYearChange() {
    try {
      if (this.year) {
        this.students = await RemoteServices.getCourseExecutionStudents(
          this.year
        );
      }
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }
}
</script>

<style lang="scss"></style>
