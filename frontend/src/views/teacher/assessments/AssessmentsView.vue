<template>
  <div>
    <assessment-form
      v-if="editMode && assessment"
      @switchMode="changeMode"
      @updateAssessment="updateAssessment"
      :edit-mode="editMode"
      :assessment="assessment"
    />
    <assessment-list
      v-if="!editMode"
      @editAssessment="editAssessment"
      @deleteAssessment="deleteAssessment"
      @newAssessment="newAssessment"
      :assessments="assessments"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import Assessment from '@/models/management/Assessment';
import AssessmentForm from '@/views/teacher/assessments/AssessmentForm.vue';
import AssessmentList from '@/views/teacher/assessments/AssessmentList.vue';

const store = useStore();

const assessments = ref<Assessment[]>([]);
const assessment = ref<Assessment | null>(null);
const editMode = ref(false);

onMounted(async () => {
  store.setLoading();
  try {
    assessments.value = await RemoteServices.getAssessments();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

const changeMode = () => {
  editMode.value = !editMode.value;
  if (editMode.value) {
    assessment.value = new Assessment();
  } else {
    assessment.value = null;
  }
};

const editAssessment = async (assessmentId: number) => {
  try {
    let foundAssessment = assessments.value.find(
      (a) => a.id === assessmentId
    );
    if (foundAssessment) {
      assessment.value = foundAssessment;
      editMode.value = true;
    }
  } catch (error) {
    store.setError(error as string);
  }
};

const updateAssessment = (updatedAssessment: Assessment) => {
  assessments.value = assessments.value.filter(
    (a) => a.id !== updatedAssessment.id
  );
  assessments.value.unshift(updatedAssessment);
  editMode.value = false;
  assessment.value = null;
};

const newAssessment = () => {
  assessment.value = new Assessment();
  editMode.value = true;
};

const deleteAssessment = (assessmentId: number) => {
  assessments.value = assessments.value.filter(
    (a) => a.id !== assessmentId
  );
};
</script>

<style lang="scss" scoped></style>
