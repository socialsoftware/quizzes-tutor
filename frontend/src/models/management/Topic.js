export default class Topic {
    constructor(jsonObj) {
        if (jsonObj) {
            this.id = jsonObj.id;
            this.name = jsonObj.name;
            this.numberOfQuestions = jsonObj.numberOfQuestions;
        }
    }
}
//# sourceMappingURL=Topic.js.map