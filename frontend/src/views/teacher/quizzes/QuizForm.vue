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

      <v-card v-show="quizQuestions.length != 0">
        <v-card-title>
          Quiz
          <v-spacer></v-spacer>
          <v-btn
            v-if="quizQuestions.length !== 0"
            color="primary"
            dark
            @click="openShowQuiz"
            >Show Quiz</v-btn
          >
        </v-card-title>

        <v-data-table
          :headers="headers"
          :custom-filter="customFilter"
          :custom-sort="customSort"
          :items="quizQuestions"
          :sort-by="['sequence']"
          :sort-desc="[false]"
          :mobile-breakpoint="0"
          must-sort
          :items-per-page="15"
          :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
        >
          <template v-slot:[`item.title`]="{ item }">
            <div
              @click="showQuestionDialog(item)"
              @contextmenu="editQuestion(item, $event)"
              class="clickableTitle"
            >
              {{ item.title }}
            </div>
          </template>

          <template v-slot:[`item.topics`]="{ item }">
            <span v-for="topic in item.topics" :key="'A-' + topic.id">
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
            <div v-if="item.sequence">
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
      </v-card>

      <v-card>
        <v-card-title>
          Available Questions
          <v-spacer></v-spacer>
          <v-text-field
            v-show="questions.length != 0"
            v-model="search"
            append-icon="mdi-magnify"
            label="Search"
            single-line
            hide-details
          ></v-text-field>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            dark
            v-on:click="showQueryForm = !showQueryForm"
          >
            {{ !showQueryForm ? 'Open Query Form' : 'Close Query Form' }}</v-btn
          >
        </v-card-title>
        <query-question-form
          v-show="showQueryForm"
          :availableOnly="true"
          v-on:query-questions="onQueryQuestions"
        />
        <v-data-table
          v-show="questions.length != 0"
          :headers="
            headers.filter((v, i) => i !== 0 && i !== headers.length - 1)
          "
          :custom-filter="customFilter"
          :items="questions"
          :search="search"
          :mobile-breakpoint="0"
          :items-per-page="15"
          :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
        >
          <template v-slot:[`item.title`]="{ item }">
            <div
              @click="showQuestionDialog(item)"
              @contextmenu="editQuestion(item, $event)"
              class="clickableTitle"
            >
              {{ item.title }}
            </div>
          </template>

          <template v-slot:[`item.topics`]="{ item }">
            <span v-for="topic in item.topics" :key="'B-' + topic.id">
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
          </template>
        </v-data-table>
        <footer>
          <v-icon class="mr-2 action-button">mouse</v-icon>Left-click on
          question's title to view it.
          <v-icon class="mr-2 action-button">mouse</v-icon>Right-click on
          question's title to edit it.
        </footer>
      </v-card>
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
    <edit-question-dialog
      v-if="currentQuestion && editQuestionDialog"
      v-model="editQuestionDialog"
      :question="currentQuestion"
      v-on:save-question="onSaveQuestion"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { Quiz } from '@/models/management/Quiz';
import Question from '@/models/management/Question';
import EditQuestionDialog from '@/views/teacher/questions/EditQuestionDialog.vue';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import ShowQuizDialog from '@/views/teacher/quizzes/ShowQuizDialog.vue';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';
import QueryQuestionForm from '@/views/teacher/questions/QueryQuestionForm.vue';

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);

@Component({
  components: {
    'query-question-form': QueryQuestionForm,
    'show-question-dialog': ShowQuestionDialog,
    'edit-question-dialog': EditQuestionDialog,
    'show-quiz-dialog': ShowQuizDialog,
  },
})
export default class QuizForm extends Vue {
  @Prop(Quiz) readonly quiz!: Quiz;
  @Prop(Boolean) readonly editMode!: boolean;
  quizQuestions: Question[] = [];
  questions: Question[] = [];
  search: string = '';
  currentQuestion: Question | null | undefined = null;
  position: number | null = null;

  positionDialog: boolean = false;
  questionDialog: boolean = false;
  editQuestionDialog: boolean = false;
  quizDialog: boolean = false;

  showQueryForm: boolean = true;

  headers: object[] = [
    {
      text: 'Sequence',
      value: 'sequence',
      align: 'center',
      width: '5px',
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '250px',
      sortable: false,
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

  async onQueryQuestions(questions: Question[]) {
    let quizQuestionIds: number[] = [];
    if (this.quiz && this.quiz.questions) {
      this.quizQuestions.forEach((quizQuestion) => {
        if (quizQuestion.id) quizQuestionIds.push(quizQuestion.id);
      });
    }

    this.questions = questions.filter(
      (question) => question.id && !quizQuestionIds.includes(question.id)
    );

    this.showQueryForm = false;
  }

  @Watch('quiz')
  onQuizChange() {
    if (this.quiz && this.quizQuestions.length === 0) {
      this.quiz.questions.forEach((question, index) => {
        question.sequence = index + 1;
        this.quizQuestions.push(question);
      });
    }
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
    this.clean();
    this.$emit('switchMode');
  }

  async save() {
    try {
      this.quiz.questions = this.quizQuestions;
      let updatedQuiz = await RemoteServices.saveQuiz(this.quiz);
      this.clean();
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

  editQuestion(question: Question, e?: Event) {
    if (e) e.preventDefault();
    this.currentQuestion = question;
    this.editQuestionDialog = true;
  }

  async onSaveQuestion(question: Question) {
    if (this.questions.find((q) => q.id !== question.id)) {
      this.questions = this.questions.filter((q) => q.id !== question.id);
      this.questions.unshift(question);
    } else {
      let quizQuestion = this.quizQuestions.find((q) => q.id == question.id);
      if (quizQuestion) {
        this.quizQuestions = this.quizQuestions.filter(
          (q) => q.id !== question.id
        );
        question.sequence = quizQuestion.sequence;
        this.quizQuestions.unshift(question);
      }
    }

    this.editQuestionDialog = false;
    this.currentQuestion = null;
  }

  addToQuiz(question: Question) {
    question.sequence = this.quizQuestions.length + 1;
    this.quizQuestions.push(question);
    let index: number = this.questions.indexOf(question);
    this.questions.splice(index, 1);
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
    this.questions.push(question);
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

  clean() {
    this.quizQuestions.forEach((question) => {
      question.sequence = null;
    });
    this.quizQuestions = [];
    this.questions = [];
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
