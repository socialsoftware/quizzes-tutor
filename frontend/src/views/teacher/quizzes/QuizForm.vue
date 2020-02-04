<template>
  <v-card v-if="editMode && quiz" class="table">
    <v-card-title>
      <span class="headline">Edit Quiz</span>
      <v-btn color="primary" dark @click="switchMode">
        {{ editMode ? 'Close' : 'Create' }}
      </v-btn>

      <v-btn color="primary" dark v-if="editMode" @click="save">Save</v-btn>
    </v-card-title>
    <v-card-text>
      <v-container grid-list-md fluid>
        <v-text-field v-model="quiz.title" label="Title" />
        <v-layout row wrap>
          <v-flex class="text-left">
            <v-datetime-picker
              label="Available Date"
              v-model="quiz.availableDate"
            >
            </v-datetime-picker>
          </v-flex>
          <v-flex class="text-left">
            <v-datetime-picker
              label="Conclusion Date"
              v-model="quiz.conclusionDate"
            >
            </v-datetime-picker>
          </v-flex>
        </v-layout>
        <v-switch
          v-model="quiz.scramble"
          :label="
            `Scramble: ${quiz.scramble ? quiz.scramble.toString() : 'false'}`
          "
        />
      </v-container>
      <v-container grid-list-md fluid>
        <v-layout row wrap>
          <v-divider class="mx-4" inset vertical> </v-divider>
          <v-spacer />
          <v-btn
            v-if="quizQuestions.length !== 0"
            color="primary"
            dark
            @click="openShowQuiz"
            >Show Quiz</v-btn
          >
        </v-layout>
        <v-data-table
          :headers="headers"
          :items="questions"
          :search="search"
          :custom-filter="customFilter"
          :items-per-page="15"
          :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
          :sort-by="['sequence']"
          :sort-desc="[false]"
          :custom-sort="customSort"
          must-sort
        >
          <template v-slot:top>
            <v-text-field v-model="search" label="Search" class="mx-4" />
          </template>
          <template v-slot:item.content="{ item }">
            <div
              class="text-left"
              v-html="convertMarkDownNoFigure(item.content, item.image)"
              @click="openShowQuestionDialog(item)"
            ></div>
          </template>

          <template v-slot:item.topics="{ item }">
            <span v-for="topic in item.topics" :key="topic.id">
              {{ topic.name }}
            </span>
          </template>

          <template v-slot:item.action="{ item }">
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-icon
                  small
                  class="mr-2"
                  v-on="on"
                  @click="openShowQuestionDialog(item)"
                >
                  visibility</v-icon
                >
              </template>
              <span>Show Question</span>
            </v-tooltip>
            <v-tooltip bottom v-if="!item.sequence">
              <template v-slot:activator="{ on }">
                <v-icon small class="mr-2" v-on="on" @click="addToQuiz(item)">
                  add</v-icon
                >
              </template>
              <span>Add to Quiz</span>
            </v-tooltip>
            <div v-if="item.sequence" :key="item.sequence">
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                    small
                    class="mr-2"
                    v-on="on"
                    @click="removeFromQuiz(item)"
                  >
                    remove</v-icon
                  >
                </template>
                <span>Remove from Quiz</span>
              </v-tooltip>
              <v-tooltip bottom v-if="item.sequence !== 1">
                <template v-slot:activator="{ on }">
                  <v-icon
                    small
                    class="mr-2"
                    v-on="on"
                    @click="changeQuestionPosition(item, 0)"
                  >
                    mdi-chevron-double-up</v-icon
                  >
                </template>
                <span>Move to first</span>
              </v-tooltip>
              <v-tooltip bottom v-if="item.sequence !== 1">
                <template v-slot:activator="{ on }">
                  <v-icon
                    small
                    class="mr-2"
                    v-on="on"
                    @click="
                      changeQuestionPosition(
                        item,
                        quizQuestions.indexOf(item) - 1
                      )
                    "
                  >
                    mdi-chevron-up</v-icon
                  >
                </template>
                <span>Move up</span>
              </v-tooltip>
              <v-tooltip bottom v-if="quizQuestions.length > 1">
                <template v-slot:activator="{ on }">
                  <v-icon
                    small
                    class="mr-2"
                    v-on="on"
                    @click="openSetPosition(item)"
                  >
                    mdi-weather-sunny</v-icon
                  >
                </template>
                <span>Set Position</span>
              </v-tooltip>
              <v-tooltip bottom v-if="item.sequence !== quizQuestions.length">
                <template v-slot:activator="{ on }">
                  <v-icon
                    small
                    class="mr-2"
                    v-on="on"
                    @click="
                      changeQuestionPosition(
                        item,
                        quizQuestions.indexOf(item) + 1
                      )
                    "
                  >
                    mdi-chevron-down</v-icon
                  >
                </template>
                <span>Move down</span>
              </v-tooltip>
              <v-tooltip bottom v-if="item.sequence !== quizQuestions.length">
                <template v-slot:activator="{ on }">
                  <v-icon
                    small
                    class="mr-2"
                    v-on="on"
                    @click="
                      changeQuestionPosition(item, quizQuestions.length - 1)
                    "
                  >
                    mdi-chevron-double-down</v-icon
                  >
                </template>
                <span>Move to last</span>
              </v-tooltip>
            </div>
          </template>
        </v-data-table>
      </v-container>
    </v-card-text>

    <show-quiz-dialog
      v-if="quiz"
      :dialog="showQuiz"
      :quiz="quiz"
      v-on:close-quiz-dialog="onCloseQuizDialog"
    />
    <v-dialog v-model="positionDialog" persistent max-width="200px">
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
          <v-btn color="blue darken-1" @click="closeSetPosition">Close</v-btn>
          <v-btn color="blue darken-1" @click="saveSetPosition">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <show-question-dialog
      v-if="questionToShow"
      :dialog="showQuestion"
      :question="questionToShow"
      v-on:close-show-question-dialog="onCloseShowQuestionDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import { Quiz } from '@/models/management/Quiz';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import ShowQuizDialog from '@/views/teacher/quizzes/ShowQuizDialog.vue';

@Component({
  components: {
    'show-question-dialog': ShowQuestionDialog,
    'show-quiz-dialog': ShowQuizDialog
  }
})
export default class QuizForm extends Vue {
  @Prop(Quiz) readonly quiz!: Quiz;
  @Prop(Boolean) readonly editMode!: boolean;
  questions: Question[] = [];
  search: string = '';
  showQuestion: boolean = false;
  questionToShow: Question | null | undefined = null;
  quizQuestions: Question[] = [];
  positionDialog: boolean = false;
  questionPosition: Question | undefined;
  position: number | null = null;
  showQuiz: boolean = false;
  forceReRender: boolean = true;

  headers: object = [
    {
      text: 'Sequence',
      value: 'sequence',
      align: 'left',
      width: '1%'
    },
    {
      text: 'Question',
      value: 'content',
      align: 'left',
      width: '70%',
      sortable: false
    },
    {
      text: 'Topics',
      value: 'topics',
      align: 'left',
      width: '20%'
    },
    { text: 'Difficulty', value: 'difficulty', align: 'center', width: '1%' },
    { text: 'Answers', value: 'numberOfAnswers', align: 'center', width: '1%' },
    {
      text: 'Title',
      value: 'title',
      align: 'left',
      width: '5%',
      sortable: false
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      width: '1%',
      sortable: false
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.questions = await RemoteServices.getAvailableQuestions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  @Watch('quiz')
  onQuizChange() {
    let questionIds: number[] = [];
    if (this.quiz && this.quiz.questions) {
      this.quiz.questions.forEach(question => {
        if (!this.quizQuestions.includes(question) && question.id) {
          questionIds.push(question.id);
        }
      });
    }

    this.questions.forEach(question => {
      if (
        question.id &&
        questionIds.includes(question.id) &&
        !this.quizQuestions
          .map(quizQuestion => quizQuestion.id)
          .includes(question.id)
      ) {
        question.sequence = questionIds.indexOf(question.id) + 1;
        this.quizQuestions.push(question);
      }
    });
  }

  switchMode() {
    this.cleanQuizQuestions();
    this.$emit('switchMode');
  }

  async save() {
    try {
      this.quiz.type = 'TEACHER';
      this.quiz.year = new Date().getFullYear();
      this.quiz.questions = this.quizQuestions;
      let updatedQuiz: Quiz;
      updatedQuiz = await RemoteServices.saveQuiz(this.quiz);
      this.cleanQuizQuestions();
      this.$emit('updateQuiz', updatedQuiz);
    } catch (error) {
      await this.$store.dispatch('error', error);
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
      if (index == 'sequence') {
        if (isDesc == 'false') {
          return this.compare(a.sequence, b.sequence);
        } else {
          return this.compare(b.sequence, a.sequence);
        }
      } else {
        if (isDesc == 'false') {
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

  openShowQuestionDialog(question: Question) {
    this.questionToShow = question;
    this.showQuestion = true;
  }

  onCloseShowQuestionDialog() {
    this.questionToShow = null;
    this.showQuestion = false;
  }

  convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return convertMarkDownNoFigure(text, image);
  }

  addToQuiz(question: Question) {
    question.sequence = this.quizQuestions.length + 1;
    this.quizQuestions.push(question);
    this.forceReRender = false;
    this.forceReRender = true;
  }

  removeFromQuiz(question: Question) {
    let index: number = this.quizQuestions.indexOf(question);
    this.quizQuestions.splice(index, 1);
    question.sequence = null;
    this.quizQuestions.forEach((question, index) => {
      question.sequence = index + 1;
    });
  }

  openSetPosition(question: Question) {
    if (question.sequence) {
      this.positionDialog = true;
      this.position = question.sequence;
      this.questionPosition = question;
    }
  }

  closeSetPosition() {
    this.positionDialog = false;
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

  openShowQuiz() {
    this.showQuiz = true;
    this.quiz.questions = this.quizQuestions;
  }

  onCloseQuizDialog() {
    this.showQuiz = false;
  }
}
</script>

<style lang="scss" scoped>
.headline {
  flex: 1 1 auto;
  text-align: left;
}

.headline + button {
  margin: 0 20px;
}
</style>
