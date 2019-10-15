<template>
  <v-content>
    <assessment-form
      @switchMode="changeMode"
      @updateAssessment="updateAssessment"
      :edit-mode="editMode"
      :assessment="assessment"
    ></assessment-form>
    <assessment-list
      v-if="!editMode"
      @editAssessment="editAssessment"
      @deleteAssessment="deleteAssessment"
      :assessments="assessments"
    ></assessment-list>
  </v-content>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import RemoteServices from "@/services/RemoteServices";
import Assessment from "@/models/management/Assessment";
import AssessmentForm from "@/views/management/assessments/AssessmentForm.vue";
import AssessmentList from "@/views/management/assessments/AssessmentList.vue";

@Component({
  components: {
    AssessmentForm,
    AssessmentList
  }
})
export default class AssessmentsView extends Vue {
  assessments: Assessment[] = [];
  assessment: Assessment | null = new Assessment();
  editMode: boolean = false;

  // noinspection JSUnusedGlobalSymbols
  async created() {
    try {
      this.assessments = await RemoteServices.getAssessments();
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
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
      this.assessment = this.assessments.filter(
        assessment => assessment.id === assessmentId
      )[0];
      this.editMode = true;
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }

  updateAssessment(updatedAssessment: Assessment) {
    this.assessments = this.assessments.filter(
      assessment => assessment.id !== updatedAssessment.id
    );
    this.assessments.push(updatedAssessment);
    this.editMode = false;
    this.assessment = null;
  }

  deleteAssessment(assessmentId: number) {
    this.assessments = this.assessments.filter(
      assessment => assessment.id !== assessmentId
    );
  }
}
</script>

<style lang="scss"></style>
