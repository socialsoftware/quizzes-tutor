<template>
  <v-card v-if="editMode && quiz" class="table">
    <v-card-title>
      <span>Edit Quiz</span>

      <v-spacer />

      <v-btn color="primary" dark @click="switchMode">
        {{ editMode ? 'Close' : 'Create' }}
      </v-btn>

      <v-btn
        v-if="editMode && canSave"
        color="green darken-1"
        data-cy="saveQuizButton"
        @click="save"
        >Save
      </v-btn>
    </v-card-title>
    <v-card-text>
      <v-text-field
        v-model="quiz.title"
        data-cy="quizTitleTextArea"
        label="*Title"
      />
      <v-container fluid>
        <v-row>
          <v-col>
            <VueDatePicker
              id="availableDateInput"
              v-model="quiz.availableDate"
              model-type="iso"
              format="yyyy-MM-dd HH:mm"
              placeholder="*Available Date"
            ></VueDatePicker>
          </v-col>
          <v-col v-if="quiz.timed">
            <VueDatePicker
              id="conclusionDateInput"
              v-model="quiz.conclusionDate"
              model-type="iso"
              format="yyyy-MM-dd HH:mm"
              placeholder="*Conclusion Date"
            ></VueDatePicker>
          </v-col>
          <v-col v-if="quiz.timed">
            <VueDatePicker
              id="resultsDateInput"
              v-model="quiz.resultsDate"
              model-type="iso"
              format="yyyy-MM-dd HH:mm"
              placeholder="Results Date"
            ></VueDatePicker>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-tooltip bottom>
              <template v-slot:activator="{ props }">
                <v-switch v-model="quiz.scramble" label="Scramble" v-bind="props" />
              </template>
              <span>Question order is scrambled</span>
            </v-tooltip>
          </v-col>
          <v-col>
            <v-tooltip bottom>
              <template v-slot:activator="{ props }">
                <v-switch
                  v-model="quiz.qrCodeOnly"
                  label="QRCode Only"
                  v-bind="props"
                />
              </template>
              <span>Students can only start quiz with the qrcode</span>
            </v-tooltip>
          </v-col>
          <v-col>
            <v-tooltip bottom>
              <template v-slot:activator="{ props }">
                <v-switch
                  v-model="quiz.oneWay"
                  label="One Way Quiz"
                  v-bind="props"
                />
              </template>
              <span>Students cannot go to previous question</span>
            </v-tooltip>
          </v-col>
          <v-col>
            <v-tooltip bottom>
              <template v-slot:activator="{ props }">
                <v-switch v-model="quiz.timed" label="Timer" v-bind="props" />
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
            >Show Quiz
          </v-btn>
        </v-card-title>

        <v-data-table
          :custom-filter="customFilter"
          :custom-sort="customSort"
          :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
          :headers="headers"
          :items="quizQuestions"
          :items-per-page="15"
          :mobile-breakpoint="0"
          :sort-by="[{ key: 'sequence', order: 'asc' }]"
          must-sort
        >
          <template v-slot:[`item.title`]="{ item }">
            <div
              class="clickableTitle"
              @click="showQuestionDialog(item.raw || item)"
              @contextmenu="editQuestion(item.raw || item, $event)"
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
              <template v-slot:activator="{ props }">
                <v-icon
                  class="mr-2 action-button"
                  @click="showQuestionDialog(item.raw || item)"
                  v-bind="props"
                >
                  visibility
                </v-icon>
              </template>
              <span>Show Question</span>
            </v-tooltip>
            <div v-if="item.sequence">
              <v-tooltip bottom>
                <template v-slot:activator="{ props }">
                  <v-icon
                    class="mr-2 action-button"
                    @click="removeFromQuiz(item.raw || item)"
                    v-bind="props"
                  >
                    remove
                  </v-icon>
                </template>
                <span>Remove from Quiz</span>
              </v-tooltip>
              <v-tooltip v-if="item.sequence !== 1" bottom>
                <template v-slot:activator="{ props }">
                  <v-icon
                    class="mr-2 action-button"
                    @click="changeQuestionPosition(item.raw || item, 0)"
                    v-bind="props"
                  >
                    mdi-chevron-double-up
                  </v-icon>
                </template>
                <span>Move to first</span>
              </v-tooltip>
              <v-tooltip v-if="item.sequence !== 1" bottom>
                <template v-slot:activator="{ props }">
                  <v-icon
                    class="mr-2 action-button"
                    @click="
                      changeQuestionPosition(
                        item.raw || item,
                        quizQuestions.indexOf(item.raw || item) - 1
                      )
                    "
                    v-bind="props"
                  >
                    mdi-chevron-up
                  </v-icon>
                </template>
                <span>Move up</span>
              </v-tooltip>
              <v-tooltip v-if="quizQuestions.length > 1" bottom>
                <template v-slot:activator="{ props }">
                  <v-icon
                    class="mr-2 action-button"
                    @click="openSetPosition(item.raw || item)"
                    v-bind="props"
                  >
                    mdi-weather-sunny
                  </v-icon>
                </template>
                <span>Set Position</span>
              </v-tooltip>
              <v-tooltip v-if="item.sequence !== quizQuestions.length" bottom>
                <template v-slot:activator="{ props }">
                  <v-icon
                    class="mr-2 action-button"
                    @click="
                      changeQuestionPosition(
                        item.raw || item,
                        quizQuestions.indexOf(item.raw || item) + 1
                      )
                    "
                    v-bind="props"
                  >
                    mdi-chevron-down
                  </v-icon>
                </template>
                <span>Move down</span>
              </v-tooltip>
              <v-tooltip v-if="item.sequence !== quizQuestions.length" bottom>
                <template v-slot:activator="{ props }">
                  <v-icon
                    class="mr-2 action-button"
                    @click="
                      changeQuestionPosition(item.raw || item, quizQuestions.length - 1)
                    "
                    v-bind="props"
                  >
                    mdi-chevron-double-down
                  </v-icon>
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
            data-cy="searchField"
            hide-details
            label="Search"
            single-line
          ></v-text-field>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            dark
            v-on:click="showQueryForm = !showQueryForm"
          >
            {{ !showQueryForm ? 'Open Query Form' : 'Close Query Form' }}
          </v-btn>
        </v-card-title>
        <query-question-form
          v-show="showQueryForm"
          :availableOnly="true"
          v-on:query-questions="onQueryQuestions"
        />
        <v-data-table
          v-show="questions.length != 0"
          :custom-filter="customFilter"
          :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
          :headers="
            headers.filter((v, i) => i !== 0 && i !== headers.length - 1)
          "
          :items="questions"
          :items-per-page="15"
          :mobile-breakpoint="0"
          :search="search"
        >
          <template v-slot:[`item.title`]="{ item }">
            <div
              class="clickableTitle"
              @click="showQuestionDialog(item.raw || item)"
              @contextmenu="editQuestion(item.raw || item, $event)"
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
              <template v-slot:activator="{ props }">
                <span
                  class="mr-2 action-button"
                  @click="showQuestionDialog(item.raw || item)"
                  v-bind="props"
                >
                  <v-icon>visibility</v-icon>
                </span>
              </template>
              <span>Show Question</span>
            </v-tooltip>
            <v-tooltip v-if="!item.sequence" bottom>
              <template v-slot:activator="{ props }">
                <span
                  id="addToQuizButton1"
                  class="mr-2 action-button"
                  data-cy="addToQuizButton"
                  @click="addToQuiz(item.raw || item)"
                  v-bind="props"
                >
                  <v-icon>add</v-icon>
                </span>
              </template>
              <span>Add to Quiz</span>
            </v-tooltip>
          </template>
        </v-data-table>
        <footer>
          <v-icon class="mr-2 action-button">mouse</v-icon>
          Left-click on question's title to view it.
          <v-icon class="mr-2 action-button">mouse</v-icon>
          Right-click on question's title to edit it.
        </footer>
      </v-card>
    </v-card-text>

    <show-quiz-dialog
      v-if="quiz"
      v-model:dialog="quizDialog"
      :quiz="quiz"
      v-on:close-quiz-dialog="onCloseQuizDialog"
    />
    <v-dialog v-model:dialog="positionDialog" max-width="200px" persistent>
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
      @update:modelValue="!$event ? onCloseShowQuestionDialog() : null"
    />
    <edit-question-dialog
      v-if="currentQuestion && editQuestionDialog"
      v-model:dialog="editQuestionDialog"
      :question="currentQuestion"
      v-on:save-question="onSaveQuestion"
    />
  </v-card>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import { Quiz } from '@/models/management/Quiz';
import Question from '@/models/management/Question';
import EditQuestionDialog from '@/views/teacher/questions/EditQuestionDialog.vue';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import ShowQuizDialog from '@/views/teacher/quizzes/ShowQuizDialog.vue';
import { VueDatePicker } from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';
import QueryQuestionForm from '@/views/teacher/questions/QueryQuestionForm.vue';

const props = defineProps<{
  quiz: Quiz;
  editMode: boolean;
}>();

const emit = defineEmits(['switchMode', 'updateQuiz']);

const store = useStore();

const quizQuestions = ref<Question[]>([]);
const questions = ref<Question[]>([]);
const search = ref<string>('');
const currentQuestion = ref<Question | null | undefined>(null);
const position = ref<number | null>(null);

const positionDialog = ref(false);
const questionDialog = ref(false);
const editQuestionDialog = ref(false);
const quizDialog = ref(false);

const showQueryForm = ref<boolean>(true);

const headers = ref<any[]>([
  { title: 'Sequence', key: 'sequence', align: 'center', width: '5px' },
  { title: 'Actions', key: 'action', align: 'start', width: '250px', sortable: false },
  { title: 'Title', key: 'title', align: 'start', width: '60%', sortable: false },
  { title: 'Topics', key: 'topics', align: 'start', width: '40%' },
  { title: 'Answers', key: 'numberOfAnswers', align: 'center', width: '5px' },
]);

const canSave = computed(() => {
  return (
    !!props.quiz.title &&
    !!props.quiz.availableDate &&
    ((props.quiz.timed && props.quiz.conclusionDate !== undefined) || !props.quiz.timed)
  );
});

const onQueryQuestions = async (qs: Question[]) => {
  let quizQuestionIds: number[] = [];
  if (props.quiz && props.quiz.questions) {
    quizQuestions.value.forEach(( quizQuestion ) => {
      if (quizQuestion.id) quizQuestionIds.push(quizQuestion.id);
    });
  }

  questions.value = qs.filter(
    (question) => question.id && !quizQuestionIds.includes(question.id)
  );

  showQueryForm.value = false;
};

watch(
  () => props.quiz,
  () => {
    if (props.quiz && quizQuestions.value.length === 0) {
      props.quiz.questions.forEach((question, index) => {
        question.sequence = index + 1;
        quizQuestions.value.push(question);
      });
    }
  },
  { immediate: true, deep: true }
);

const clean = () => {
  quizQuestions.value.forEach((question) => {
    question.sequence = null;
  });
  quizQuestions.value = [];
  questions.value = [];
};

const switchMode = () => {
  clean();
  emit('switchMode');
};

const save = async () => {
  try {
    props.quiz.questions = quizQuestions.value;
    let updatedQuiz = await RemoteServices.saveQuiz(props.quiz);
    clean();
    emit('updateQuiz', updatedQuiz);
  } catch (error) {
    store.setError(error as string);
  }
};

const customFilter = (value: string, query: string, item: any) => {
  return (
    query != null &&
    JSON.stringify(item?.raw || item).toLowerCase().indexOf(query.toLowerCase()) !== -1
  );
};

const compare = (a: number | null | undefined, b?: number | null | undefined) => {
  if (a == b) {
    return 0;
  } else if (a == null) {
    return 1;
  } else if (b == null) {
    return -1;
  } else {
    return a < b ? -1 : 1;
  }
};

const customSort = (items: any[], index: string, isDesc: string) => {
  items.sort((a: any, b: any) => {
    if (index == 'sequence') {
      if (isDesc == 'false') {
        return compare(a.sequence, b.sequence);
      } else {
        return compare(b.sequence, a.sequence);
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
};

const showQuestionDialog = (question: Question) => {
  currentQuestion.value = question;
  questionDialog.value = true;
};

const onCloseShowQuestionDialog = () => {
  currentQuestion.value = null;
  questionDialog.value = false;
};

const editQuestion = (question: Question, e?: Event) => {
  if (e) e.preventDefault();
  currentQuestion.value = question;
  editQuestionDialog.value = true;
};

const onSaveQuestion = async (question: Question) => {
  if (questions.value.find((q) => q.id !== question.id)) {
    questions.value = questions.value.filter((q) => q.id !== question.id);
    questions.value.unshift(question);
  } else {
    let quizQuestion = quizQuestions.value.find((q) => q.id == question.id);
    if (quizQuestion) {
      quizQuestions.value = quizQuestions.value.filter((q) => q.id !== question.id);
      question.sequence = quizQuestion.sequence;
      quizQuestions.value.unshift(question);
    }
  }

  editQuestionDialog.value = false;
  currentQuestion.value = null;
};

const addToQuiz = (question: Question) => {
  question.sequence = quizQuestions.value.length + 1;
  quizQuestions.value.push(question);
  let index: number = questions.value.indexOf(question);
  questions.value.splice(index, 1);
};

const removeFromQuiz = (question: Question) => {
  let index: number = quizQuestions.value.indexOf(question);
  quizQuestions.value.splice(index, 1);
  question.sequence = null;
  quizQuestions.value
    .sort((qq1, qq2) => compare(qq1.sequence, qq2.sequence))
    .forEach((q, i) => {
      q.sequence = i + 1;
    });
  questions.value.push(question);
};

const openSetPosition = (question: Question) => {
  if (question.sequence) {
    positionDialog.value = true;
    position.value = question.sequence;
    currentQuestion.value = question;
  }
};

const closeSetPosition = () => {
  positionDialog.value = false;
  position.value = null;
  currentQuestion.value = undefined;
};

const changeQuestionPosition = (question: Question, pos: number) => {
  if (question.sequence) {
    quizQuestions.value.sort((qq1, qq2) => compare(qq1.sequence, qq2.sequence));
    let currentPosition: number = quizQuestions.value.indexOf(question);
    quizQuestions.value.splice(
      pos,
      0,
      quizQuestions.value.splice(currentPosition, 1)[0]
    );
    quizQuestions.value.forEach((q, i) => {
      q.sequence = i + 1;
    });
  }
};

const saveSetPosition = () => {
  if (
    currentQuestion.value &&
    currentQuestion.value.sequence !== position.value &&
    position.value &&
    position.value > 0 &&
    position.value <= quizQuestions.value.length
  ) {
    changeQuestionPosition(currentQuestion.value, position.value - 1);
  }
  closeSetPosition();
};

const openShowQuiz = () => {
  quizDialog.value = true;
  props.quiz.questions = quizQuestions.value;
};

const onCloseQuizDialog = () => {
  quizDialog.value = false;
};
</script>

<style lang="scss" scoped></style>
