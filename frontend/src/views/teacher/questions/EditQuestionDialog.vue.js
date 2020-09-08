import { __decorate } from "tslib";
import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';
let EditQuestionDialog = class EditQuestionDialog extends Vue {
    created() {
        this.updateQuestion();
    }
    updateQuestion() {
        this.editQuestion = new Question(this.question);
    }
    // TODO use EasyMDE with these configs
    // markdownConfigs: object = {
    //   status: false,
    //   spellChecker: false,
    //   insertTexts: {
    //     image: ['![image][image]', '']
    //   }
    // };
    async saveQuestion() {
        this.$refs.form.validate();
        try {
            const result = this.editQuestion.id != null
                ? await RemoteServices.updateQuestion(this.editQuestion)
                : await RemoteServices.createQuestion(this.editQuestion);
            this.$emit('save-question', result);
        }
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
};
__decorate([
    Model('dialog', Boolean)
], EditQuestionDialog.prototype, "dialog", void 0);
__decorate([
    Prop({ type: Question, required: true })
], EditQuestionDialog.prototype, "question", void 0);
__decorate([
    Watch('question', { immediate: true, deep: true })
], EditQuestionDialog.prototype, "updateQuestion", null);
EditQuestionDialog = __decorate([
    Component
], EditQuestionDialog);
export default EditQuestionDialog;
//# sourceMappingURL=EditQuestionDialog.vue.js.map