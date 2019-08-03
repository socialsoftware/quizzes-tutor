<template v-if="topics.size == 0 ">
   
  <v-card>
    <v-card-title>
      <v-flex xs12 sm6 md6>
        <v-text-field
          v-model="search"
          append-icon="search"
          label="Search"
          single-line
          hide-details></v-text-field>
      </v-flex>
      <v-divider
          class="mx-4"
          inset
          vertical>
      </v-divider>
      <v-spacer></v-spacer>
        <v-btn color="primary" dark class="mb-2" @click="open">New Topic</v-btn>
      <v-dialog v-model="dialog" max-width="1000px">
          <v-card>
            <v-card-title>
              <span class="headline">{{ formTitle() }}</span>
            </v-card-title>

            <p class="red--text" v-if="error">
              {{error}}
            </p>

            <v-card-text>
              <v-container grid-list-md fluid>
                <v-layout column wrap>
                    <v-flex xs24 sm12 md8>
                      <v-text-field v-model="editedTopic" label="Topic"></v-text-field>
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
    class="elevation-1">
    <template slot="items" slot-scope="props">
        <tr>
          <td class="text-left">{{props.item}}</td>
          <td>
            <v-icon small class="mr-2" @click="editItem(props.item)">edit</v-icon>
            <v-icon small class="mr-2" @click="deleteItem(props.item)">delete</v-icon>
          </td>
        </tr>
    </template>
  </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit, Watch } from "vue-property-decorator";
import RemoteServices from "@/services/RemoteServices";

@Component
export default class TopicsView extends Vue {
  topics: string[] = [];
  editedTopic: string | null = null;
  oldTopic: string | null = null;
  error: string | null = null;
  dialog: boolean = false;

  constructor() {
    super();
  }

  data () {
      return {
        dialog: this.dialog,
        search: "",
        headers: [
          { text: 'Topic', value: 'topic', align: 'left', width: "50%" },
          { text: 'Actions', value: 'action', align: 'center', width: "10%",  sortable: false },
        ],
        topics: this.topics,
        editedTopic: this.editedTopic,
        error: this.error,
      }
  }

  beforeMount() {
    RemoteServices.getTopics().then(result => {
      this.topics = result.data;
    });
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
    return this.editedTopic === null ? "New Topic" : "Edit Topic"
  }

  open() {
    this.editedTopic = null
    this.oldTopic = null;
    this.error = null;
    this.dialog = true;
  }

  editTopic(topic: string) {
    this.editedTopic = topic;
    this.oldTopic = topic;
    this.dialog = true;
  }

  deleteItem (topic: string) {
      if (confirm('Are you sure you want to delete this topic?')) {
        RemoteServices.deleteTopic(topic)
        .then(response => {
          this.topics = this.topics.filter(t => t != topic);
        })
        .catch((error) => {
          if (error.response) {
            confirm(error.response.data.message)
            console.log(error.response.data.message);
          } else if (error.request) {
            confirm("No response received")
          } else {
            confirm("Error")
          }
        });   
      }
  }

  close () {
    this.error = null;
    this.dialog = false
  }

  save () {
    if (this.oldTopic !== null) {
      RemoteServices.updateTopic(this.oldTopic, this.editedTopic)
      .then(response => {
        this.topics = this.topics.filter(topic => topic !== this.oldTopic);
        this.topics.unshift(this.editedTopic)
      })
      .catch((error) => {
        if (error.response) {
          this.error = error.response.data.message;
        } else if (error.request) {
          this.error = "No response received";
        } else {
          this.error = "Error";
        }
        this.dialog = true;
      });  
    } else {
      RemoteServices.createTopic(this.editedTopic)
        .then(response => {
          this.topics.unshift(this.editedTopic);
        })
        .catch((error) => {
          if (error.response) {
            this.error = error.response.data.message;
            console.log(error.response.data.message);
          } else if (error.request) {
            this.error = "No response received";
          } else {
            this.error = "Error";
          }
          this.dialog = true;
      });   
    }
    this.close()
  }
}
</script>

<style lang="scss"></style>
