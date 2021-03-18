<template>
  <div>
    <assessment-form
      v-if="editMode"
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

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Assessment from '@/models/management/Assessment';
import AssessmentForm from '@/views/teacher/assessments/AssessmentForm.vue';
import AssessmentList from '@/views/teacher/assessments/AssessmentList.vue';

@Component({
  components: {
    AssessmentForm,
    AssessmentList,
  },
})
export default class AssessmentsView extends Vue {
  assessments: Assessment[] = [];
  assessment: Assessment | null = null;
  editMode: boolean = false;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.assessments = await RemoteServices.getAssessments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  changeMode() {
    this.editMode = !this.editMode;
    if (this.editMode) {
      this.assessment = new Assessment();
    } else {
      this.assessment = null;
    }
  }

  async editAssessment(assessmentId: number) {
    try {
      let assessment = this.assessments.find(
        (assessment) => assessment.id === assessmentId
      );
      if (assessment) {
        this.assessment = assessment;
        this.editMode = true;
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  updateAssessment(updatedAssessment: Assessment) {
    this.assessments = this.assessments.filter(
      (assessment) => assessment.id !== updatedAssessment.id
    );
    this.assessments.unshift(updatedAssessment);
    this.editMode = false;
    this.assessment = null;
  }

  newAssessment() {
    this.assessment = new Assessment();
    this.editMode = true;
  }

  deleteAssessment(assessmentId: number) {
    this.assessments = this.assessments.filter(
      (assessment) => assessment.id !== assessmentId
    );
  }
}
</script>

<style lang="scss" scoped></style>
