import Topic from "@/models/management/Topic";

export default class TopicConjuctions {
  static maxSequence = 0;
  id!: number;
  sequence: number;
  topics: Topic[] = [];

  constructor(jsonObj?: TopicConjuctions) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.topics = jsonObj.topics.map((topicDto: Topic) => {
        return new Topic(topicDto);
      });
    }
    this.sequence = TopicConjuctions.maxSequence++;
  }
}
