import RemoteServices from '@/services/RemoteServices';
export default class StatementManager {
    constructor() {
        this.numberOfQuestions = '5';
        this.statementQuiz = null;
        this.correctAnswers = [];
    }
    static get getInstance() {
        return this._quiz;
    }
    async getQuizStatement() {
        let params = {
            assessment: this.assessment,
            numberOfQuestions: +this.numberOfQuestions
        };
        this.statementQuiz = await RemoteServices.generateStatementQuiz(params);
    }
    async concludeQuiz() {
        if (this.statementQuiz) {
            this.correctAnswers = await RemoteServices.concludeQuiz(this.statementQuiz);
        }
        else {
            throw Error('No quiz');
        }
    }
    reset() {
        this.statementQuiz = null;
        this.correctAnswers = [];
    }
    isEmpty() {
        return this.statementQuiz == null;
    }
}
StatementManager._quiz = new StatementManager();
//# sourceMappingURL=StatementManager.js.map