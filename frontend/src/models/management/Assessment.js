import TopicConjunction from '@/models/management/TopicConjunction';
export default class Assessment {
    constructor(jsonObj) {
        this.id = null;
        this.title = '';
        this.status = 'AVAILABLE';
        this.topicConjunctions = [];
        if (jsonObj) {
            this.id = jsonObj.id;
            this.title = jsonObj.title;
            this.status = jsonObj.status;
            this.sequence = jsonObj.sequence;
            this.numberOfQuestions = jsonObj.numberOfQuestions;
            this.topicConjunctions = jsonObj.topicConjunctions.map((topicConjunctionsDto) => {
                return new TopicConjunction(topicConjunctionsDto);
            });
        }
    }
}
//# sourceMappingURL=Assessment.js.map