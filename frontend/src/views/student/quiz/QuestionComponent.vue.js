import { __decorate } from "tslib";
import { Component, Vue, Prop, Model, Emit } from 'vue-property-decorator';
import StatementQuestion from '@/models/statement/StatementQuestion';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
let QuestionComponent = class QuestionComponent extends Vue {
    constructor() {
        super(...arguments);
        this.hover = false;
        this.optionLetters = ['A', 'B', 'C', 'D'];
    }
    increaseOrder() {
        return 1;
    }
    decreaseOrder() {
        return 1;
    }
    selectOption(optionId) {
        return optionId;
    }
    convertMarkDown(text, image = null) {
        return convertMarkDown(text, image);
    }
};
__decorate([
    Model('questionOrder', Number)
], QuestionComponent.prototype, "questionOrder", void 0);
__decorate([
    Prop(StatementQuestion)
], QuestionComponent.prototype, "question", void 0);
__decorate([
    Prop(Number)
], QuestionComponent.prototype, "optionId", void 0);
__decorate([
    Prop()
], QuestionComponent.prototype, "questionNumber", void 0);
__decorate([
    Prop()
], QuestionComponent.prototype, "backsies", void 0);
__decorate([
    Emit()
], QuestionComponent.prototype, "increaseOrder", null);
__decorate([
    Emit()
], QuestionComponent.prototype, "decreaseOrder", null);
__decorate([
    Emit()
], QuestionComponent.prototype, "selectOption", null);
QuestionComponent = __decorate([
    Component
], QuestionComponent);
export default QuestionComponent;
//# sourceMappingURL=QuestionComponent.vue.js.map