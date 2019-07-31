<template v-if="questions">
   
  <v-card>
    <v-card-title>
      <v-text-field
        v-model="search"
        append-icon="search"
        label="Search"
        single-line
        hide-details></v-text-field>
      <v-divider
          class="mx-4"
          inset
          vertical>
      </v-divider>
      <v-dialog v-model="dialog" max-width="1000px">
          <template v-slot:activator="{ on }">
            <v-btn color="primary" dark class="mb-2" @click="open">New Item</v-btn>
          </template>
          <v-card>
            <v-card-title>
              <span class="headline">{{ formTitle() }}</span>
            </v-card-title>

            <v-card-text v-if="editedItem">
              <v-container grid-list-md fluid>
                <v-layout column wrap>
                    <v-flex xs24 sm12 md8>
                      <v-text-field v-model="editedItem.title" label="Title"></v-text-field>
                    </v-flex>
                    <v-flex xs24 sm12 md12>
                      <v-text-field textarea rows="10" v-model="editedItem.content" 
                      label="Content"></v-text-field>
                    </v-flex>
                    <v-flex xs12 sm6 md10>
                      <v-switch v-model="editedItem.correctZero" class="ma-4" label="Correct"></v-switch>
                      <v-text-field textarea rows="3" v-model="editedItem.optionZero" 
                      label="Option One"></v-text-field>
                    </v-flex>
                    <v-flex xs24 sm12 md12>
                      <v-switch v-model="editedItem.correctOne" class="ma-4" label="Correct"></v-switch>
                      <v-text-field textarea rows="3" v-model="editedItem.optionOne" 
                      label="Option Two"></v-text-field>
                    </v-flex>
                    <v-flex xs24 sm12 md12>
                      <v-switch v-model="editedItem.correctTwo" class="ma-4" label="Correct"></v-switch>
                      <v-text-field textarea rows="3" v-model="editedItem.optionTwo" 
                      label="Option Three"></v-text-field>
                    </v-flex>
                    <v-flex xs24 sm12 md12>
                      <v-switch v-model="editedItem.correctThree" class="ma-4" label="Correct"></v-switch>
                      <v-text-field textarea rows="3" v-model="editedItem.optionThree" 
                      label="Option Four"></v-text-field>
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
    :items="questions"
    :items-per-page="10"
    :search="search"
    class="elevation-1">
    <template slot="items" slot-scope="props">
        <tr>
          <td class="text-left" 
          @click="props.expanded = !props.expanded"
          v-html="convertMarkDown(props.item.content, props.item.image)"></td>
          <td>{{props.item.difficulty}}</td>
          <td>{{props.item.numberOfAnswers}}</td>
          <td>{{props.item.title}}</td>
          <td><span v-if="props.item.active">TRUE</span><span v-else>FALSE</span></td>
          <td>
            <v-icon small class="mr-2" @click="editItem(props.item.id)">edit</v-icon>
            <v-icon small @click="deleteItem(props.item.id)">delete</v-icon></td>
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
  </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from "vue-property-decorator";
import { Question, QuestionDto } from "@/models/question/Question";
import QuestionForm from "@/models/question/QuestionForm";
import Option from "@/models/question/Option";
import RemoteServices from "@/services/RemoteServices";
import { convertMarkDown } from "@/scripts/script";
import Image from "@/models/student/Image";

@Component
export default class QuestionsMangement extends Vue {
  questions: Question[] | null = null;
  editedItem: QuestionForm = new QuestionForm();
  editedId: number = -1;
  dialog: boolean = false;

  constructor() {
    super();
  }

  data () {
      return {
        dialog: this.dialog,
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
        editedId: this.editedId,
        editedItem: this.editedItem,
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

  formTitle() {
    return this.editedId === -1 ? "New Question" : "Edit Question"
  }

  open() {
    this.editedItem = new QuestionForm();
    this.editedItem.title = '';
    this.editedItem.content = '';
    this.editedItem.optionZero = '';
    this.editedItem.correctZero = false;
    this.editedItem.optionOne = '';
    this.editedItem.correctOne = false;
    this.editedItem.optionTwo = '';
    this.editedItem.correctTwo = false;
    this.editedItem.optionThree = '';
    this.editedItem.correctThree = false;
    this.dialog = true;
  }

  editItem(item: number) {
    if (this.questions !== null) {
      var question = this.questions.find(question => question.id === item);
      if (question) {
        this.editedId = question.id;
        this.editedItem = new QuestionForm();
        this.editedItem.title = question.title;
        this.editedItem.content = question.content !== null ? question.content : '';
        this.editedItem.optionZero = question.options[0].content;
        this.editedItem.correctZero = question.options[0].correct;
        this.editedItem.optionOne = question.options[1].content;
        this.editedItem.correctOne = question.options[1].correct;
        this.editedItem.optionTwo = question.options[2].content;
        this.editedItem.correctTwo = question.options[2].correct;
        this.editedItem.optionThree = question.options[3].content;
        this.editedItem.correctThree = question.options[3].correct;
        this.dialog = true;
      }
    }
  }

  deleteItem (item: number) {
    if (this.questions != null) {
      // const index = this.questions.find(question => question.id === item)
      // confirm('Are you sure you want to delete this item?') && this.questions.splice(index, 1)
      confirm('Are you sure you want to delete this item?')
    }
  }

  close () {
    this.dialog = false
    setTimeout(() => {
      this.editedItem = new QuestionForm()
      this.editedId = -1
    }, 300)
  }

  save () {
    if (this.questions !== null && this.editedId > -1) {
      var question = this.questions.find(question => question.id === this.editedId);
      if (question !== null) {
        var clone = {...question, 
          options: question.options.map(option => Object.assign({}, option)),
          image: Object.assign({}, question.image)};
          clone.title = this.editedItem.title;
          clone.content = this.editedItem.content;
          clone.options[0].content = this.editedItem.optionZero;
          clone.options[0].correct = this.editedItem.correctZero;
          clone.options[1].content = this.editedItem.optionOne;
          clone.options[1].correct = this.editedItem.correctOne;
          clone.options[2].content = this.editedItem.optionTwo;
          clone.options[2].correct = this.editedItem.correctTwo;
          clone.options[3].content = this.editedItem.optionThree;
          clone.options[3].correct = this.editedItem.correctThree;
        RemoteServices.updateQuestion(this.editedId, clone).then(result => {
          question.title = this.editedItem.title;
          question.content = this.editedItem.content;
          question.options[0].content = this.editedItem.optionZero;
          question.options[0].correct = this.editedItem.correctZero;
          question.options[1].content = this.editedItem.optionOne;
          question.options[1].correct = this.editedItem.correctOne;
          question.options[2].content = this.editedItem.optionTwo;
          question.options[2].correct = this.editedItem.correctTwo;
          question.options[3].content = this.editedItem.optionThree;
          question.options[3].correct = this.editedItem.correctThree;
        });
      }
      
    } else {
      confirm('I\'m going to create the question')

    }
    this.close()
  }
}
</script>

<style lang="scss"></style>
