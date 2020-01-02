<template>
  <div>
    <p>
      Choose course
    </p>
    <div v-for="course in courseList" :key="course.acronym">
      {{ course.name + course.acronym }}
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Course from "@/models/auth/Course";

@Component
export default class HomeView extends Vue {
  chosenCourse: Course | null = null;
  courseList: Course[] = [];

  async created() {
    await this.$store.dispatch("loading");
    if (this.$route.query.error) {
      await this.$store.dispatch("error", "Fenix authentication error");
      await this.$router.push({ name: "home" });
    } else {
      await this.$store.dispatch("login", this.$route.query.code);
      this.courseList = this.$store.getters.getUser.courses;
      // await this.$router.push({ name: "home" });
    }
    await this.$store.dispatch("clearLoading");
  }
}
</script>
