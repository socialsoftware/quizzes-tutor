<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="quizzes"
      :search="search"
      multi-sort
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      class="elevation-1"
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
          <v-btn color="primary" dark @click="newQuiz">New Quiz</v-btn>
        </v-card-title>
      </template>

      <template v-slot:item.sortingDate="{ item }">
        {{ item.stringDate }}
      </template>

      <template v-slot:item.sortingAvailableDate="{ item }">
        {{ item.stringAvailableDate }}
      </template>

      <template v-slot:item.sortingConclusionDate="{ item }">
        {{ item.stringConclusionDate }}
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon small class="mr-2" v-on="on" @click="showQuiz(item.id)"
              >visibility</v-icon
            >
          </template>
          <span>Show Questions</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon small class="mr-2" v-on="on" @click="editQuiz(item.id)"
              >edit</v-icon
            >
          </template>
          <span>Edit Quiz</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="deleteQuiz(item.id)"
              color="red"
              >delete</v-icon
            >
          </template>
          <span>Delete Quiz</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <show-quiz-dialog
      v-if="quiz"
      :dialog="previewQuiz"
      :quiz="quiz"
      v-on:close-quiz-dialog="closeQuizDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { Quiz } from '@/models/management/Quiz';
import RemoteServices from '@/services/RemoteServices';
import ShowQuizDialog from '@/views/teacher/quizzes/ShowQuizDialog.vue';

@Component({
  components: { 'show-quiz-dialog': ShowQuizDialog }
})
export default class QuizList extends Vue {
  @Prop({ type: Array, required: true }) readonly quizzes!: Quiz[];
  quiz: Quiz | null = null;
  search: string = '';
  previewQuiz: boolean = false;
  headers: object = [
    { text: 'Title', value: 'title', align: 'left', width: '30%' },
    { text: 'Date', value: 'sortingDate', align: 'center', width: '10%' },
    {
      text: 'Available Date',
      value: 'sortingAvailableDate',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Conclusion Date',
      value: 'sortingConclusionDate',
      align: 'center',
      width: '10%'
    },
    { text: 'Scramble', value: 'scramble', align: 'center', width: '10%' },
    { text: 'Type', value: 'type', align: 'center', width: '10%' },
    { text: 'Year', value: 'year', align: 'center', width: '10%' },
    { text: 'Series', value: 'series', align: 'center', width: '10%' },
    { text: 'Version', value: 'version', align: 'center', width: '10%' },
    {
      text: 'Questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Answers',
      value: 'numberOfAnswers',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      width: '1%',
      sortable: false
    }
  ];

  async showQuiz(quizId: number) {
    try {
      this.quiz = await RemoteServices.getQuiz(quizId);
      this.previewQuiz = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  closeQuizDialog() {
    this.previewQuiz = false;
    this.quiz = null;
  }

  newQuiz() {
    this.$emit('newQuiz');
  }

  editQuiz(quizId: number) {
    this.$emit('editQuiz', quizId);
  }

  async deleteQuiz(quizId: number) {
    if (confirm('Are you sure you want to delete this quiz?')) {
      try {
        await RemoteServices.deleteQuiz(quizId);
        this.$emit('deleteQuiz', quizId);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped />
