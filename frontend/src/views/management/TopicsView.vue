<template v-if="topics.size === 0">
  <v-card>
    <v-card-title>
      <v-flex xs12 sm6 md6>
        <v-text-field
          v-model="search"
          append-icon="search"
          label="Search"
          single-line
          hide-details
        ></v-text-field>
      </v-flex>
      <v-divider class="mx-4" inset vertical> </v-divider>
      <v-spacer></v-spacer>
      <v-btn color="primary" dark class="mb-2" @click="open">New Topic</v-btn>
      <v-dialog v-model="dialog" max-width="1000px">
        <v-card>
          <v-card-title>
            <span class="headline">{{ formTitle() }}</span>
          </v-card-title>

          <p class="red--text" v-if="error">
            {{ error }}
          </p>

          <v-card-text>
            <v-container grid-list-md fluid>
              <v-layout column wrap>
                <v-flex xs24 sm12 md8>
                  <v-text-field
                    v-model="editedTopic"
                    label="Topic"
                  ></v-text-field>
                </v-flex>
              </v-layout>
            </v-container>
          </v-card-text>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" text @click="close">Cancel</v-btn>
            <v-btn color="blue darken-1" text @click="save">Save</v-btn>
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
    >
      <template slot="items" slot-scope="props">
        <tr>
          <td class="text-left">{{ props.item }}</td>
          <td>
            <v-icon small class="mr-2" @click="editTopic(props.item)"
              >edit</v-icon
            >
            <v-icon small class="mr-2" @click="deleteTopic(props.item)"
              >delete</v-icon
            >
          </td>
        </tr>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import RemoteServices from "@/services/RemoteServices";

@Component
export default class TopicsView extends Vue {
  topics: String[] = [];
  editedTopic: string | null = null;
  oldTopic: string | null = null;
  error: string | null = null;
  dialog: boolean = false;
  search: string = "";
  headers: object = [
    { text: "Topic", value: "topic", align: "left", width: "50%" },
    {
      text: "Actions",
      value: "action",
      align: "center",
      width: "10%",
      sortable: false
    }
  ];

  constructor() {
    super();
  }

  // noinspection JSUnusedGlobalSymbols
  async beforeMount() {
    try {
      this.topics = await RemoteServices.getTopics();
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }

  customFilter(items: string[], search: string) {
    return items.filter(
      (topic: string) =>
        JSON.stringify(topic)
          .toLowerCase()
          .indexOf(search.toLowerCase()) !== -1
    );
  }

  formTitle() {
    return this.editedTopic === null ? "New Topic" : "Edit Topic";
  }

  open() {
    this.editedTopic = null;
    this.oldTopic = null;
    this.error = null;
    this.dialog = true;
  }

  editTopic(topic: string) {
    this.editedTopic = topic;
    this.oldTopic = topic;
    this.dialog = true;
  }

  async deleteTopic(topic: string) {
    if (confirm("Are you sure you want to delete this topic?")) {
      try {
        await RemoteServices.deleteTopic(topic);
        this.topics = this.topics.filter(t => t != topic);
      } catch (error) {
        await this.$store.dispatch("error", error);
      }
    }
  }

  close() {
    this.error = null;
    this.dialog = false;
  }

  async save() {
    if (this.oldTopic && this.editedTopic) {
      try {
        await RemoteServices.updateTopic(this.oldTopic, this.editedTopic);
        this.topics = this.topics.filter(topic => topic !== this.oldTopic);
        this.topics.unshift(this.editedTopic);
      } catch (error) {
        await this.$store.dispatch("error", error);
      }
    } else if (this.editedTopic) {
      try {
        await RemoteServices.createTopic(this.editedTopic);
        this.topics.unshift(this.editedTopic);
      } catch (error) {
        await this.$store.dispatch("error", error);
      }
    }
    this.close();
  }
}
</script>

<style lang="scss"></style>
