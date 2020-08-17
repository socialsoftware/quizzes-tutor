import { __decorate } from "tslib";
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { QrcodeStream } from 'vue-qrcode-reader';
import StatementManager from '@/models/statement/StatementManager';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';
let ScanView = class ScanView extends Vue {
    constructor() {
        super(...arguments);
        this.quizId = null;
        this.quiz = null;
        this.timer = '';
    }
    async onDecode(decodedString) {
        this.quizId = Number(decodedString);
        this.getQuizByQRCode();
    }
    async getQuizByQRCode() {
        if (this.quizId && this.$router.currentRoute.name === 'scan') {
            try {
                this.quiz = await RemoteServices.getQuizByQRCode(this.quizId);
                if (!this.quiz.timeToAvailability) {
                    let statementManager = StatementManager.getInstance;
                    statementManager.statementQuiz = this.quiz;
                    await this.$router.push({ name: 'solve-quiz' });
                }
            }
            catch (error) {
                await this.$store.dispatch('error', error);
                await this.$router.push({ name: 'home' });
            }
        }
    }
    timerMethod() {
        if (!!this.quiz && !this.quiz.timeToAvailability) {
            this.getQuizByQRCode();
        }
        this.timer = milisecondsToHHMMSS(this.quiz?.timeToAvailability);
    }
};
__decorate([
    Watch('quiz.timeToAvailability')
], ScanView.prototype, "timerMethod", null);
ScanView = __decorate([
    Component({
        components: {
            'qrcode-stream': QrcodeStream
        }
    })
], ScanView);
export default ScanView;
//# sourceMappingURL=ScanView.vue.js.map