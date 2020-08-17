import { __decorate } from "tslib";
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
let ShowQuestion = class ShowQuestion extends Vue {
    convertMarkDown(text, image = null) {
        return convertMarkDown(text, image);
    }
};
__decorate([
    Prop({ type: Question, required: true })
], ShowQuestion.prototype, "question", void 0);
ShowQuestion = __decorate([
    Component
], ShowQuestion);
export default ShowQuestion;
//# sourceMappingURL=ShowQuestion.vue.js.map