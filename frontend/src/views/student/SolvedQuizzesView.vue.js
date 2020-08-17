import { __decorate } from "tslib";
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StatementManager from '@/models/statement/StatementManager';
let AvailableQuizzesView = class AvailableQuizzesView extends Vue {
    constructor() {
        super(...arguments);
        this.quizzes = [];
    }
    async created() {
        await this.$store.dispatch('loading');
        try {
            this.quizzes = (await RemoteServices.getSolvedQuizzes()).reverse();
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    calculateScore(quiz) {
        let correct = 0;
        for (let i = 0; i < quiz.statementQuiz.questions.length; i++) {
            if (quiz.statementQuiz.answers[i] &&
                quiz.correctAnswers[i].correctOptionId ===
                    quiz.statementQuiz.answers[i].optionId) {
                correct += 1;
            }
        }
        return `${correct}/${quiz.statementQuiz.questions.length}`;
    }
    async showResults(quiz) {
        let statementManager = StatementManager.getInstance;
        statementManager.correctAnswers = quiz.correctAnswers;
        statementManager.statementQuiz = quiz.statementQuiz;
        await this.$router.push({ name: 'quiz-results' });
    }
};
AvailableQuizzesView = __decorate([
    Component
], AvailableQuizzesView);
export default AvailableQuizzesView;
//# sourceMappingURL=SolvedQuizzesView.vue.js.map