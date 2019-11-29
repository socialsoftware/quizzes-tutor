<template v-if="topics">
  <v-card class="table">
    <v-card-title>
      <v-text-field
        v-model="search"
        append-icon="search"
        label="Search"
        single-line
        hide-details
      />
      <v-spacer />
      <v-btn color="primary" dark @click="newTopic">New Topic</v-btn>
      <v-dialog v-model="dialog" max-width="75%">
        <v-card>
          <v-card-title>
            <span class="headline">{{ formTitle() }}</span>
          </v-card-title>

          <v-card-text v-if="editedTopic">
            <v-container grid-list-md fluid>
              <v-layout column wrap>
                <v-flex xs24 sm12 md8>
                  <v-text-field v-model="editedTopic.name" label="Topic" />
                </v-flex>
              </v-layout>
            </v-container>
          </v-card-text>

          <v-card-actions>
            <v-spacer />
            <v-btn color="blue darken-1" @click="closeDialogue">Cancel</v-btn>
            <v-btn color="blue darken-1" @click="saveTopic">Save</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-card-title>
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="topics"
      :search="search"
      disable-pagination
      class="elevation-1"
      :items-per-page="50"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon small class="mr-2" v-on="on" @click="editTopic(item)"
              >edit</v-icon
            >
          </template>
          <span>Edit Topic</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="deleteTopic(item)"
              color="red"
              >delete</v-icon
            >
          </template>
          <span>Delete Topic</span>
        </v-tooltip>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import RemoteServices from "@/services/RemoteServices";
import Topic from "@/models/management/Topic";

@Component
export default class TopicsView extends Vue {
  topics: Topic[] = [];
  editedTopic: Topic = new Topic();
  dialog: boolean = false;
  search: string = "";
  headers: object = [
    { text: "Topic", value: "name", align: "left", width: "50%" },
    {
      text: "Questions",
      value: "numberOfQuestions",
      align: "center",
      width: "10%"
    },
    {
      text: "Actions",
      value: "action",
      align: "center",
      width: "10%",
      sortable: false
    }
  ];

  async created() {
    await this.$store.dispatch("loading");
    try {
      this.topics = await RemoteServices.getTopics();
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
    await this.$store.dispatch("clearLoading");
  }

  customFilter(value: string, search: string) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
      search != null &&
      typeof value === "string" &&
      value.toLocaleLowerCase().indexOf(search.toLocaleLowerCase()) !== -1
    );
  }

  formTitle() {
    return this.editedTopic === null ? "New Topic" : "Edit Topic";
  }

  newTopic() {
    this.editedTopic = new Topic();
    this.dialog = true;
  }

  closeDialogue() {
    this.dialog = false;
  }

  editTopic(topic: Topic) {
    this.editedTopic = topic;
    this.dialog = true;
  }

  async deleteTopic(toDeleteTopic: Topic) {
    if (confirm("Are you sure you want to delete this topic?")) {
      try {
        await RemoteServices.deleteTopic(toDeleteTopic);
        this.topics = this.topics.filter(
          topic => topic.id !== toDeleteTopic.id
        );
      } catch (error) {
        await this.$store.dispatch("error", error);
      }
    }
  }

  async saveTopic() {
    try {
      if (this.editedTopic.id) {
        this.editedTopic = await RemoteServices.updateTopic(this.editedTopic);
        this.topics = this.topics.filter(
          topic => topic.id !== this.editedTopic.id
        );
      } else if (this.editedTopic) {
        this.editedTopic = await RemoteServices.createTopic(this.editedTopic);
      }

      this.topics.unshift(this.editedTopic);
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
    this.closeDialogue();
  }
}
</script>

<style lang="scss" />
