<template>
  <v-card v-if="editMode && quiz" class="table">
    <v-card-title>
      <span>Edit Quiz</span>

      <v-spacer />

      <v-btn color="primary" dark @click="switchMode">
        {{ editMode ? 'Close' : 'Create' }}
      </v-btn>

      <v-btn color="primary" dark v-if="editMode && canSave" @click="save"
        >Save</v-btn
      >
    </v-card-title>
    <v-card-text>
      <v-text-field v-model="quiz.title" label="*Title" />
      <v-row>
        <v-col cols="12" sm="6">
          <v-datetime-picker
            label="*Available Date"
            format="yyyy-MM-dd HH:mm"
            v-model="quiz.availableDate"
            date-format="yyyy-MM-dd"
            time-format="HH:mm"
          >
          </v-datetime-picker>
        </v-col>
        <v-spacer></v-spacer>
        <v-col cols="12" sm="6">
          <v-datetime-picker
            :label="
              quiz.type === 'IN_CLASS' ? '*Conclusion Date' : 'Conclusion Date'
            "
            v-model="quiz.conclusionDate"
            date-format="yyyy-MM-dd"
            time-format="HH:mm"
          >
          </v-datetime-picker>
        </v-col>
      </v-row>
      <v-row wrap justify="center">
        <v-col style="display: flex; justify-content: center">
          <v-switch v-model="quiz.scramble" label="Scramble" />
        </v-col>
        <v-col style="display: flex; justify-content: center">
          <v-switch v-model="quiz.qrCodeOnly" label="QRCode Only" />
        </v-col>
        <v-col style="display: flex; justify-content: center">
          <v-switch v-model="quiz.oneWay" label="One Way Quiz" />
        </v-col>
        <v-col cols="12" sm="6">
          <v-select
            v-model="quiz.type"
            :items="['PROPOSED', 'IN_CLASS']"
            label="*Type"
          ></v-select>
          {{ quiz.type }}
        </v-col>
      </v-row>

      <v-data-table
        :headers="headers"
        :custom-filter="customFilter"
        :custom-sort="customSort"
        :items="questions"
        :search="search"
        :sort-by="['sequence']"
        :sort-desc="[false]"
        :mobile-breakpoint="0"
        must-sort
        :items-per-page="15"
        :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      >
        <template v-slot:top>
          <v-row>
            <v-col cols="12" sm="6">
              <v-text-field v-model="search" label="Search" class="mx-4" />
            </v-col>
            <v-col cols="12" sm="6">
              <v-btn
                v-if="quizQuestions.length !== 0"
                color="primary"
                dark
                @click="openShowQuiz"
                >Show Quiz</v-btn
              >
            </v-col>
          </v-row>
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
    </v-card-text>

    <show-quiz-dialog
      v-if="quiz"
      v-model="quizDialog"
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
      v-if="currentQuestion"
      :dialog="questionDialog"
      :question="currentQuestion"
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
  currentQuestion: Question | null | undefined = null;
  quizQuestions: Question[] = [];
  position: number | null = null;

  positionDialog: boolean = false;
  questionDialog: boolean = false;
  quizDialog: boolean = false;

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

  get canSave(): boolean {
    return (
      !!this.quiz.title &&
      !!this.quiz.availableDate &&
      !!this.quiz.type &&
      ((this.quiz.type == 'IN_CLASS' &&
        this.quiz.conclusionDate !== undefined) ||
        this.quiz.type !== 'IN_CLASS')
    );
  }

  switchMode() {
    this.cleanQuizQuestions();
    this.$emit('switchMode');
  }

  async save() {
    try {
      this.quiz.questions = this.quizQuestions;
      let updatedQuiz = await RemoteServices.saveQuiz(this.quiz);
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
    this.currentQuestion = question;
    this.questionDialog = true;
  }

  onCloseShowQuestionDialog() {
    this.currentQuestion = null;
    this.questionDialog = false;
  }

  convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return convertMarkDownNoFigure(text, image);
  }

  addToQuiz(question: Question) {
    question.sequence = this.quizQuestions.length + 1;
    this.quizQuestions.push(question);
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
      this.currentQuestion = question;
    }
  }

  closeSetPosition() {
    this.positionDialog = false;
    this.position = null;
    this.currentQuestion = undefined;
  }

  saveSetPosition() {
    if (
      this.currentQuestion &&
      this.currentQuestion.sequence !== this.position &&
      this.position &&
      this.position > 0 &&
      this.position <= this.quizQuestions.length
    ) {
      this.changeQuestionPosition(this.currentQuestion, this.position - 1);
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
    this.quizDialog = true;
    this.quiz.questions = this.quizQuestions;
  }

  onCloseQuizDialog() {
    this.quizDialog = false;
  }
}
</script>

<style lang="scss" scoped></style>
