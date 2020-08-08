import { __decorate } from "tslib";
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';
let ShowStudentAnswersDialog = class ShowStudentAnswersDialog extends Vue {
    constructor() {
        super(...arguments);
        this.search = '';
        this.timeout = null;
        this.headers = [
            { text: 'Name', value: 'name', align: 'left', width: '5%' },
            {
                text: 'Username',
                value: 'username',
                align: 'center',
                width: '5%'
            },
            {
                text: 'Start Date',
                value: 'creationDate',
                align: 'center',
                width: '5%'
            },
            {
                text: 'Submission Lag',
                value: 'submissionLag',
                align: 'center',
                width: '5%'
            },
            {
                text: 'Answers',
                value: 'answers',
                align: 'center',
                width: '5%'
            }
        ];
    }
    convertToHHMMSS(time) {
        return milisecondsToHHMMSS(time);
    }
    convertToLetter(number) {
        if (number === undefined) {
            return 'X';
        }
        else {
            return String.fromCharCode(65 + number);
        }
    }
};
__decorate([
    Model('dialog', Boolean)
], ShowStudentAnswersDialog.prototype, "dialog", void 0);
__decorate([
    Prop({ required: true })
], ShowStudentAnswersDialog.prototype, "quizAnswers", void 0);
__decorate([
    Prop({ required: true })
], ShowStudentAnswersDialog.prototype, "conclusionDate", void 0);
ShowStudentAnswersDialog = __decorate([
    Component
], ShowStudentAnswersDialog);
export default ShowStudentAnswersDialog;
//# sourceMappingURL=ShowQuizAnswersDialog.vue.js.map