<template>
  <div class="container">
    <h2>Select Course</h2>

    <div v-if="courseExecutions">
      <div
        v-for="term in Object.keys(courseExecutions).sort(compareTerm)"
        :key="term"
      >
        <v-card class="mx-auto" elevation="10">
          <v-list rounded>
            <v-subheader class="title">{{ term }}</v-subheader>
            <v-list-item-group color="primary">
              <v-tooltip
                v-for="course in courseExecutions[term]"
                :key="course.acronym + course.academicTerm"
                bottom
              >
                <template v-slot:activator="{ on }">
                  <v-list-item
                    v-on="on"
                    @click="selectCourse(course)"
                    :class="course.status.toLowerCase()"
                  >
                    <v-list-item-content>
                      <v-list-item-title>
                        {{ course.name }} ({{ course.acronym }})
                      </v-list-item-title>
                    </v-list-item-content>

                    <v-list-item-action>
                      <v-btn icon>
                        <v-icon
                          v-if="course.status === 'INACTIVE'"
                          color="grey lighten-1"
                          >mdi-key</v-icon
                        >
                        <v-icon
                          v-else-if="course.status === 'HISTORIC'"
                          color="grey lighten-1"
                          >mdi-book-open-variant</v-icon
                        >
                        <v-icon v-else color="grey lighten-1"
                          >mdi-location-enter</v-icon
                        >
                      </v-btn>
                    </v-list-item-action>
                  </v-list-item>
                </template>
                <span v-if="course.status === 'INACTIVE'"
                  >Activate course for students</span
                >
                <span v-else-if="course.status === 'HISTORIC'"
                  >View Historic Records</span
                >
                <span v-else
                  >Enter {{ course.name }} {{ course.academicTerm }}
                </span>
              </v-tooltip>
            </v-list-item-group>
          </v-list>
        </v-card>
      </div>
    </div>

    <v-dialog v-model="confirmationDialog" v-if="selectedCourse" width="50%">
      <v-card>
        <v-card-title primary-title class="secondary white--text headline">
          Confirmation
        </v-card-title>

        <v-card-text class="text--black title">
          <br />
          Are you sure you want to activate
          <span class="bold">{{ selectedCourse.name }}</span>
          for
          <span class="bold">{{ selectedCourse.academicTerm }}</span
          >?
          <br />
          (Once activated students will be able to login and use this platform)
        </v-card-text>

        <v-divider />

        <v-card-actions>
          <v-spacer />
          <v-btn color="secondary" text @click="unselectCourse"> Cancel </v-btn>
          <v-btn color="primary" text @click="activateCourse"> I'm sure </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Course from '@/models/user/Course';
import RemoteServices from '@/services/RemoteServices';

interface CourseMap {
  [key: string]: Course[];
}

@Component
export default class CourseSelectionView extends Vue {
  courseExecutions: CourseMap | null = null;
  confirmationDialog: Boolean = false;
  selectedCourse: Course | null = null;

  async created() {
    this.courseExecutions = await this.$store.getters.getUser.courses;
  }

  async selectCourse(course: Course) {
    if (course.status !== 'INACTIVE') {
      await this.$store.dispatch('currentCourse', course);
      await this.$router.push({ name: 'home' });
    } else {
      this.selectedCourse = course;
      this.confirmationDialog = true;
    }
  }

  async activateCourse() {
    this.confirmationDialog = false;
    try {
      if (this.selectedCourse) {
        this.selectedCourse.status = 'ACTIVE';
        this.selectedCourse = await RemoteServices.activateCourseExecution(
          this.selectedCourse
        );
        await this.$store.dispatch('currentCourse', this.selectedCourse);
        await this.$router.push({ name: 'home' });
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  unselectCourse() {
    this.selectedCourse = null;
    this.confirmationDialog = false;
  }

  compareTerm(term1: string, term2: string) {
    let yearCompare = term2
      .substr(term2.length - 9)
      .localeCompare(term1.substr(term1.length - 9));

    if (yearCompare !== 0) return yearCompare;

    return term2.localeCompare(term1);
  }
}
</script>

<style lang="scss">
.title {
  text-align: center;
  font-family: 'Baloo Tamma', cursive;
}

.bold {
  font-weight: bolder;
  text-decoration: underline;
}

.active {
  background-color: #42b983;
  .v-icon {
    padding: 0;
  }
}

.inactive {
  background-color: #7f7f7f;
  .v-icon {
    padding: 0;
  }
}

.historic {
  background-color: cornflowerblue;
  .v-icon {
    padding: 0;
  }
}
</style>
