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
                :key="(course.acronym || '') + (course.academicTerm || '')"
                bottom
              >
                <template v-slot:activator="{ props }">
                  <v-list-item
                    v-bind="props"
                    @click="selectCourse(course)"
                    :class="course.status?.toLowerCase() || ''"
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
          <br />
          (You have to logout and login to start managing it)
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

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import { useRouter } from 'vue-router';
import Course from '@/models/user/Course';
import RemoteServices from '@/services/RemoteServices';

interface CourseMap {
  [key: string]: Course[];
}

const store = useStore();
const router = useRouter();

const courseExecutions = ref<CourseMap | null>(null);
const confirmationDialog = ref(false);
const selectedCourse = ref<Course | null>(null);

onMounted(async () => {
  const user = store.user;
  if (user && user.courses) {
    courseExecutions.value = user.courses;
  }
});

const selectCourse = async (course: Course) => {
  if (course.status !== 'INACTIVE') {
    store.setCurrentCourse(course);
    await router.push({ name: 'home' });
  } else {
    selectedCourse.value = course;
    confirmationDialog.value = true;
  }
};

const activateCourse = async () => {
  confirmationDialog.value = false;
  try {
    if (selectedCourse.value) {
      selectedCourse.value.status = 'ACTIVE';
      selectedCourse.value = await RemoteServices.activateCourseExecution(
        selectedCourse.value
      );
      store.setCurrentCourse(selectedCourse.value);
      await router.push({ name: 'home' });
    }
  } catch (error) {
    store.setError(error as string);
  }
};

const unselectCourse = () => {
  selectedCourse.value = null;
  confirmationDialog.value = false;
};

const compareTerm = (term1: string, term2: string) => {
  if (!term1 || !term2) return 0;
  let yearCompare = term2
    .substr(term2.length - 9)
    .localeCompare(term1.substr(term1.length - 9));

  if (yearCompare !== 0) return yearCompare;

  return term2.localeCompare(term1);
};
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
