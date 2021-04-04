<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
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

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Course from '@/models/user/Course';

@Component
export default class EditCourseDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Course, required: true }) readonly course!: Course;

  editCourse!: Course;
  isCreateCourse: boolean = false;

  created() {
    this.editCourse = new Course(this.course);

    this.isCreateCourse = !!this.editCourse.name;
  }

  async saveCourse() {
    if (
      this.editCourse &&
      (!this.editCourse.name ||
        !this.editCourse.acronym ||
        !this.editCourse.academicTerm)
    ) {
      await this.$store.dispatch(
        'error',
        'Course must have name, acronym and academicTerm'
      );
      return;
    }

    if (this.editCourse && this.editCourse.courseExecutionId == null) {
      try {
        const result = await RemoteServices.createExternalCourse(
          this.editCourse
        );
        this.$emit('new-course', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
