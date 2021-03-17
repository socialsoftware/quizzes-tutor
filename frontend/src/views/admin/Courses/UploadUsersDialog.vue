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
        <span class="headline"> Upload Users </span>
        <v-tooltip bottom>
          <template v-slot:activator="{ on, attrs }">
            <v-icon color="blue darken-1" dark v-bind="attrs" v-on="on"
              >info</v-icon
            >
          </template>
          <v-card-text>
            <div>The file to upload must follow the rule:</div>
            <div>email,name,['student'|'teacher']</div>
            <div>
              When omitted the third column, by default is considered a student
            </div>
          </v-card-text>
        </v-tooltip>
      </v-card-title>

      <v-file-input
        show-size
        dense
        small-chips
        label="Select a .csv file"
        v-model="chosenFile"
        accept=".csv"
      />

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="red darken-1"
          @click="$emit('close-dialog')"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn
          color="green darken-1"
          @click="uploadUsers(course)"
          data-cy="uploadFileButton"
          >Upload File</v-btn
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
export default class UploadUsersDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Course, required: true }) readonly course!: Course;

  chosenFile: File | null = null;

  async uploadUsers(course: Course) {
    try {
      if (course.courseExecutionId != null && this.chosenFile != null) {
        let updatedCourse = await RemoteServices.registerExternalUsersCsvFile(
          this.chosenFile,
          course.courseExecutionId
        );
        confirm('File was uploaded!');

        this.$emit('users-uploaded', updatedCourse);
      } else {
        await this.$store.dispatch(
          'error',
          'In order to upload users, it must be selected a file'
        );
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
