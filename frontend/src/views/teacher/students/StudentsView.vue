<template v-if="year">
  <v-container fluid>
    <v-card class="table">
      <v-card-title>
        <v-row align="center">
          <v-col class="d-flex" cols="12" sm="6">
            <v-select
              v-model="academicTerm"
              :items="courseExecutions"
              item-text="academicTerm"
              item-value="academicTerm"
              label="Semestre"
            />
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
          <v-text-field v-model="search" label="Search" class="mx-4" />
        </template>

        <template v-slot:item.percentageOfCorrectAnswers="{ item }">
          <v-chip
            :color="getPercentageColor(item.percentageOfCorrectAnswers)"
            dark
            >{{ item.percentageOfCorrectAnswers + "%" }}</v-chip
          >
        </template>

        <template v-slot:item.percentageOfCorrectTeacherAnswers="{ item }">
          <v-chip
            :color="getPercentageColor(item.percentageOfCorrectTeacherAnswers)"
            dark
            >{{ item.percentageOfCorrectTeacherAnswers + "%" }}</v-chip
          >
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
  academicTerm: string | null = null;
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
      text: "Generated Quizzes",
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

  async created() {
    await this.$store.dispatch("loading");
    try {
      this.courseExecutions = await RemoteServices.getCourseExecutions();
      this.academicTerm = this.courseExecutions[0].academicTerm;
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
    await this.$store.dispatch("clearLoading");
  }

  @Watch("academicTerm")
  async onAcademicTermChange() {
    await this.$store.dispatch("loading");
    try {
      if (this.academicTerm) {
        this.students = await RemoteServices.getCourseExecutionStudents(
          this.academicTerm
        );
      }
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
    await this.$store.dispatch("clearLoading");
  }

  getPercentageColor(percentage: number) {
    if (percentage < 25) return "red";
    else if (percentage < 50) return "orange";
    else if (percentage < 75) return "lime";
    else return "green";
  }
}
</script>

<style lang="scss" />
