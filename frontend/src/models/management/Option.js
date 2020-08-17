export default class Option {
    constructor(jsonObj) {
        this.id = null;
        this.content = '';
        this.correct = false;
        if (jsonObj) {
            this.id = jsonObj.id;
            this.sequence = jsonObj.sequence;
            this.content = jsonObj.content;
            this.correct = jsonObj.correct;
        }
    }
}
//# sourceMappingURL=Option.js.map