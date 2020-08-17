import { __decorate } from "tslib";
import { Component, Vue } from 'vue-property-decorator';
import StatementManager from '@/models/statement/StatementManager';
import RemoteServices from '@/services/RemoteServices';
let CreateQuizzesView = class CreateQuizzesView extends Vue {
    constructor() {
        super(...arguments);
        this.statementManager = StatementManager.getInstance;
        this.availableAssessments = [];
    }
    async created() {
        await this.$store.dispatch('loading');
        this.statementManager.reset();
        try {
            this.availableAssessments = await RemoteServices.getAvailableAssessments();
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
    }
    async createQuiz() {
        try {
            await this.statementManager.getQuizStatement();
            await this.$router.push({ name: 'solve-quiz' });
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
};
CreateQuizzesView = __decorate([
    Component
], CreateQuizzesView);
export default CreateQuizzesView;
//# sourceMappingURL=CreateQuizzesView.vue.js.map