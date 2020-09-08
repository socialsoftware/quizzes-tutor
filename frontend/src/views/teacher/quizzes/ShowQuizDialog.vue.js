import { __decorate } from "tslib";
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import { Quiz } from '@/models/management/Quiz';
import ShowQuestionList from '@/views/teacher/questions/ShowQuestionList.vue';
let ShowQuizDialog = class ShowQuizDialog extends Vue {
};
__decorate([
    Model('dialog', Boolean)
], ShowQuizDialog.prototype, "dialog", void 0);
__decorate([
    Prop({ type: Quiz, required: true })
], ShowQuizDialog.prototype, "quiz", void 0);
ShowQuizDialog = __decorate([
    Component({
        components: {
            'show-question-list': ShowQuestionList
        }
    })
], ShowQuizDialog);
export default ShowQuizDialog;
//# sourceMappingURL=ShowQuizDialog.vue.js.map