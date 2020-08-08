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
            this.quizzes = (await RemoteServices.getAvailableQuizzes()).reverse();
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    async solveQuiz(quiz) {
        let statementManager = StatementManager.getInstance;
        try {
            statementManager.statementQuiz = await RemoteServices.startQuiz(quiz.id);
            await this.$router.push({ name: 'solve-quiz' });
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
};
AvailableQuizzesView = __decorate([
    Component
], AvailableQuizzesView);
export default AvailableQuizzesView;
//# sourceMappingURL=AvailableQuizzesView.vue.js.map