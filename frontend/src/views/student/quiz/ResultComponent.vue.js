import { __decorate } from "tslib";
import { Component, Vue, Prop, Model, Emit } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementAnswer from '@/models/statement/StatementAnswer';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
let ResultComponent = class ResultComponent extends Vue {
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
    convertMarkDown(text, image = null) {
        return convertMarkDown(text, image);
    }
};
__decorate([
    Model('questionOrder', Number)
], ResultComponent.prototype, "questionOrder", void 0);
__decorate([
    Prop(StatementQuestion)
], ResultComponent.prototype, "question", void 0);
__decorate([
    Prop(StatementCorrectAnswer)
], ResultComponent.prototype, "correctAnswer", void 0);
__decorate([
    Prop(StatementAnswer)
], ResultComponent.prototype, "answer", void 0);
__decorate([
    Prop()
], ResultComponent.prototype, "questionNumber", void 0);
__decorate([
    Emit()
], ResultComponent.prototype, "increaseOrder", null);
__decorate([
    Emit()
], ResultComponent.prototype, "decreaseOrder", null);
ResultComponent = __decorate([
    Component
], ResultComponent);
export default ResultComponent;
//# sourceMappingURL=ResultComponent.vue.js.map