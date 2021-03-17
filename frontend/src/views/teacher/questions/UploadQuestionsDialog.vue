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
        <span class="headline"> Import Questions </span>
        <v-tooltip bottom>
          <template v-slot:activator="{ on, attrs }">
            <v-icon color="blue darken-1" dark v-bind="attrs" v-on="on"
              >info</v-icon
            >
          </template>
          <v-card-text>
            <div>The questions will be created in this course</div>
          </v-card-text>
        </v-tooltip>
      </v-card-title>

      <v-file-input
        show-size
        dense
        small-chips
        label="Select a .xml file"
        v-model="chosenFile"
        accept=".xml"
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
          @click="uploadQuestions()"
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
export default class UploadQuestionsDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;

  chosenFile: File | null = null;

  async uploadQuestions() {
    try {
      if (this.chosenFile != null) {
        let uploadedQuestions = await RemoteServices.importQuestions(
          this.chosenFile
        );
        confirm('File was uploaded!');

        this.$emit('questions-uploaded', uploadedQuestions);
      } else {
        await this.$store.dispatch(
          'error',
          'In order to import questions, it must be selected a file'
        );
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
