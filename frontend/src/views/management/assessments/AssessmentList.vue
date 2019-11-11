<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="assessments"
      :search="search"
      multi-sort
      :items-per-page="20"
      class="elevation-1"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-4"
          ></v-text-field>

          <v-spacer></v-spacer>
          <v-btn color="primary" dark class="b-2" @click="newAssessment"
            >New Assessment</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:item.status="{ item }">
        <v-select
          v-model="item.status"
          :items="statusList"
          small-chips
          dense
          @change="setStatus(item.id, item.status)"
        ></v-select>
      </template>
      <template v-slot:item.action="{ item }">
        <v-icon small class="mr-2" @click="editAssessment(item.id)"
          >edit</v-icon
        >
        <v-icon small class="mr-2" @click="deleteAssessment(item.id)"
          >delete</v-icon
        >
      </template>
    </v-data-table>
    <v-dialog
      v-model="dialog"
      @keydown.esc="closeAssessment"
      fullscreen
      hide-overlay
      max-width="1000px"
    >
      <v-card v-if="assessment">
        <v-toolbar dark color="primary">
          <v-toolbar-title>{{ assessment.title }}</v-toolbar-title>
          <div class="flex-grow-1"></div>
          <v-toolbar-items>
            <v-btn dark color="primary" text @click="closeAssessment"
              >Close</v-btn
            >
          </v-toolbar-items>
        </v-toolbar>

        <v-card-text>
          <v-container grid-list-md fluid>
            <v-layout column wrap>
              <ol>
                <li
                  v-for="question in assessment.questions"
                  :key="question.sequence"
                  class="text-left"
                >
                  <span
                    v-html="convertMarkDown(question.content, question.image)"
                  ></span>
                  <ul>
                    <li v-for="option in question.options" :key="option.number">
                      <span
                        v-html="convertMarkDown(option.content, null)"
                        v-bind:class="[
                          option.correct ? 'font-weight-bold' : ''
                        ]"
                      ></span>
                    </li>
                  </ul>
                  <br />
                </li>
              </ol>
            </v-layout>
          </v-container>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn dark color="primary" text @click="closeAssessment"
            >close</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from "vue-property-decorator";
import RemoteServices from "@/services/RemoteServices";
import { convertMarkDown } from "@/services/ConvertMarkdownService";
import Image from "@/models/management/Image";
import Assessment from "@/models/management/Assessment";

@Component
export default class AssessmentList extends Vue {
  @Prop({ type: Array, required: true }) readonly assessments!: Assessment[];
  assessment: Assessment | null = null;
  search: string = "";
  statusList = ["DISABLED", "AVAILABLE", "REMOVED"];
  dialog: boolean = false;
  headers: object = [
    { text: "Title", value: "title", align: "left", width: "30%" },
    { text: "Status", value: "status", align: "left", width: "1%" },
    {
      text: "Actions",
      value: "action",
      align: "center",
      width: "1%",
      sortable: false
    }
  ];

  closeAssessment() {
    this.dialog = false;
    this.assessment = null;
  }

  async setStatus(assessmentId: number, status: string) {
    try {
      await RemoteServices.setAssessmentStatus(assessmentId, status);
      let assessment = this.assessments.find(
        assessment => assessment.id === assessmentId
      );
      if (assessment) {
        assessment.status = status;
      }
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }

  newAssessment() {
    this.$emit("newAssessment");
  }

  editAssessment(assessmentId: number) {
    this.$emit("editAssessment", assessmentId);
  }

  async deleteAssessment(assessmentId: number) {
    if (confirm("Are you sure you want to delete this assessment?")) {
      try {
        await RemoteServices.deleteAssessment(assessmentId);
        this.$emit("deleteAssessment", assessmentId);
      } catch (error) {
        await this.$store.dispatch("error", error);
      }
    }
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss"></style>
