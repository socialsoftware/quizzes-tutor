<template>
  <v-content>
    <v-card>
        <v-card-actions>
            <v-flex class="text-xs-right">
                <v-btn color="primary" dark class="mb-2" @click="switchMode">{{editMode ? 'Close' : 'Create'}}</v-btn>
                <v-btn color="primary" dark class="mb-2" v-if="editMode" @click="save">Save</v-btn>
            </v-flex>
        </v-card-actions>
    </v-card>
    <v-card v-if="editMode">
        <v-card-title>
            <span class="headline">Edit Quiz</span>
            <v-dialog v-model="showQuestion" max-width="1000px">
                <v-card v-if="questionToShow">
                    <v-card-title>
                        <span class="headline">{{ questionToShow.title }}</span>
                    </v-card-title>
                    <v-card-text>
                        <v-container grid-list-md fluid>
                        <v-layout column wrap>
                            <v-flex class="text-left" xs24 sm12 md8>
                            <p v-html="renderQuestion(questionToShow)"></p>
                            </v-flex>
                        </v-layout>
                        </v-container>
                    </v-card-text>
                    <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn color="blue darken-1" text @click="closeShowQuestionDialog"
                        >Close</v-btn
                        >
                    </v-card-actions>
                </v-card>
            </v-dialog>

        </v-card-title>
        <v-card-text>
            <v-container grid-list-md fluid>
              <v-layout row wrap>
                <v-flex xs18 sm9 md6>
                  <v-text-field
                    v-model="quiz.title"
                    label="Title"
                  ></v-text-field>
                </v-flex>
                <v-flex xs12 sm6 md4>
                    <br/>
                    <datetime type="datetime" v-model="quiz.availableDate">
                        <label for="startDate" slot="before">Available Date:</label>
                    </datetime>
                </v-flex>
              </v-layout>
            </v-container>
            <v-container grid-list-md fluid>
                <v-flex xs12 sm6 md6>
                    <v-text-field
                        v-model="search"
                        append-icon="search"
                        label="Search"
                        single-line
                        hide-details></v-text-field>
                </v-flex>
                <v-divider class="mx-4" inset vertical> </v-divider>
                <v-spacer></v-spacer>
                <v-data-table :key="tableChange"
                        :headers="headers"
                        :items="questions"
                        :search="search"
                        :custom-filter="customFilter"
                        :items-per-page="10"
                        :custom-sort="customSort"
                        must-sort
                        class="elevation-1">
                    <template slot="items" slot-scope="props">
                        <tr>
                            <td>{{ props.item.sequence }}</td>
                            <td
                                class="text-left"
                                @click="props.expanded = !props.expanded"
                                v-html="convertMarkDownNoFigure(props.item.content, props.item.image)"></td>
                            <td class="text-left">
		  	                    <span v-for="prop in props.item.topics" :key="prop">{{ prop }}</span></td>
                            <td>{{ props.item.difficulty }}</td>
                            <td>{{ props.item.numberOfAnswers }}</td>
                            <td class="text-left">{{ props.item.title }}</td>
                            <td><v-icon small class="mr-2"
                                    @click="openShowQuestionDialog(props.item.id)">
                                    visibility</v-icon>
                                <v-icon v-if="!props.item.sequence" small class="mr-2"
                                    @click="addToQuiz(props.item.id)">
                                    add</v-icon>
                                <v-icon v-if="props.item.sequence" small class="mr-2"
                                    @click="removeFromQuiz(props.item.id)">
                                    remove</v-icon>
                                <v-icon v-if="props.item.sequence && props.item.sequence !== 1" small class="mr-2"
                                    @click="moveFirst(props.item.id)">
                                    first_page</v-icon>
                                <v-icon v-if="props.item.sequence && props.item.sequence !== 1" small class="mr-2"
                                    @click="moveLeft(props.item.id)">
                                    chevron_left</v-icon>
                                <v-icon v-if="props.item.sequence" small class="mr-2"
                                    @click="moveRight(props.item.id)">
                                    expand_more</v-icon>
                                <v-icon v-if="props.item.sequence && props.item.sequence !== quizQuestions.length" small class="mr-2"
                                    @click="moveRight(props.item.id)">
                                    chevron_right</v-icon>
                                <v-icon v-if="props.item.sequence && props.item.sequence !== quizQuestions.length" small class="mr-2"
                                    @click="moveLast(props.item.id)">
                                    last_page</v-icon></td>
                        </tr>
                    </template>
                    <template slot="expand" slot-scope="props">
                        <v-simple-table>
                            <thead>
                                <tr>
                                    <th class="text-left">Id</th>
                                    <th class="text-left">Option</th>
                                    <th class="text-left">Correct</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="option in props.item.options" :key="option.id">
                                    <td class="text-left">{{ option.id }}</td>
                                    <td class="text-left"
                                        v-html="convertMarkDownNoFigure(option.content, null)"></td>
                                    <td><span v-if="option.correct">TRUE</span><span v-else>FALSE</span></td>
                                </tr>
                            </tbody>
                        </v-simple-table>
                    </template>
                </v-data-table>
            </v-container>
        </v-card-text>
    </v-card>
  </v-content>
</template>

<script lang="ts">
import { Component, Vue, Prop} from "vue-property-decorator";
import { Datetime } from 'vue-datetime';
import 'vue-datetime/dist/vue-datetime.css'
import RemoteServices from "@/services/RemoteServices";
import {
  convertMarkDown,
  convertMarkDownNoFigure
} from "@/services/ConvertMarkdownService";
import { Quiz } from "@/models/management/Quiz";
import { Question } from "@/models/management/Question";
import Image from "@/models/management/Image";

@Component({
  components: {
    Datetime,
  }
})
export default class QuizForm extends Vue {
    @Prop(Boolean) readonly editMode!: boolean;
    quiz: Quiz = new Quiz();
    questions: Question[] = [];
    search: string = "";
    tableChange: number = 0;
    showQuestion: boolean = false;
    questionToShow: Question | null | undefined = null;
    quizQuestions: Question[] = [];
    headers: object = [
        { text: "Sequence", value: "sequence", align: "left", width: "1%" },
        { text: "Question", value: "content", align: "left", width: "70%", sortable: false },
        { text: "Topics", value: "topics", align: "left", width: "20%", sortable: false },
        { text: "Difficulty", value: "difficulty", align: "center", width: "1%"},
        { text: "Answers", value: "numberOfAnswers", align: "center", width: "1%"},
        { text: "Title", value: "title", align: "left", width: "5%", sortable: false },
        { text: "Actions", value: "action", align: "center", width: "1%", sortable: false }
    ];

    constructor() {
        super();
    }

    // noinspection JSUnusedGlobalSymbols
    async beforeMount() {
        try {
            this.questions = await RemoteServices.getActiveQuestions();

            let questionIds: number[] = [];
            if (this.quiz.questions) {
                this.quiz.questions.forEach(question => {
                    questionIds.push(question.id);
                })
            }
            this.questions.forEach(question => {
                if (questionIds.includes(question.id)) {
                    question.sequence = questionIds.indexOf(question.id) + 1;
                }
            });
        } catch (error) {
            await this.$store.dispatch("error", error);
        }
    }

    switchMode() {
        this.$emit('switchMode');
    }

    async save() {
        try {
            this.quiz.type = 'TEACHER';
            this.quiz.year = (new Date()).getFullYear();
            this.quiz.questions = this.quizQuestions;
            let updatedQuiz: Quiz; 
            updatedQuiz = await RemoteServices.saveQuiz(this.quiz);
            this.quiz = new Quiz();
            this.$emit('updateQuiz', updatedQuiz);
        } catch(error) {
            await this.$store.dispatch("error", error);
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

    customSort(items: Question[], index: string, isDesc: boolean) {
      items.sort((a, b) => {
        if (index === "sequence") {
          if (!isDesc) {
            return this.compare(a.sequence, b.sequence);
          } else {
            return this.compare(b.sequence, a.sequence);
          }
        } else {
          if (!isDesc) {
            return a[index] < b[index] ? -1 : 1;
          } else {
            return b[index] < a[index] ? -1 : 1;
          }
        }
      });
      return items;
    }

    compare(a: number, b: number) {
        if (a === b) {
            return 0;
        } else if (a === null || a === undefined) {
            return 1;
        } else if (b === null || b === undefined) {
            return -1
        } else {
            return a < b ? -1 : 1;
        }
    }

    openShowQuestionDialog(questionId: number) {
        this.questionToShow = this.questions.find(
            question => question.id === questionId);
        this.showQuestion = true;
    }

    closeShowQuestionDialog() {
        this.questionToShow = null;
        this.showQuestion = false;
    }

    convertMarkDownNoFigure(text: string, image: Image | null = null): string {
        return convertMarkDownNoFigure(text, image);
    }

    renderQuestion(question: Question): string {
        let text =
            convertMarkDown(question.content, question.image) + " <br/> <br/> ";
        text =
            text +
            question.options
                .map(
                (option, index) =>
                    index.toString() +
                    " - " +
                    option.content +
                    (option.correct ? " (correct)" : "")
                )
                .join(" <br/> ");
        return text;
    }

    addToQuiz(questionId: number) {
        let question = this.questions.find(q => q.id === questionId);
        if (question) {
            question.sequence = this.quizQuestions.length + 1;
            this.quizQuestions.push(question);
            this.tableChange++;
        }
    }

    removeFromQuiz(questionId: number) {
        let question = this.questions.find(q => q.id === questionId);
        if (question) {
            let index: number = this.quizQuestions.indexOf(question);
            this.quizQuestions.splice(index , 1);
            question.sequence = null;
            this.quizQuestions.forEach((question, index) => {
                question.sequence = index + 1;
            })
            this.tableChange++;
        }
    }

    moveFirst(questionId: number) {
        let question = this.quizQuestions.find(q => q.id === questionId);
        if (question) {
            this.changeQuestionPosition(question, 0);
        }
    }

    moveLast(questionId: number) {
        let question = this.quizQuestions.find(q => q.id === questionId);
            if (question) {
                this.changeQuestionPosition(question, this.quizQuestions.length - 1);
            }
    }

    moveLeft(questionId: number) {
        let question = this.quizQuestions.find(q => q.id === questionId);
        if (question && question.sequence) {
            this.changeQuestionPosition(question, this.quizQuestions.indexOf(question) - 1);
        }
    }

    moveRight(questionId: number) {
        let question = this.quizQuestions.find(q => q.id === questionId);
        if (question && question.sequence) {
            this.changeQuestionPosition(question, this.quizQuestions.indexOf(question) + 1);
        }
    }

    changeQuestionPosition(question: Question, position: number) {
        if (question.sequence) {
            let currentPosition: number = this.quizQuestions.indexOf(question);
            this.quizQuestions.splice(position, 0, this.quizQuestions.splice(currentPosition, 1)[0]);
            this.quizQuestions.forEach((question, index) => {
                question.sequence = index + 1;
            })
            this.tableChange++;
        }
    }

}
</script>

<style lang="scss"></style>
