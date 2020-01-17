<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="questions"
      :search="search"
      multi-sort
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />

          <v-spacer />
          <v-btn color="primary" dark @click="newQuestion">New Question</v-btn>
        </v-card-title>
      </template>

      <template v-slot:item.content="{ item }">
        <p
          v-html="convertMarkDownNoFigure(item.content, null)"
          @click="showQuestionDialog(item)"
        />
      </template>

      <template v-slot:item.topics="{ item }">
        <v-form>
          <v-autocomplete
            v-model="item.topics"
            :items="topics"
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
                <v-list-item-title v-html="data.item.name" />
              </v-list-item-content>
            </template>
          </v-autocomplete>
        </v-form>
      </template>

      <template v-slot:item.difficulty="{ item }">
        <v-chip
          v-if="item.difficulty"
          :color="getDifficultyColor(item.difficulty)"
          dark
          >{{ item.difficulty + "%" }}</v-chip
        >
      </template>

      <template v-slot:item.status="{ item }">
        <v-select
          v-model="item.status"
          :items="statusList"
          dense
          @change="setStatus(item.id, item.status)"
        >
          <template v-slot:selection="{ item }">
            <v-chip :color="getStatusColor(item)" small>
              <span>{{ item }}</span>
            </v-chip>
          </template>
        </v-select>
      </template>

      <template v-slot:item.sortingCreationDate="{ item }">
        {{ item.stringCreationDate }}
      </template>

      <template v-slot:item.image="{ item }">
        <v-file-input
          show-size
          dense
          small-chips
          @change="handleFileUpload($event, item)"
          accept="image/*"
        />
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="showQuestionDialog(item)"
              >visibility</v-icon
            >
          </template>
          <span>Show Question</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon small class="mr-2" v-on="on" @click="editQuestion(item)"
              >edit</v-icon
            >
          </template>
          <span>Edit Question</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="duplicateQuestion(item)"
              >cached</v-icon
            >
          </template>
          <span>Duplicate Question</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="deleteQuestion(item)"
              color="red"
              >delete</v-icon
            >
          </template>
          <span>Delete Question</span>
        </v-tooltip>
      </template>
    </v-data-table>

    <v-dialog v-model="editQuestionDialog" max-width="75%">
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
                <v-text-field v-model="currentQuestion.title" label="Title" />
              </v-flex>
              <v-flex xs24 sm12 md12>
                <vue-simplemde
                  v-model="currentQuestion.content"
                  class="question-textarea"
                  :configs="markdownConfigs"
                />
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
                />
                <vue-simplemde
                  v-model="currentQuestion.options[index - 1].content"
                  class="option-textarea"
                  :configs="markdownConfigs"
                />
              </v-flex>
            </v-layout>
          </v-container>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="blue darken-1" @click="closeDialogue">Cancel</v-btn>
          <v-btn color="blue darken-1" @click="saveQuestion">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog v-model="showQuestion" max-width="75%">
      <v-card v-if="currentQuestion">
        <v-card-title>
          <span class="headline">{{ currentQuestion.title }}</span>
        </v-card-title>
        <v-card-text>
          <v-container grid-list-md fluid>
            <v-layout column wrap>
              <v-flex class="text-left" xs24 sm12 md8>
                <QuestionView :question="currentQuestion" />
              </v-flex>
            </v-layout>
          </v-container>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="blue darken-1" @click="closeQuestionDialog"
            >Close</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import RemoteServices from "@/services/RemoteServices";
import {
  convertMarkDown,
  convertMarkDownNoFigure
} from "@/services/ConvertMarkdownService";
import Question from "@/models/management/Question";
import Image from "@/models/management/Image";
import Topic from "@/models/management/Topic";
import QuestionView from "@/views/utils/QuestionView.vue";

@Component({
  components: { QuestionView, QuestionVue: QuestionView }
})
export default class QuestionsView extends Vue {
  questions: Question[] = [];
  currentQuestion: Question | null = null;
  topics: Topic[] = [];
  editQuestionDialog: boolean = false;
  showQuestion: boolean = false;
  search: string = "";
  statusList = ["DISABLED", "AVAILABLE", "REMOVED"];

  // https://github.com/F-loat/vue-simplemde/blob/master/doc/configuration_en.md
  markdownConfigs: object = {
    status: false,
    spellChecker: false,
    insertTexts: {
      image: ["![image][image]", ""]
    }
  };

  headers: object = [
    { text: "Question", value: "content", align: "left" },
    {
      text: "Topics",
      value: "topics",
      align: "center",
      sortable: false
    },
    { text: "Difficulty", value: "difficulty", align: "center" },
    { text: "Answers", value: "numberOfAnswers", align: "center" },
    { text: "Title", value: "title", align: "center" },
    { text: "Status", value: "status", align: "center" },
    {
      text: "Creation Date",
      value: "sortingCreationDate",
      align: "center"
    },
    {
      text: "Image",
      value: "image",
      align: "center",
      sortable: false
    },
    {
      text: "Actions",
      value: "action",
      align: "center",
      sortable: false
    }
  ];

  async created() {
    await this.$store.dispatch("loading");
    try {
      [this.topics, this.questions] = await Promise.all([
        RemoteServices.getTopics(),
        RemoteServices.getQuestions()
      ]);
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
    await this.$store.dispatch("clearLoading");
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
    this.editQuestionDialog = true;
  }

  async setStatus(questionId: number, status: string) {
    try {
      await RemoteServices.setQuestionStatus(questionId, status);
      let question = this.questions.find(
        question => question.id === questionId
      );
      if (question) {
        question.status = status;
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
    this.editQuestionDialog = true;
  }

  editQuestion(question: Question) {
    this.currentQuestion = question;
    this.editQuestionDialog = true;
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
    this.editQuestionDialog = false;
  }

  getDifficultyColor(difficulty: number) {
    if (difficulty < 25) return "green";
    else if (difficulty < 50) return "lime";
    else if (difficulty < 75) return "orange";
    else return "red";
  }

  getStatusColor(status: string) {
    if (status === "REMOVED") return "red";
    else if (status === "DISABLED") return "orange";
    else return "green";
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

<style lang="scss" scoped>
.question-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 200px !important;
  }
}
.option-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 100px !important;
  }
}
</style>
