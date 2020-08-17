import { __decorate } from "tslib";
import { Component, Prop, Vue } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';
let EditQuestionTopics = class EditQuestionTopics extends Vue {
    constructor() {
        super(...arguments);
        this.questionTopics = JSON.parse(JSON.stringify(this.question.topics));
    }
    async saveTopics() {
        if (this.question.id) {
            try {
                await RemoteServices.updateQuestionTopics(this.question.id, this.questionTopics);
            }
            catch (error) {
                await this.$store.dispatch('error', error);
            }
        }
        this.$emit('question-changed-topics', this.question.id, this.questionTopics);
    }
    removeTopic(topic) {
        this.questionTopics = this.questionTopics.filter(element => element.id != topic.id);
        this.saveTopics();
    }
};
__decorate([
    Prop({ type: Question, required: true })
], EditQuestionTopics.prototype, "question", void 0);
__decorate([
    Prop({ type: Array, required: true })
], EditQuestionTopics.prototype, "topics", void 0);
EditQuestionTopics = __decorate([
    Component
], EditQuestionTopics);
export default EditQuestionTopics;
//# sourceMappingURL=EditQuestionTopics.vue.js.map