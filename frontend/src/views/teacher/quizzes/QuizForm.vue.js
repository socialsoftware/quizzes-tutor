import { __decorate } from "tslib";
import { Component, Vue, Prop, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { Quiz } from '@/models/management/Quiz';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import ShowQuizDialog from '@/views/teacher/quizzes/ShowQuizDialog.vue';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';
Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);
let QuizForm = class QuizForm extends Vue {
    constructor() {
        super(...arguments);
        this.questions = [];
        this.search = '';
        this.currentQuestion = null;
        this.quizQuestions = [];
        this.position = null;
        this.positionDialog = false;
        this.questionDialog = false;
        this.quizDialog = false;
        this.headers = [
            {
                text: 'Actions',
                value: 'action',
                align: 'left',
                width: '5px',
                sortable: false
            },
            {
                text: 'Sequence',
                value: 'sequence',
                align: 'center',
                width: '5px'
            },
            {
                text: 'Title',
                value: 'title',
                align: 'left',
                width: '60%',
                sortable: false
            },
            {
                text: 'Topics',
                value: 'topics',
                align: 'left',
                width: '40%'
            },
            { text: 'Answers', value: 'numberOfAnswers', align: 'center', width: '5px' }
        ];
    }
    async created() {
        await this.$store.dispatch('loading');
        try {
            this.questions = await RemoteServices.getAvailableQuestions();
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    onQuizChange() {
        let questionIds = [];
        if (this.quiz && this.quiz.questions) {
            this.quiz.questions.forEach(question => {
                if (!this.quizQuestions.includes(question) && question.id) {
                    questionIds.push(question.id);
                }
            });
        }
        this.questions.forEach(question => {
            if (question.id &&
                questionIds.includes(question.id) &&
                !this.quizQuestions
                    .map(quizQuestion => quizQuestion.id)
                    .includes(question.id)) {
                question.sequence = questionIds.indexOf(question.id) + 1;
                this.quizQuestions.push(question);
            }
        });
    }
    get canSave() {
        return (!!this.quiz.title &&
            !!this.quiz.availableDate &&
            ((this.quiz.timed && this.quiz.conclusionDate !== undefined) ||
                !this.quiz.timed));
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
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
    customFilter(value, search, question) {
        // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
        return (search != null &&
            JSON.stringify(question)
                .toLowerCase()
                .indexOf(search.toLowerCase()) !== -1);
    }
    customSort(items, index, isDesc) {
        items.sort((a, b) => {
            if (index == 'sequence') {
                if (isDesc == 'false') {
                    return this.compare(a.sequence, b.sequence);
                }
                else {
                    return this.compare(b.sequence, a.sequence);
                }
            }
            else {
                if (isDesc == 'false') {
                    return a[index] < b[index] ? -1 : 1;
                }
                else {
                    return b[index] < a[index] ? -1 : 1;
                }
            }
        });
        return items;
    }
    compare(a, b) {
        if (a == b) {
            return 0;
        }
        else if (a == null) {
            return 1;
        }
        else if (b == null) {
            return -1;
        }
        else {
            return a < b ? -1 : 1;
        }
    }
    showQuestionDialog(question) {
        this.currentQuestion = question;
        this.questionDialog = true;
    }
    onCloseShowQuestionDialog() {
        this.currentQuestion = null;
        this.questionDialog = false;
    }
    addToQuiz(question) {
        question.sequence = this.quizQuestions.length + 1;
        this.quizQuestions.push(question);
    }
    removeFromQuiz(question) {
        let index = this.quizQuestions.indexOf(question);
        this.quizQuestions.splice(index, 1);
        question.sequence = null;
        this.quizQuestions.forEach((question, index) => {
            question.sequence = index + 1;
        });
    }
    openSetPosition(question) {
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
        if (this.currentQuestion &&
            this.currentQuestion.sequence !== this.position &&
            this.position &&
            this.position > 0 &&
            this.position <= this.quizQuestions.length) {
            this.changeQuestionPosition(this.currentQuestion, this.position - 1);
        }
        this.closeSetPosition();
    }
    changeQuestionPosition(question, position) {
        if (question.sequence) {
            let currentPosition = this.quizQuestions.indexOf(question);
            this.quizQuestions.splice(position, 0, this.quizQuestions.splice(currentPosition, 1)[0]);
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
};
__decorate([
    Prop(Quiz)
], QuizForm.prototype, "quiz", void 0);
__decorate([
    Prop(Boolean)
], QuizForm.prototype, "editMode", void 0);
__decorate([
    Watch('quiz')
], QuizForm.prototype, "onQuizChange", null);
QuizForm = __decorate([
    Component({
        components: {
            'show-question-dialog': ShowQuestionDialog,
            'show-quiz-dialog': ShowQuizDialog
        }
    })
], QuizForm);
export default QuizForm;
//# sourceMappingURL=QuizForm.vue.js.map