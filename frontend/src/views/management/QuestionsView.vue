<template v-if="questions.size == 0 ">
   
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
        <v-btn color="primary" dark class="mb-2" @click="open">New Question</v-btn>
      <v-dialog v-model="dialog" max-width="1000px">
          <v-card>
            <v-card-title>
              <span class="headline">{{ formTitle() }}</span>
            </v-card-title>

            <p class="red--text" v-if="error">
              {{error}}
            </p>

            <v-card-text v-if="editedItem">
              <v-container grid-list-md fluid>
                <v-layout column wrap>
                    <v-flex xs24 sm12 md8>
                      <v-text-field v-model="editedItem.title" label="Title"></v-text-field>
                    </v-flex>
                    <v-flex xs24 sm12 md12>
                      <v-textarea outline rows="10" v-model="editedItem.content" 
                      label="Content"></v-textarea>
                    </v-flex>
                    <v-flex xs12 sm6 md10>
                      <v-switch v-model="editedItem.correctZero" class="ma-4" label="Correct"></v-switch>
                      <v-textarea outline rows="3" v-model="editedItem.optionZero" 
                      label="Option One"></v-textarea>
                    </v-flex>
                    <v-flex xs24 sm12 md12>
                      <v-switch v-model="editedItem.correctOne" class="ma-4" label="Correct"></v-switch>
                      <v-textarea outline rows="3" v-model="editedItem.optionOne" 
                      label="Option Two"></v-textarea>
                    </v-flex>
                    <v-flex xs24 sm12 md12>
                      <v-switch v-model="editedItem.correctTwo" class="ma-4" label="Correct"></v-switch>
                      <v-textarea outline rows="3" v-model="editedItem.optionTwo" 
                      label="Option Three"></v-textarea>
                    </v-flex>
                    <v-flex xs24 sm12 md12>
                      <v-switch v-model="editedItem.correctThree" class="ma-4" label="Correct"></v-switch>
                      <v-textarea outline rows="3" v-model="editedItem.optionThree" 
                      label="Option Four"></v-textarea>
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
    :search="search"
    loading loading-text="Loading... Please wait"
    :items-per-page="10"
    class="elevation-1">
    <template slot="items" slot-scope="props">
        <tr>
          <td class="text-left" 
          @click="props.expanded = !props.expanded"
          v-html="convertMarkDown(props.item.content, props.item.image)"></td>
          <td class="text-left">
            <v-autocomplete
                v-model="props.item.topics"
                :items="topics"
                filled
                chips
                multiple
                @focus="getSelectedTopics(props.item.id)"
                @change="saveTopics(props.item.id)"
              >
              </v-autocomplete>
          </td>
          <td>{{props.item.difficulty}}</td>
          <td>{{props.item.numberOfAnswers}}</td>
          <td class="text-left">{{props.item.title}}</td>
          <td class="text-left">
            <v-btn text small @click="switchActive(props.item.id)">
              <span v-if="props.item.active">Enabled</span><span v-else>Disabled</span>
            </v-btn>
          </td>
          <td class="text-center">
            <label>
              <input type="file" style="display:none" @change="handleFileUpload($event, props.item.id)"
              accept="image/*" class="input-file">Upload</label>
            <!-- <v-file-input accept="image/*" label="File input"></v-file-input> -->
          </td>
          <td>
            <v-icon small class="mr-2" @click="editItem(props.item.id)">edit</v-icon>
            <v-icon small class="mr-2" @click="duplicateItem(props.item.id)">cached</v-icon>
            <v-icon small class="mr-2" @click="deleteItem(props.item.id)">delete</v-icon>
          </td>
        </tr>
    </template>
    <template slot="expand" slot-scope="props">
        <v-simple-table>
        <thead>
          <tr>
            <th class="text-left">Id</th>
            <th class="text-left">Option</th>
            <th class="text-left">Correct</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="option in props.item.options" :key="option.id">
            <td class="text-left">{{option.id}}</td>
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
import { Component, Vue, Prop, Model, Emit, Watch } from "vue-property-decorator";
import { Question, QuestionDto } from "@/models/question/Question";
import QuestionForm from "@/models/question/QuestionForm";
import Option from "@/models/question/Option";
import RemoteServices from "@/services/RemoteServices";
import { convertMarkDown } from "@/scripts/script";
import Image from "@/models/student/Image";

@Component
export default class QuestionsView extends Vue {
  questions: Question[] = [];
  editedItem: QuestionForm = new QuestionForm();
  editedId: number = -1;
  error: string | null = null;
  dialog: boolean = false;
  topics: string[] = [];

  constructor() {
    super();
  }

  data () {
      return {
        dialog: this.dialog,
        search: "",
        headers: [
          { text: 'Question', value: 'content', align: 'left', width: "50%" },
          { text: 'Topics', value: 'topics', align: 'left', width: "20%" , sortable: false },
          { text: 'Difficulty', value: 'difficulty', align: 'center', width: "1%"  },
          { text: 'Answers', value: 'numberOfAnswers', align: 'center', width: "1%"  },
          { text: 'Title', value: 'title', align: 'left', width: "5%"  },
          { text: 'Active', value: 'active', align: 'left', width: "1%"  },
          { text: 'Image', value: 'image', align: 'center', width: "1%",  sortable: false  },
          { text: 'Actions', value: 'action', align: 'center', width: "1%",  sortable: false },
        ],
        questions: this.questions,
        editedId: this.editedId,
        editedItem: this.editedItem,
        error: this.error,
        topics: this.topics,
      }
  }

  beforeMount() {
    RemoteServices.getTopics().then(result => {
      this.topics = result.data;
        RemoteServices.getQuestions().then(result => {
        this.questions = result.data.map(
          (question: QuestionDto) => new Question(question)
        );
        });
    });
  }
  
  customFilter(items: Question[], search: string) {
    return items.filter(
      (question: Question) =>
        JSON.stringify(question)
          .toLowerCase()
          .indexOf(search.toLowerCase()) !== -1
    );
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  saveTopics(questionId: number) {
    let question = this.questions.find(question => question.id === questionId);
    RemoteServices.updateQuestionTopics(questionId, question.topics)
      .then(response => {
        
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

  formTitle() {
    return this.editedId === -1 ? "New Question" : "Edit Question"
  }

  open() {
    this.editedId = -1
    this.editedItem = new QuestionForm();
    this.error = null;
    this.dialog = true;
  }

  switchActive(item: number) {
    RemoteServices.questionSwitchActive(item)
      .then(response => {
        let question = this.questions.find(question => question.id === item);
        question.active = !question.active;
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

  handleFileUpload(event: Event, questionId: number) {
    let question = this.questions.find(question => question.id === questionId);
    if (question) {
      RemoteServices.uploadImage(event.target.files[0], questionId)
        .then(response => {
          question.image = {...question.image, url: response.data}
          confirm("Image " + response.data +  " was uploaded!")
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

  duplicateItem(item: number) {
    let question = this.questions.find(question => question.id === item);
    if (question) {
      this.editedId = -1;
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

  editItem(item: number) {
    let question = this.questions.find(question => question.id === item);
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

  deleteItem (item: number) {
      const selectedQuestion = this.questions.find(question => question.id === item)
      if (confirm('Are you sure you want to delete this question?')) {
        RemoteServices.deleteQuestion(selectedQuestion.id)
        .then(response => {
          this.questions = this.questions.filter(question => question.id != selectedQuestion.id);
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
    if (this.editedId > -1) {
      let question = this.questions.find(question => question.id === this.editedId);
      if (question !== null) {
        let clone = {title: question.title, content: question.content, 
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
        RemoteServices.updateQuestion(this.editedId, clone)
        .then(response => {
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
      }      
    } else {
      const question = { 
        title: this.editedItem.title,
        content: this.editedItem.content,
        options: [
          {id: -1, content: this.editedItem.optionZero, correct: this.editedItem.correctZero},
          {id: -1, content: this.editedItem.optionOne, correct: this.editedItem.correctOne},
          {id: -1, content: this.editedItem.optionTwo, correct: this.editedItem.correctTwo},
          {id: -1, content: this.editedItem.optionThree, correct: this.editedItem.correctThree}
        ]
      }

      RemoteServices.createQuestion(question)
        .then(response => {
          let result = new Question(response.data);
          this.questions.unshift(result);
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
