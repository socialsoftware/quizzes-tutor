<template>
  <div class="container">
    <h2>Select Course</h2>

    <div v-if="courseExecutions">
      <div
        v-for="term in Object.keys(courseExecutions).sort(compareTerm)"
        :key="term"
      >
        <v-card class="mx-auto" elevation="10">
          <v-list class="course-list">
            <v-list-subheader class="term-header">{{ term }}</v-list-subheader>
            <v-tooltip
              v-for="course in courseExecutions[term]"
              :key="(course.acronym || '') + (course.academicTerm || '')"
              location="bottom"
            >
              <template v-slot:activator="{ props }">
                <v-list-item
                  v-bind="props"
                  @click="selectCourse(course)"
                  :class="['course-item', course.status?.toLowerCase() || '']"
                >
                  <v-list-item-title class="course-title">
                    {{ course.name }} ({{ course.acronym }})
                  </v-list-item-title>

                  <template v-slot:append>
                    <v-btn icon variant="text" size="36" class="course-action">
                      <v-icon
                        v-if="course.status === 'INACTIVE'"
                        color="grey-lighten-1"
                        >mdi-key</v-icon
                      >
                      <v-icon
                        v-else-if="course.status === 'HISTORIC'"
                        color="grey-lighten-1"
                        >mdi-book-open-variant</v-icon
                      >
                      <v-icon v-else color="grey-lighten-1"
                        >mdi-location-enter</v-icon
                      >
                    </v-btn>
                  </template>
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
          </v-list>
        </v-card>
      </div>
    </div>

    <v-dialog v-model="confirmationDialog" v-if="selectedCourse" width="50%">
      <v-card>
        <v-card-title class="bg-secondary text-white text-h5">
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
          <v-btn color="secondary" variant="text" @click="unselectCourse"> Cancel </v-btn>
          <v-btn color="primary" variant="text" @click="activateCourse"> I'm sure </v-btn>
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

<style lang="scss" scoped>
.bold {
  font-weight: bolder;
  text-decoration: underline;
}

// Vuetify 2 `v-list rounded` look: pill-shaped items separated by 8px
.course-list {
  padding: 8px;
}

.term-header {
  min-height: 48px;
  padding: 0 16px;
  font-size: 1.25rem;
  font-weight: 500;
  line-height: 2rem;
  letter-spacing: 0.0125em;
  color: rgba(0, 0, 0, 0.6);
}

.course-item {
  min-height: 48px;
  padding: 0 16px;
  margin-bottom: 8px;
  border-radius: 32px;

  // V2 dropped the gap after the last item, unless it was the term's only one
  &:last-child:not(:nth-child(2)) {
    margin-bottom: 0;
  }
}

.course-title {
  font-size: 1rem;
  line-height: 1.2;
  text-align: center;
  white-space: normal;
}

.course-action {
  margin: 12px 0 12px 16px;

  .v-icon {
    padding: 0;
  }
}

.active {
  background-color: #42b983;
}

.inactive {
  background-color: #7f7f7f;
}

.historic {
  background-color: cornflowerblue;
}
</style>
