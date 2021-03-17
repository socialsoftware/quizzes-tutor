<template>
  <v-card v-if="editMode && quiz" class="table">
    <v-card-title>
      <span>Edit Quiz</span>

      <v-spacer />

      <v-btn color="primary" dark @click="switchMode">
        {{ editMode ? 'Close' : 'Create' }}
      </v-btn>

      <v-btn
        color="green darken-1"
        v-if="editMode && canSave"
        data-cy="saveQuizButton"
        @click="save"
        >Save</v-btn
      >
    </v-card-title>
    <v-card-text>
      <v-text-field
        v-model="quiz.title"
        label="*Title"
        data-cy="quizTitleTextArea"
      />
      <v-container fluid>
        <v-row>
          <v-col>
            <VueCtkDateTimePicker
              label="*Available Date"
              id="availableDateInput"
              v-model="quiz.availableDate"
              format="YYYY-MM-DDTHH:mm:ssZ"
            ></VueCtkDateTimePicker>
          </v-col>
          <v-col v-if="quiz.timed">
            <VueCtkDateTimePicker
              label="*Conclusion Date"
              id="conclusionDateInput"
              v-model="quiz.conclusionDate"
              format="YYYY-MM-DDTHH:mm:ssZ"
            ></VueCtkDateTimePicker>
          </v-col>
          <v-col v-if="quiz.timed">
            <VueCtkDateTimePicker
              label="Results Date"
              id="resultsDateInput"
              v-model="quiz.resultsDate"
              format="YYYY-MM-DDTHH:mm:ssZ"
            ></VueCtkDateTimePicker>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-switch v-on="on" v-model="quiz.scramble" label="Scramble" />
              </template>
              <span>Question order is scrambled</span>
            </v-tooltip>
          </v-col>
          <v-col>
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-switch
                  v-on="on"
                  v-model="quiz.qrCodeOnly"
                  label="QRCode Only"
                />
              </template>
              <span>Students can only start quiz with the qrcode</span>
            </v-tooltip>
          </v-col>
          <v-col>
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-switch
                  v-on="on"
                  v-model="quiz.oneWay"
                  label="One Way Quiz"
                />
              </template>
              <span>Students cannot go to previous question</span>
            </v-tooltip>
          </v-col>
          <v-col>
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-switch v-on="on" v-model="quiz.timed" label="Timer" />
              </template>
              <span>Displays a timer to conclusion and to show results</span>
            </v-tooltip>
          </v-col>
        </v-row>
      </v-container>

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
          <v-container fluid>
            <v-row>
              <v-col>
                <v-text-field v-model="search" label="Search" class="mx-4" />
              </v-col>
              <v-col>
                <v-btn
                  v-if="quizQuestions.length !== 0"
                  color="primary"
                  dark
                  @click="openShowQuiz"
                  >Show Quiz</v-btn
                >
              </v-col>
            </v-row>
          </v-container>
        </template>

        <template v-slot:[`item.title`]="{ item }">
          <div
            @click="showQuestionDialog(item)"
            @contextmenu="rightClickEditQuestion($event, item)"
            class="clickableTitle"
          >
            {{ item.title }}
          </div>
        </template>

        <template v-slot:[`item.topics`]="{ item }">
          <span v-for="topic in item.topics" :key="topic.id">
            {{ topic.name }}
          </span>
        </template>

        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                v-on="on"
                @click="showQuestionDialog(item)"
              >
                visibility</v-icon
              >
            </template>
            <span>Show Question</span>
          </v-tooltip>
          <v-tooltip bottom v-if="!item.sequence">
            <template v-slot:activator="{ on }">
              <v-icon
                id="addToQuizButton1"
                class="mr-2 action-button"
                v-on="on"
                @click="addToQuiz(item)"
                data-cy="addToQuizButton"
              >
                add</v-icon
              >
            </template>
            <span>Add to Quiz</span>
          </v-tooltip>
          <div v-if="item.sequence" :key="item.sequence">
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-icon
                  class="mr-2 action-button"
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
                  class="mr-2 action-button"
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
                  class="mr-2 action-button"
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
                  class="mr-2 action-button"
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
                  class="mr-2 action-button"
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
                  class="mr-2 action-button"
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

      <footer>
        <v-icon class="mr-2 action-button">mouse</v-icon> Left-click on title to
        view question. <v-icon class="mr-2 action-button">mouse</v-icon>Right
        -click on title to edit question.
      </footer>
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
          <v-text-field v-model="position" label="position" required>
          </v-text-field>
        </v-card-text>
        <v-card-actions>
          <div class="flex-grow-1"></div>
          <v-btn color="red darken-1" @click="closeSetPosition">Close</v-btn>
          <v-btn color="green darken-1" @click="saveSetPosition">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <show-question-dialog
      v-if="currentQuestion"
      v-model="questionDialog"
      :question="currentQuestion"
      v-on:close-show-question-dialog="onCloseShowQuestionDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { Quiz } from '@/models/management/Quiz';
import Question from '@/models/management/Question';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import ShowQuizDialog from '@/views/teacher/quizzes/ShowQuizDialog.vue';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);

@Component({
  components: {
    'show-question-dialog': ShowQuestionDialog,
    'show-quiz-dialog': ShowQuizDialog,
  },
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
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false,
    },
    {
      text: 'Sequence',
      value: 'sequence',
      align: 'center',
      width: '5px',
    },
    {
      text: 'Title',
      value: 'title',
      align: 'left',
      width: '60%',
      sortable: false,
    },
    {
      text: 'Topics',
      value: 'topics',
      align: 'left',
      width: '40%',
    },
    {
      text: 'Answers',
      value: 'numberOfAnswers',
      align: 'center',
      width: '5px',
    },
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
      this.quiz.questions.forEach((question) => {
        if (!this.quizQuestions.includes(question) && question.id) {
          questionIds.push(question.id);
        }
      });
    }

    this.questions.forEach((question) => {
      if (
        question.id &&
        questionIds.includes(question.id) &&
        !this.quizQuestions
          .map((quizQuestion) => quizQuestion.id)
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
      ((this.quiz.timed && this.quiz.conclusionDate !== undefined) ||
        !this.quiz.timed)
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
      JSON.stringify(question).toLowerCase().indexOf(search.toLowerCase()) !==
        -1
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

  compare(a: number | null, b?: number | null) {
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

  showQuestionDialog(question: Question) {
    this.currentQuestion = question;
    this.questionDialog = true;
  }

  onCloseShowQuestionDialog() {
    this.currentQuestion = null;
    this.questionDialog = false;
  }

  addToQuiz(question: Question) {
    question.sequence = this.quizQuestions.length + 1;
    this.quizQuestions.push(question);
  }

  removeFromQuiz(question: Question) {
    let index: number = this.quizQuestions.indexOf(question);
    this.quizQuestions.splice(index, 1);
    question.sequence = null;
    this.quizQuestions
      .sort((qq1, qq2) => this.compare(qq1.sequence, qq2.sequence))
      .forEach((question, index) => {
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
      this.quizQuestions.sort((qq1, qq2) =>
        this.compare(qq1.sequence, qq2.sequence)
      );
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
    this.quizQuestions.forEach((question) => {
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
