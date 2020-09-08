import { __decorate } from "tslib";
import { Component, Prop, Vue } from 'vue-property-decorator';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';
let ShowQuestionList = class ShowQuestionList extends Vue {
};
__decorate([
    Prop({ type: Array, required: true })
], ShowQuestionList.prototype, "questions", void 0);
ShowQuestionList = __decorate([
    Component({
        components: { 'show-question': ShowQuestion }
    })
], ShowQuestionList);
export default ShowQuestionList;
//# sourceMappingURL=ShowQuestionList.vue.js.map