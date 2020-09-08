import { __decorate } from "tslib";
import { Component, Vue, Prop } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import ShowQuizDialog from '@/views/teacher/quizzes/ShowQuizDialog.vue';
import ShowQuizAnswersDialog from '@/views/teacher/quizzes/ShowQuizAnswersDialog.vue';
import VueQrcode from 'vue-qrcode';
let QuizList = class QuizList extends Vue {
    constructor() {
        super(...arguments);
        this.quiz = null;
        this.quizAnswers = null;
        this.correctSequence = [];
        this.timeToSubmission = 0;
        this.search = '';
        this.quizDialog = false;
        this.quizAnswersDialog = false;
        this.qrcodeDialog = false;
        this.qrValue = null;
        this.headers = [
            {
                text: 'Actions',
                value: 'action',
                align: 'left',
                width: '150px',
                sortable: false
            },
            { text: 'Title', value: 'title', align: 'left', width: '30%' },
            {
                text: 'Available Date',
                value: 'availableDate',
                align: 'center',
                width: '150px'
            },
            {
                text: 'Conclusion Date',
                value: 'conclusionDate',
                align: 'center',
                width: '150px'
            },
            {
                text: 'Results Date',
                value: 'resultsDate',
                align: 'center',
                width: '150px'
            },
            { text: 'Options', value: 'options', align: 'center', width: '150px' },
            {
                text: 'Questions',
                value: 'numberOfQuestions',
                width: '5px',
                align: 'center'
            },
            {
                text: 'Answers',
                value: 'numberOfAnswers',
                width: '5px',
                align: 'center'
            },
            {
                text: 'Creation Date',
                value: 'creationDate',
                width: '150px',
                align: 'center'
            }
        ];
    }
    async showQuizDialog(quizId) {
        try {
            this.quiz = await RemoteServices.getQuiz(quizId);
            this.quizDialog = true;
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
    async showQuizAnswers(quiz) {
        try {
            this.quizAnswers = await RemoteServices.getQuizAnswers(quiz.id);
            this.quiz = quiz;
            this.quizAnswersDialog = true;
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
    editQuiz(quiz, e) {
        if (e)
            e.preventDefault();
        this.$emit('editQuiz', quiz.id);
    }
    showQrCode(quizId) {
        this.qrValue = quizId;
        this.qrcodeDialog = true;
    }
    async exportQuiz(quizId) {
        let fileName = this.quizzes.filter(quiz => quiz.id == quizId)[0].title + '.zip';
        try {
            let result = await RemoteServices.exportQuiz(quizId);
            const url = window.URL.createObjectURL(result);
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', fileName);
            document.body.appendChild(link);
            link.click();
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
    async deleteQuiz(quizId) {
        if (confirm('Are you sure you want to delete this quiz?')) {
            try {
                await RemoteServices.deleteQuiz(quizId);
                this.$emit('deleteQuiz', quizId);
            }
            catch (error) {
                await this.$store.dispatch('error', error);
            }
        }
    }
    async populateWithQuizAnswers(quizId) {
        try {
            let quiz = await RemoteServices.populateWithQuizAnswers(quizId);
            this.$emit('updateQuiz', quiz);
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
    async removeNonAnsweredQuizAnswers(quizId) {
        try {
            let quiz = await RemoteServices.removeNonAnsweredQuizAnswers(quizId);
            this.$emit('updateQuiz', quiz);
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
};
__decorate([
    Prop({ type: Array, required: true })
], QuizList.prototype, "quizzes", void 0);
QuizList = __decorate([
    Component({
        components: {
            'show-quiz-answers-dialog': ShowQuizAnswersDialog,
            'show-quiz-dialog': ShowQuizDialog,
            'vue-qrcode': VueQrcode
        }
    })
], QuizList);
export default QuizList;
//# sourceMappingURL=QuizList.vue.js.map