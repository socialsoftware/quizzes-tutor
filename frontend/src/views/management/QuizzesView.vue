<template>
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
        <v-btn color="primary" dark class="mb-2" @click="open">New Quiz</v-btn>
     </v-card-title>
    <v-data-table
    :headers="headers"
    :items="quizzes"
    :search="search"
    disable-pagination
    class="elevation-1">
    <template slot="items" slot-scope="props">
        <tr>
          <td class="text-left">{{props.item.title}}</td>
          <td class="text-center">{{props.item.date}}</td>
          <td class="text-center">{{props.item.type}}</td>
          <td class="text-center">{{props.item.year}}</td>
          <td class="text-center">{{props.item.series}}</td>
          <td class="text-center">{{props.item.version}}</td>
          <td class="text-center">{{props.item.numberOfQuestions}}</td>
          <td class="text-center">{{props.item.numberOfAnswers}}</td>
          <td>
            <v-icon small class="mr-2" @click="showQuiz(props.item.id)">visibility</v-icon>
            <v-icon small class="mr-2" @click="deleteQuiz(props.item.id)">delete</v-icon>
          </td>
        </tr>
    </template>
  </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue} from "vue-property-decorator";
import { Quiz } from "@/models/quiz/Quiz";
import RemoteServices from "@/services/RemoteServices";

@Component
export default class QuizzesView extends Vue {
  quizzes: Quiz[] = [];
  search: string = "";
  headers: object = [
    { text: 'Title', value: 'title', align: 'left', width: "30%" },
    { text: 'Date', value: 'date', align: 'center', width: "10%" },
    { text: 'Type', value: 'type', align: 'center', width: "10%" },
    { text: 'Year', value: 'year', align: 'center', width: "10%" },
    { text: 'Series', value: 'series', align: 'center', width: "10%" },
    { text: 'Version', value: 'version', align: 'center', width: "10%" },
    { text: 'Questions', value: 'numberOfQuestions', align: 'center', width: "10%" },
    { text: 'Answers', value: 'numberOfAnswers', align: 'center', width: "10%" },
    { text: 'Actions', value: 'action', align: 'center', width: "1%",  sortable: false }
  ];

  constructor() {
    super();
  }

  async beforeMount() {
      try {
        this.quizzes = await RemoteServices.getNonGeneratedQuizzes();
      } catch (error) {
        confirm(error);
      }
  }

  open() {
    alert("to be implemented");
  }

  async showQuiz(quizId: number) {
    alert("to be implemented");
  }

  async deleteQuiz(quizId: number) {
    if (confirm('Are you sure you want to delete this quiz?')) {
      try {
        await RemoteServices.deleteQuiz(quizId);
        this.quizzes = this.quizzes.filter(quiz => quiz.id != quizId);
      } catch (error) {
        confirm(error);
      }
    }
  }

}
</script>

<style lang="scss"></style>
