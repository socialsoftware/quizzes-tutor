<template>
  <v-card class="table">
    <v-card-title>
      <v-spacer></v-spacer>
      <v-btn color="primary" dark class="mb-2" @click="newQuestion"
        >New Question</v-btn
      >
      <v-dialog v-model="dialog" max-width="1000px">
        <v-card>
          <v-card-title>
            <span class="headline">
              {{
                currentQuestion && currentQuestion.id === null
                  ? "New Question"
                  : "Edit Question"
              }}
            </span>
          </v-card-title>

          <v-card-text v-if="currentQuestion">
            <v-container grid-list-md fluid>
              <v-layout column wrap>
                <v-flex xs24 sm12 md8>
                  <v-text-field
                    v-model="currentQuestion.title"
                    label="Title"
                  ></v-text-field>
                </v-flex>
                <v-flex xs24 sm12 md12>
                  <v-textarea
                    outline
                    rows="10"
                    v-model="currentQuestion.content"
                    label="Content"
                  ></v-textarea>
                </v-flex>
                <v-flex
                  xs24
                  sm12
                  md12
                  v-for="index in currentQuestion.options.length"
                  :key="index"
                >
                  <v-switch
                    v-model="currentQuestion.options[index - 1].correct"
                    class="ma-4"
                    label="Correct"
                  ></v-switch>
                  <v-textarea
                    outline
                    :rows="index"
                    v-model="currentQuestion.options[index - 1].content"
                    :label="'Option ' + index"
                  ></v-textarea>
                </v-flex>
              </v-layout>
            </v-container>
          </v-card-text>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" text @click="closeDialogue"
              >Cancel</v-btn
            >
            <v-btn color="blue darken-1" text @click="saveQuestion">Save</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
      <v-dialog v-model="showQuestion" max-width="1000px">
        <v-card v-if="currentQuestion">
          <v-card-title>
            <span class="headline">{{ currentQuestion.title }}</span>
          </v-card-title>
          <v-card-text>
            <v-container grid-list-md fluid>
              <v-layout column wrap>
                <v-flex class="text-left" xs24 sm12 md8>
                  <p v-html="renderQuestion(currentQuestion)"></p>
                </v-flex>
              </v-layout>
            </v-container>
          </v-card-text>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" text @click="closeQuestionDialog"
              >Close</v-btn
            >
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-card-title>

    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="questions"
      :search="search"
      multi-sort
      :items-per-page="10"
      show-expand
    >
      <template v-slot:top>
        <v-text-field
          v-model="search"
          label="Search Content"
          class="mx-2"
        ></v-text-field>
      </template>

      <template v-slot:item.content="{ item }">
        <p v-html="convertMarkDownNoFigure(item.content, null)"></p>
      </template>

      <template v-slot:item.topics="{ item }">
        <v-form>
          <v-autocomplete
            v-model="item.topics"
            :items="topics"
            chips
            multiple
            return-object
            item-text="name"
            item-value="name"
            @change="saveTopics(item.id)"
          >
            <template v-slot:selection="data">
              <v-chip
                v-bind="data.attrs"
                :input-value="data.selected"
                close
                @click="data.select"
                @click:close="removeTopic(item.id, data.item)"
              >
                {{ data.item.name }}
              </v-chip>
            </template>
            <template v-slot:item="data">
              <v-list-item-content>
                <v-list-item-title v-html="data.item.name"></v-list-item-title>
              </v-list-item-content>
            </template>
          </v-autocomplete>
        </v-form>
      </template>

      <template v-slot:item.active="{ item }">
        <v-btn text small @click="switchActive(item.id)">
          <span v-if="item.active">Enabled</span><span v-else>Disabled</span>
        </v-btn>
      </template>

      <template v-slot:item.image="{ item }">
        <v-file-input
          small-chips
          show-size
          outlined
          dense
          @change="handleFileUpload($event, item)"
          accept="image/*"
          label="File input"
        ></v-file-input>
      </template>

      <template v-slot:item.action="{ item }">
        <v-icon small class="mr-2" @click="showQuestionDialog(item)"
          >visibility</v-icon
        >
        <v-icon small class="mr-2" @click="editQuestion(item)">edit</v-icon>
        <v-icon small class="mr-2" @click="duplicateQuestion(item)"
          >cached</v-icon
        >
        <v-icon small class="mr-2" @click="deleteQuestion(item)">delete</v-icon>
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
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import RemoteServices from "@/services/RemoteServices";
import {
  convertMarkDown,
  convertMarkDownNoFigure
} from "@/services/ConvertMarkdownService";
import { Question } from "@/models/management/Question";
import Image from "@/models/management/Image";
import { Topic } from "@/models/management/Topic";

@Component
export default class QuestionsView extends Vue {
  questions: Question[] = [];
  currentQuestion: Question | null = null;
  topics: Topic[] = [];
  dialog: boolean = false;
  showQuestion: boolean = false;
  search: string = "";
  headers: object = [
    { text: "Question", value: "content", align: "left", width: "70%" },
    {
      text: "Topics",
      value: "topics",
      align: "left",
      width: "20%",
      sortable: false
    },
    { text: "Difficulty", value: "difficulty", align: "center", width: "1%" },
    { text: "Answers", value: "numberOfAnswers", align: "center", width: "1%" },
    { text: "Title", value: "title", align: "left", width: "3%" },
    { text: "Active", value: "active", align: "left", width: "1%" },
    {
      text: "Image",
      value: "image",
      align: "center",
      width: "3%",
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
      this.topics = await RemoteServices.getTopics();
      this.questions = await RemoteServices.getQuestions();
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

  convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return convertMarkDownNoFigure(text, image);
  }

  async saveTopics(questionId: number) {
    let question = this.questions.find(question => question.id === questionId);
    if (question) {
      try {
        await RemoteServices.updateQuestionTopics(questionId, question.topics);
      } catch (error) {
        await this.$store.dispatch("error", error);
      }
    }
  }

  showQuestionDialog(question: Question) {
    this.currentQuestion = question;
    this.showQuestion = true;
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

  removeTopic(questionId: number, topic: Topic) {
    let question = this.questions.find(
      (question: Question) => question.id == questionId
    );
    if (question) {
      question.topics = question.topics.filter(
        element => element.id != topic.id
      );
      this.saveTopics(questionId);
    }
  }

  closeQuestionDialog() {
    this.showQuestion = false;
  }

  newQuestion() {
    this.currentQuestion = new Question();
    this.dialog = true;
  }

  async switchActive(questionId: number) {
    try {
      await RemoteServices.questionSwitchActive(questionId);
      let question = this.questions.find(
        question => question.id === questionId
      );
      if (question) {
        question.active = !question.active;
      }
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }

  async handleFileUpload(event: File, question: Question) {
    if (question.id) {
      try {
        const imageURL = await RemoteServices.uploadImage(event, question.id);
        question.image = new Image();
        question.image.url = imageURL;
        confirm("Image " + imageURL + " was uploaded!");
      } catch (error) {
        await this.$store.dispatch("error", error);
      }
    }
  }

  duplicateQuestion(question: Question) {
    this.currentQuestion = new Question(question);
    this.currentQuestion.id = null;
    this.dialog = true;
  }

  editQuestion(question: Question) {
    this.currentQuestion = question;
    this.dialog = true;
  }

  async deleteQuestion(toDeletequestion: Question) {
    if (
      toDeletequestion.id &&
      confirm("Are you sure you want to delete this question?")
    ) {
      try {
        await RemoteServices.deleteQuestion(toDeletequestion.id);
        this.questions = this.questions.filter(
          question => question.id != toDeletequestion.id
        );
      } catch (error) {
        await this.$store.dispatch("error", error);
      }
    }
  }

  closeDialogue() {
    this.dialog = false;
  }

  async saveQuestion() {
    if (
      this.currentQuestion &&
      (!this.currentQuestion.title || !this.currentQuestion.content)
    ) {
      await this.$store.dispatch(
        "error",
        "Question must have title and content"
      );
      return;
    }

    if (this.currentQuestion && this.currentQuestion.id != null) {
      try {
        this.currentQuestion = await RemoteServices.updateQuestion(
          this.currentQuestion
        );

        this.closeDialogue();
      } catch (error) {
        await this.$store.dispatch("error", error);
      }
    } else if (this.currentQuestion) {
      try {
        const question = await RemoteServices.createQuestion(
          this.currentQuestion
        );

        this.questions.unshift(question);
        this.closeDialogue();
      } catch (error) {
        await this.$store.dispatch("error", error);
      }
    }
  }
}
</script>
