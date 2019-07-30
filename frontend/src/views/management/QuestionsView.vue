<template>
  <div v-if="questions">
   
  <v-card>
    <v-card-title>
      Questions
      <v-spacer></v-spacer>
      <v-text-field
        v-model="search"
        append-icon="search"
        label="Search"
        single-line
        hide-details></v-text-field>
    </v-card-title> 
    <v-data-table
    :headers="headers"
    :custom-filter="customFilter"
    :items="questions"
    :items-per-page="10"
    :search="search"
    class="elevation-1">
    <template v-slot:top>
      <v-toolbar flat color="white">
        <v-toolbar-title>My CRUD</v-toolbar-title>
        <v-divider
          class="mx-4"
          inset
          vertical
        ></v-divider>
        <v-spacer></v-spacer>
        <v-dialog v-model="dialog" max-width="500px">
          <template v-slot:activator="{ on }">
            <v-btn color="primary" dark class="mb-2" v-on="on">New Item</v-btn>
          </template>
          <v-card>
            <v-card-title>
              <span class="headline">{{ formTitle }}</span>
            </v-card-title>

            <v-card-text>
              <v-container grid-list-md>
                <v-layout wrap>
                  <v-flex xs12 sm6 md4>
                    <v-text-field v-model="editedItem.name" label="Dessert name"></v-text-field>
                  </v-flex>
                  <v-flex xs12 sm6 md4>
                    <v-text-field v-model="editedItem.calories" label="Calories"></v-text-field>
                  </v-flex>
                  <v-flex xs12 sm6 md4>
                    <v-text-field v-model="editedItem.fat" label="Fat (g)"></v-text-field>
                  </v-flex>
                  <v-flex xs12 sm6 md4>
                    <v-text-field v-model="editedItem.carbs" label="Carbs (g)"></v-text-field>
                  </v-flex>
                  <v-flex xs12 sm6 md4>
                    <v-text-field v-model="editedItem.protein" label="Protein (g)"></v-text-field>
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
      </v-toolbar>
    </template>
    <template slot="items" slot-scope="props">
        <tr @click="props.expanded = !props.expanded">
          <td class="text-left" v-html="convertMarkDown(props.item.content, props.item.image)"></td>
          <td class="text-left" v-text="props.item.difficulty"></td>
          <td class="text-left" v-text="props.item.numberOfAnswers"></td>
          <td class="text-left" v-text="props.item.title"></td>
          <td class="text-left"><span v-if="props.item.active">TRUE</span><span v-else>FALSE</span></td>
        </tr>
    </template>
    <template slot="expand" slot-scope="props">
        <v-simple-table>
        <thead>
          <tr>
            <th class="text-left">Option</th>
            <th class="text-left">Correct</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="option in props.item.options" :key="option.id">
            <td class="text-left" v-html="convertMarkDown(option.content)"></td>
            <td><span v-if="option.correct">TRUE</span><span v-else>FALSE</span></td>
          </tr>
        </tbody>
      </v-simple-table>
    </template>
    <template v-slot:item.action="{ item }">
      <v-icon
        small
        class="mr-2"
        @click="editItem(item)"
      >
        edit
      </v-icon>
      <v-icon
        small
        @click="deleteItem(item)"
      >
        delete
      </v-icon>
    </template>
    <template v-slot:no-data>
      <v-btn color="primary" @click="initialize">Reset</v-btn>
    </template>
  </v-data-table>
  </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from "vue-property-decorator";
import { Question, QuestionDto } from "@/models/question/Question";
import RemoteServices from "@/services/RemoteServices";
import { convertMarkDown } from "@/scripts/script";
import Image from "@/models/student/Image";

@Component
export default class QuestionsMangement extends Vue {
  questions: Question[] | null = null;

  constructor() {
    super();
  }

  data () {
      return {
        dialog: false,
        search: "",
        headers: [
          { text: 'Question', value: 'content' },
          { text: 'Difficulty', value: 'difficulty' },
          { text: 'Number of Answers', value: 'numberOfAnswers' },
          { text: 'Title', value: 'title' },
          { text: 'Active', value: 'active' },
          { text: 'Actions', value: 'action', sortable: false },
        ],
        questions: this.questions,
        editedIndex: -1,
        editedItem: {
          content: '',
          title: '',
        },
        defaultItem: {
          content: '',
          title: '',
        },
      }
  }

  customFilter(items: Question[], search: string) {
    return items.filter(
      (question: Question) =>
        JSON.stringify(question)
          .toLowerCase()
          .indexOf(search.toLowerCase()) !== -1
    );
  }

  beforeMount() {
    RemoteServices.getQuestions().then(result => {
      this.questions = result.data.map(
        (question: QuestionDto) => new Question(question)
      );
    });
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss"></style>
