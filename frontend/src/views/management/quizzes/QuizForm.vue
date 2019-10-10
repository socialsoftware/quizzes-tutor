<template>
  <v-content>
    <v-card>
      <v-divider class="mx-4" inset vertical> </v-divider>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" dark class="mb-2" @click="switchMode">{{
          editMode ? "Close" : "Create"
        }}</v-btn>
        <v-btn color="primary" dark class="mb-2" v-if="editMode" @click="save"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
    <v-card v-if="editMode">
      <v-card-title>
        <span class="headline">Edit Quiz</span>
        <v-dialog v-model="showQuestion" max-width="1000px">
          <v-card v-if="questionToShow">
            <v-card-title>
              <span class="headline">{{ questionToShow.title }}</span>
            </v-card-title>
            <v-card-text>
              <v-container grid-list-md fluid>
                <v-layout column wrap>
                  <v-flex class="text-left" xs24 sm12 md8>
                    <p v-html="renderQuestion(questionToShow)"></p>
                  </v-flex>
                </v-layout>
              </v-container>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="blue darken-1" text @click="closeShowQuestionDialog"
                >Close</v-btn
              >
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-card-title>
      <v-card-text>
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs18 sm9 md6>
              <v-text-field v-model="quiz.title" label="Title"></v-text-field>
            </v-flex>
            <v-flex xs12 sm6 md4 class="text-left">
              <br />
              <datetime
                type="datetime"
                v-model="quiz.availableDate"
                id="availableDate"
              >
                <label for="availableDate" slot="before">Available Date:</label>
              </datetime>
            </v-flex>
            <v-flex xs12 sm6 md4 class="text-left">
              <datetime
                type="datetime"
                v-model="quiz.conclusionDate"
                id="conclusionDate"
              >
                <label for="conclusionDate" slot="before"
                  >Conclusion Date:</label
                >
              </datetime>
            </v-flex>
            <v-flex xs12 sm6 md4>
              <v-switch
                v-model="quiz.scramble"
                :label="
                  `Scramble: ${
                    quiz.scramble ? quiz.scramble.toString() : 'false'
                  }`
                "
              ></v-switch>
            </v-flex>
          </v-layout>
        </v-container>
        <v-container grid-list-md fluid>
          <v-layout row wrap>
            <v-divider class="mx-4" inset vertical> </v-divider>
            <v-spacer></v-spacer>
            <v-btn
              v-if="quizQuestions.length !== 0"
              color="primary"
              dark
              class="mb-2"
              @click="openShowDialog"
              >Show Quiz</v-btn
            >
          </v-layout>
          <v-data-table
            :headers="headers"
            :items="questions"
            :search="search"
            :custom-filter="customFilter"
            :items-per-page="10"
            :sort-by="['sequence']"
            :sort-desc="[false]"
            :custom-sort="customSort"
            must-sort
            show-expand
          >
            <template v-slot:top>
              <v-text-field
                v-model="search"
                label="Search"
                class="mx-4"
              ></v-text-field>
            </template>
            <template v-slot:item.content="{ item }">
              <div
                class="text-left"
                @click="props.expanded = !props.expanded"
                v-html="convertMarkDownNoFigure(item.content, item.image)"
              ></div>
            </template>

            <template v-slot:item.topics="{ item }">
              <span v-for="topic in item.topics" :key="topic.id">
                {{ topic.name }}
              </span>
            </template>

            <template v-slot:item.action="{ item }">
              <v-icon
                small
                class="mr-2"
                @click="openShowQuestionDialog(item.id)"
              >
                visibility</v-icon
              >
              <v-icon
                v-if="!item.sequence"
                small
                class="mr-2"
                @click="addToQuiz(item.id)"
              >
                add</v-icon
              >
              <v-icon
                v-if="item.sequence"
                small
                class="mr-2"
                @click="removeFromQuiz(item.id)"
              >
                remove</v-icon
              >
              <v-icon
                v-if="item.sequence && item.sequence !== 1"
                small
                class="mr-2"
                @click="moveFirst(item.id)"
              >
                first_page</v-icon
              >
              <v-icon
                v-if="item.sequence && item.sequence !== 1"
                small
                class="mr-2"
                @click="moveLeft(item.id)"
              >
                chevron_left</v-icon
              >
              <v-icon
                v-if="item.sequence && quizQuestions.length > 1"
                small
                class="mr-2"
                @click="openSetPosition(item.id)"
              >
                expand_more</v-icon
              >
              <v-icon
                v-if="item.sequence && item.sequence !== quizQuestions.length"
                small
                class="mr-2"
                @click="moveRight(item.id)"
              >
                chevron_right</v-icon
              >
              <v-icon
                v-if="item.sequence && item.sequence !== quizQuestions.length"
                small
                class="mr-2"
                @click="moveLast(item.id)"
              >
                last_page</v-icon
              >
            </template>

            <template v-slot:expanded-item="{ item }">
              <td :colspan="9">
                <v-simple-table>
                  <thead>
                    <tr>
                      <th class="text-left">Option</th>
                      <th class="text-left">Correct</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="option in item.options" :key="option.id">
                      <td
                        class="text-left"
                        v-html="convertMarkDownNoFigure(option.content, null)"
                      ></td>
                      <td>
                        <span v-if="option.correct">TRUE</span
                        ><span v-else>FALSE</span>
                      </td>
                    </tr>
                  </tbody>
                </v-simple-table>
              </td>
            </template>
          </v-data-table>
        </v-container>
      </v-card-text>
    </v-card>
    <v-dialog v-if="position" v-model="dialog" persistent max-width="200px">
      <v-card>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12" sm="6" md="4">
                <v-text-field v-model="position" label="position" required>
                </v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <div class="flex-grow-1"></div>
          <v-btn color="blue darken-1" text @click="closeSetPosition"
            >Close</v-btn
          >
          <v-btn color="blue darken-1" text @click="saveSetPosition"
            >Save</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog
      v-model="showDialog"
      @keydown.esc="closeShowDialog"
      fullscreen
      hide-overlay
      max-width="1000px"
    >
      <v-card v-if="quiz">
        <v-toolbar dark color="primary">
          <v-toolbar-title>{{ quiz.title }}</v-toolbar-title>
          <div class="flex-grow-1"></div>
          <v-toolbar-items>
            <v-btn dark color="primary" text @click="closeShowDialog"
              >Close</v-btn
            >
          </v-toolbar-items>
        </v-toolbar>

        <v-card-text>
          <v-container grid-list-md fluid>
            <v-layout column wrap>
              <ol>
                <li
                  v-for="question in quiz.questions"
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
          <v-btn dark color="primary" text @click="closeShowDialog"
            >close</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-content>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from "vue-property-decorator";
import { Datetime } from "vue-datetime";
import "vue-datetime/dist/vue-datetime.css";
import RemoteServices from "@/services/RemoteServices";
import {
  convertMarkDown,
  convertMarkDownNoFigure
} from "@/services/ConvertMarkdownService";
import { Quiz } from "@/models/management/Quiz";
import { Question } from "@/models/management/Question";
import Image from "@/models/management/Image";

@Component({
  components: {
    Datetime
  }
})
export default class QuizForm extends Vue {
  @Prop(Quiz) readonly quiz!: Quiz;
  @Prop(Boolean) readonly editMode!: boolean;
  questions: Question[] = [];
  search: string = "";
  showQuestion: boolean = false;
  questionToShow: Question | null | undefined = null;
  quizQuestions: Question[] = [];
  dialog: boolean = false;
  questionPosition: Question | undefined;
  position: number | null = null;
  showDialog: boolean = false;
  headers: object = [
    {
      text: "Sequence",
      value: "sequence",
      align: "left",
      width: "1%"
    },
    {
      text: "Question",
      value: "content",
      align: "left",
      width: "70%",
      sortable: false
    },
    {
      text: "Topics",
      value: "topics",
      align: "left",
      width: "20%",
      sortable: false
    },
    { text: "Difficulty", value: "difficulty", align: "center", width: "1%" },
    { text: "Answers", value: "numberOfAnswers", align: "center", width: "1%" },
    {
      text: "Title",
      value: "title",
      align: "left",
      width: "5%",
      sortable: false
    },
    {
      text: "Actions",
      value: "action",
      align: "center",
      width: "1%",
      sortable: false
    }
  ];

  // noinspection JSUnusedGlobalSymbols
  async created() {
    try {
      this.questions = await RemoteServices.getAvailableQuestions();
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }

  @Watch("quiz")
  onQuizChange() {
    if (this.quiz !== null) {
      let questionIds: number[] = [];
      if (this.quiz.questions) {
        this.quiz.questions.forEach(question => {
          if (!this.quizQuestions.includes(question) && question.id) {
            questionIds.push(question.id);
          }
        });
      }

      this.questions.forEach(question => {
        if (question.id && questionIds.includes(question.id)) {
          question.sequence = questionIds.indexOf(question.id) + 1;
          this.quizQuestions.push(question);
        }
      });
    }
  }

  switchMode() {
    this.cleanQuizQuestions();
    this.$emit("switchMode");
  }

  async save() {
    try {
      this.quiz.type = "TEACHER";
      this.quiz.year = new Date().getFullYear();
      this.quiz.questions = this.quizQuestions;
      let updatedQuiz: Quiz;
      updatedQuiz = await RemoteServices.saveQuiz(this.quiz);
      this.cleanQuizQuestions();
      this.$emit("updateQuiz", updatedQuiz);
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }

  customFilter(value: string, search: string, question: Question) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
      search != null &&
      JSON.stringify(question)
        .toLowerCase()
        .indexOf(search.toLowerCase()) !== -1
    );
  }

  customSort(items: Question[], index: string, isDesc: string) {
    items.sort((a: any, b: any) => {
      if (index == "sequence") {
        if (isDesc == "false") {
          return this.compare(a.sequence, b.sequence);
        } else {
          return this.compare(b.sequence, a.sequence);
        }
      } else {
        if (isDesc == "false") {
          return a[index] < b[index] ? -1 : 1;
        } else {
          return b[index] < a[index] ? -1 : 1;
        }
      }
    });
    return items;
  }

  compare(a: number, b: number) {
    if (a == b) {
      return 0;
    } else if (a == null) {
      return 1;
    } else if (b == null) {
      return -1;
    } else {
      return a < b ? -1 : 1;
    }
  }

  openShowQuestionDialog(questionId: number) {
    this.questionToShow = this.questions.find(
      question => question.id === questionId
    );
    this.showQuestion = true;
  }

  closeShowQuestionDialog() {
    this.questionToShow = null;
    this.showQuestion = false;
  }

  convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return convertMarkDownNoFigure(text, image);
  }

  renderQuestion(question: Question): string {
    let text =
      convertMarkDown(question.content, question.image) + " <br/> <br/> ";
    text =
      text +
      question.options
        .map(
          (option, index) =>
            index.toString() +
            " - " +
            option.content +
            (option.correct ? " (correct)" : "")
        )
        .join(" <br/> ");
    return text;
  }

  addToQuiz(questionId: number) {
    let question = this.questions.find(q => q.id === questionId);
    if (question) {
      question.sequence = this.quizQuestions.length + 1;
      this.quizQuestions.push(question);
    }
  }

  removeFromQuiz(questionId: number) {
    let question = this.questions.find(q => q.id === questionId);
    if (question) {
      let index: number = this.quizQuestions.indexOf(question);
      this.quizQuestions.splice(index, 1);
      question.sequence = null;
      this.quizQuestions.forEach((question, index) => {
        question.sequence = index + 1;
      });
    }
  }

  moveFirst(questionId: number) {
    let question = this.quizQuestions.find(q => q.id === questionId);
    if (question) {
      this.changeQuestionPosition(question, 0);
    }
  }

  moveLast(questionId: number) {
    let question = this.quizQuestions.find(q => q.id === questionId);
    if (question) {
      this.changeQuestionPosition(question, this.quizQuestions.length - 1);
    }
  }

  moveLeft(questionId: number) {
    let question = this.quizQuestions.find(q => q.id === questionId);
    if (question && question.sequence) {
      this.changeQuestionPosition(
        question,
        this.quizQuestions.indexOf(question) - 1
      );
    }
  }

  moveRight(questionId: number) {
    let question = this.quizQuestions.find(q => q.id === questionId);
    if (question && question.sequence) {
      this.changeQuestionPosition(
        question,
        this.quizQuestions.indexOf(question) + 1
      );
    }
  }

  openSetPosition(questionId: number) {
    let question = this.quizQuestions.find(q => q.id === questionId);
    if (question && question.sequence) {
      this.dialog = true;
      this.position = question.sequence;
      this.questionPosition = question;
    }
  }

  closeSetPosition() {
    this.dialog = false;
    this.position = null;
    this.questionPosition = undefined;
  }

  saveSetPosition() {
    if (
      this.questionPosition &&
      this.questionPosition.sequence !== this.position &&
      this.position &&
      this.position > 0 &&
      this.position <= this.quizQuestions.length
    ) {
      this.changeQuestionPosition(this.questionPosition, this.position - 1);
    }
    this.closeSetPosition();
  }

  changeQuestionPosition(question: Question, position: number) {
    if (question.sequence) {
      let currentPosition: number = this.quizQuestions.indexOf(question);
      this.quizQuestions.splice(
        position,
        0,
        this.quizQuestions.splice(currentPosition, 1)[0]
      );
      this.quizQuestions.forEach((question, index) => {
        question.sequence = index + 1;
      });
    }
  }

  cleanQuizQuestions() {
    this.quizQuestions.forEach(question => {
      question.sequence = null;
    });
    this.quizQuestions = [];
  }

  openShowDialog() {
    this.showDialog = true;
    this.quiz.questions = this.quizQuestions;
  }

  closeShowDialog() {
    this.showDialog = false;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss"></style>
