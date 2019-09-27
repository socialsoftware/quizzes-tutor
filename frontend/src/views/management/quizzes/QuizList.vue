<template>
  <v-content>
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
        <v-dialog
          v-model="dialog"
          @keydown.esc="closeQuiz"
          fullscreen
          hide-overlay
          max-width="1000px"
        >
          <v-card v-if="quiz">
            <v-toolbar dark color="primary">
              <v-toolbar-title>{{ quiz.title }}</v-toolbar-title>
              <div class="flex-grow-1"></div>
              <v-toolbar-items>
                <v-btn dark color="primary" text @click="closeQuiz"
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
                        v-html="
                          convertMarkDown(question.content, question.image)
                        "
                      ></span>
                      <ul>
                        <li
                          v-for="option in question.options"
                          :key="option.number"
                        >
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
              <v-btn dark color="primary" text @click="closeQuiz">close</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-card-title>
      <v-data-table
        :headers="headers"
        :items="quizzes"
        :search="search"
        :sort-by="['year']"
        :sort-desc="[false]"
        multi-sort
        disable-pagination
        class="elevation-1"
      >
        <template slot="items" slot-scope="props">
          <tr>
            <td class="text-left">{{ props.item.title }}</td>
            <td class="text-center">{{ convertDate(props.item.date) }}</td>
            <td class="text-center">
              {{ convertDate(props.item.availableDate) }}
            </td>
            <td class="text-center">
              {{ convertDate(props.item.conclusionDate) }}
            </td>
            <td class="text-center">
              {{ props.item.scramble }}
            </td>
            <td class="text-center">{{ props.item.type }}</td>
            <td class="text-center">{{ props.item.year }}</td>
            <td class="text-center">{{ props.item.series }}</td>
            <td class="text-center">{{ props.item.version }}</td>
            <td class="text-center">{{ props.item.numberOfQuestions }}</td>
            <td class="text-center">{{ props.item.numberOfAnswers }}</td>
            <td>
              <v-icon small class="mr-2" @click="showQuiz(props.item.id)"
                >visibility</v-icon
              >
              <v-icon small class="mr-2" @click="editQuiz(props.item.id)"
                >edit</v-icon
              >
              <v-icon small class="mr-2" @click="deleteQuiz(props.item.id)"
                >delete</v-icon
              >
            </td>
          </tr>
        </template>
      </v-data-table>
    </v-card>
  </v-content>
</template>

<script lang="ts">
import { Component, Vue, Prop } from "vue-property-decorator";
import { Quiz } from "@/models/management/Quiz";
import RemoteServices from "@/services/RemoteServices";
import { convertMarkDown } from "@/services/ConvertMarkdownService";
import Image from "@/models/management/Image";

@Component
export default class QuizList extends Vue {
  @Prop({ type: Array, required: true }) readonly quizzes!: Quiz[];
  quiz: Quiz | null = null;
  search: string = "";
  dialog: boolean = false;
  headers: object = [
    { text: "Title", value: "title", align: "left", width: "30%" },
    { text: "Date", value: "date", align: "center", width: "10%" },
    {
      text: "Available Date",
      value: "availableDate",
      align: "center",
      width: "10%"
    },
    {
      text: "Conclusion Date",
      value: "conclusionDate",
      align: "center",
      width: "10%"
    },
    { text: "Scramble", value: "scramble", align: "center", width: "10%" },
    { text: "Type", value: "type", align: "center", width: "10%" },
    { text: "Year", value: "year", align: "center", width: "10%" },
    { text: "Series", value: "series", align: "center", width: "10%" },
    { text: "Version", value: "version", align: "center", width: "10%" },
    {
      text: "Questions",
      value: "numberOfQuestions",
      align: "center",
      width: "10%"
    },
    {
      text: "Answers",
      value: "numberOfAnswers",
      align: "center",
      width: "10%"
    },
    {
      text: "Actions",
      value: "action",
      align: "center",
      width: "1%",
      sortable: false
    }
  ];

  constructor() {
    super();
  }

  async showQuiz(quizId: number) {
    try {
      this.quiz = await RemoteServices.getQuiz(quizId);
      this.dialog = true;
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }

  closeQuiz() {
    this.dialog = false;
    this.quiz = null;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  editQuiz(quizId: number) {
    this.$emit("editQuiz", quizId);
  }

  async deleteQuiz(quizId: number) {
    if (confirm("Are you sure you want to delete this quiz?")) {
      try {
        await RemoteServices.deleteQuiz(quizId);
        this.$emit("deleteQuiz", quizId);
      } catch (error) {
        await this.$store.dispatch("error", error);
      }
    }
  }

  convertDate(date: string[]): string {
    if (date == null) {
      return "";
    } else {
      return (
        date[0] + "/" + date[1] + "/" + date[2] + " " + date[3] + ":" + date[4]
      );
    }
  }
}
</script>

<style lang="scss"></style>
