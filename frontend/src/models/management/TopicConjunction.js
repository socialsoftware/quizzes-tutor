import Topic from '@/models/management/Topic';
export default class TopicConjunction {
    constructor(jsonObj) {
        this.topics = [];
        if (jsonObj) {
            this.id = jsonObj.id;
            this.topics = jsonObj.topics.map((topicDto) => {
                return new Topic(topicDto);
            });
        }
        this.sequence = TopicConjunction.maxSequence++;
    }
}
TopicConjunction.maxSequence = 0;
//# sourceMappingURL=TopicConjunction.js.map