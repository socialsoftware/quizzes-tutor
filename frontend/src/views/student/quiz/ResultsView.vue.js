import { __decorate } from "tslib";
import { Component, Vue } from 'vue-property-decorator';
import StatementManager from '@/models/statement/StatementManager';
import ResultComponent from '@/views/student/quiz/ResultComponent.vue';
let ResultsView = class ResultsView extends Vue {
    constructor() {
        super(...arguments);
        this.statementManager = StatementManager.getInstance;
        this.questionOrder = 0;
    }
    async created() {
        if (this.statementManager.isEmpty()) {
            await this.$router.push({ name: 'create-quiz' });
        }
        else if (this.statementManager.correctAnswers.length === 0) {
            await this.$store.dispatch('loading');
            setTimeout(() => {
                this.statementManager.concludeQuiz();
            }, 2000);
            await this.$store.dispatch('clearLoading');
        }
    }
    increaseOrder() {
        if (this.questionOrder + 1 <
            +this.statementManager.statementQuiz.questions.length) {
            this.questionOrder += 1;
        }
    }
    decreaseOrder() {
        if (this.questionOrder > 0) {
            this.questionOrder -= 1;
        }
    }
    changeOrder(n) {
        if (n >= 0 && n < +this.statementManager.statementQuiz.questions.length) {
            this.questionOrder = n;
        }
    }
};
ResultsView = __decorate([
    Component({
        components: {
            'result-component': ResultComponent
        }
    })
], ResultsView);
export default ResultsView;
//# sourceMappingURL=ResultsView.vue.js.map