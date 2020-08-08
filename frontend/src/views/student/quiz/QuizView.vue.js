import { __decorate } from "tslib";
import { Component, Vue, Watch } from 'vue-property-decorator';
import QuestionComponent from '@/views/student/quiz/QuestionComponent.vue';
import StatementManager from '@/models/statement/StatementManager';
import RemoteServices from '@/services/RemoteServices';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';
let QuizView = class QuizView extends Vue {
    constructor() {
        super(...arguments);
        this.statementManager = StatementManager.getInstance;
        this.statementQuiz = StatementManager.getInstance.statementQuiz;
        this.confirmationDialog = false;
        this.confirmed = false;
        this.nextConfirmationDialog = false;
        this.startTime = new Date();
        this.questionOrder = 0;
        this.hideTime = false;
        this.quizSubmitted = false;
    }
    async created() {
        if (!this.statementQuiz?.id) {
            await this.$router.push({ name: 'create-quiz' });
        }
    }
    increaseOrder() {
        if (this.questionOrder + 1 < +this.statementQuiz.questions.length) {
            this.calculateTime();
            this.questionOrder += 1;
        }
        this.nextConfirmationDialog = false;
    }
    decreaseOrder() {
        if (this.questionOrder > 0 && !this.statementQuiz?.oneWay) {
            this.calculateTime();
            this.questionOrder -= 1;
        }
    }
    changeOrder(newOrder) {
        if (!this.statementQuiz?.oneWay) {
            if (newOrder >= 0 && newOrder < +this.statementQuiz.questions.length) {
                this.calculateTime();
                this.questionOrder = newOrder;
            }
        }
    }
    async changeAnswer(optionId) {
        if (this.statementQuiz && this.statementQuiz.answers[this.questionOrder]) {
            try {
                this.calculateTime();
                let newAnswer = { ...this.statementQuiz.answers[this.questionOrder] };
                if (newAnswer.optionId === optionId) {
                    newAnswer.optionId = null;
                }
                else {
                    newAnswer.optionId = optionId;
                }
                if (!!this.statementQuiz && this.statementQuiz.timed) {
                    newAnswer.timeToSubmission = this.statementQuiz.timeToSubmission;
                    RemoteServices.submitAnswer(this.statementQuiz.id, newAnswer);
                }
                this.statementQuiz.answers[this.questionOrder].optionId =
                    newAnswer.optionId;
            }
            catch (error) {
                await this.$store.dispatch('error', error);
            }
        }
    }
    confirmAnswer() {
        if (this.statementQuiz?.oneWay &&
            this.questionOrder + 1 < +this.statementQuiz.questions.length) {
            this.nextConfirmationDialog = true;
        }
        else {
            this.increaseOrder();
        }
    }
    submissionTimerWatcher() {
        if (!!this.statementQuiz && !this.statementQuiz.timeToSubmission) {
            this.concludeQuiz();
        }
    }
    convertToHHMMSS(time) {
        return milisecondsToHHMMSS(time);
    }
    async concludeQuiz() {
        await this.$store.dispatch('loading');
        try {
            this.calculateTime();
            this.confirmed = true;
            await this.statementManager.concludeQuiz();
            if (this.statementManager.correctAnswers.length !== 0) {
                await this.$router.push({ name: 'quiz-results' });
            }
            else {
                this.quizSubmitted = true;
            }
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    calculateTime() {
        if (this.statementQuiz) {
            this.statementQuiz.answers[this.questionOrder].timeTaken +=
                new Date().getTime() - this.startTime.getTime();
            this.startTime = new Date();
        }
    }
};
__decorate([
    Watch('statementQuiz.timeToSubmission')
], QuizView.prototype, "submissionTimerWatcher", null);
QuizView = __decorate([
    Component({
        components: {
            'question-component': QuestionComponent
        }
    })
], QuizView);
export default QuizView;
//# sourceMappingURL=QuizView.vue.js.map