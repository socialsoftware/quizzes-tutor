import { __decorate } from "tslib";
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';
let ShowQuestionDialog = class ShowQuestionDialog extends Vue {
};
__decorate([
    Model('dialog', Boolean)
], ShowQuestionDialog.prototype, "dialog", void 0);
__decorate([
    Prop({ type: Question, required: true })
], ShowQuestionDialog.prototype, "question", void 0);
ShowQuestionDialog = __decorate([
    Component({
        components: {
            'show-question': ShowQuestion
        }
    })
], ShowQuestionDialog);
export default ShowQuestionDialog;
//# sourceMappingURL=ShowQuestionDialog.vue.js.map