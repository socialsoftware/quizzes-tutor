<template>
  <div class="container">
    <h2>Select Course</h2>

    <v-container fluid grid-list-xl>
      <v-layout wrap v-if="courseExecutions">
        <v-flex v-for="name in Object.keys(courseExecutions)" :key="name">
          <v-card class="mx-auto" elevation="10">
            <v-list rounded>
              <v-subheader class="title">{{ name }}</v-subheader>
              <v-list-item-group color="primary">
                <v-list-item
                  v-for="course in courseExecutions[name]"
                  :key="course.acronym"
                  @click="selectCourse(course)"
                >
                  <v-list-item-content>
                    <v-list-item-title v-text="course.academicTerm" />
                  </v-list-item-content>
                </v-list-item>
              </v-list-item-group>
            </v-list>
          </v-card>
        </v-flex>
      </v-layout>
    </v-container>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Course from "@/models/user/Course";

interface CourseMap {
  [key: string]: Course[];
}

@Component
export default class CourseSelectionView extends Vue {
  courseExecutions: CourseMap | null = null;

  async created() {
    this.courseExecutions = await this.$store.getters.getUser.courses;
  }

  async selectCourse(currentCourse: Course) {
    await this.$store.dispatch("currentCourse", currentCourse);
    await this.$router.push({ name: "home" });
  }
}
</script>

<style>
.title {
  text-align: center;
  font-family: "Baloo Tamma", cursive;
}
</style>
