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
    <template slot="items" slot-scope="props">
        <tr @click="props.expanded = !props.expanded">
          <td class="text-left" v-html="convertMarkDown(props.item.content, props.item.image)"></td>
          <td class="text-left" v-text="props.item.title"></td>
          <td class="text-left"><span v-if="props.item.active">TRUE</span><span v-else>FALSE</span></td>
          <td class="text-left" v-text="props.item.difficulty"></td>
          <td class="text-left" v-text="props.item.numberOfAnswers"></td>
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
        search: "",
        headers: [
          { text: 'Question', value: 'content' },
          { text: 'Title', value: 'title' },
          { text: 'Active', value: 'active' },
          { text: 'Difficulty', value: 'difficulty' },
          { text: 'Number of Answers', value: 'numberOfAnswers' },
        ],
        questions: this.questions
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
