<template>
  <v-dialog
    :model-value="dialog"
    @update:model-value="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline"> New Course </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editCourse">
        <p><b>Course Type:</b> {{ editCourse.courseType }}</p>
        <p v-if="isCreateCourse"><b>Name:</b> {{ editCourse.name }}</p>
        <v-text-field
          v-if="!isCreateCourse"
          v-model="editCourse.name"
          label="Name"
          data-cy="courseExecutionNameInput"
        />
        <p>
          <b>Course Execution Type:</b>
          {{ editCourse.courseExecutionType }}
        </p>
        <v-text-field
          v-model="editCourse.acronym"
          label="Acronym"
          data-cy="courseExecutionAcronymInput"
        />
        <v-text-field
          v-model="editCourse.academicTerm"
          label="Academic Term"
          data-cy="courseExecutionAcademicTermInput"
        />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="red darken-1"
          @click="$emit('close-dialog')"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn color="green darken-1" @click="saveCourse()" data-cy="saveButton"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import Course from '@/models/user/Course';

const props = defineProps<{
  dialog: boolean;
  course: Course;
}>();

const emit = defineEmits(['close-dialog', 'new-course', 'update:dialog']);
const store = useStore();

const editCourse = ref<Course | null>(null);
const isCreateCourse = ref(false);

onMounted(() => {
  editCourse.value = new Course(props.course);
  isCreateCourse.value = !!editCourse.value.name;
});

const saveCourse = async () => {
  if (
    editCourse.value &&
    (!editCourse.value.name ||
      !editCourse.value.acronym ||
      !editCourse.value.academicTerm)
  ) {
    store.setError('Course must have name, acronym and academicTerm');
    return;
  }

  if (editCourse.value && editCourse.value.courseExecutionId == null) {
    try {
      const result = await RemoteServices.createExternalCourse(editCourse.value);
      emit('new-course', result);
    } catch (error) {
      store.setError(error as string);
    }
  }
};
</script>
