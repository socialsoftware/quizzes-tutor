import TopicConjunction from '@/models/management/TopicConjunction';

export default class Assessment {
  id: number | null = null;
  title: string = '';
  sequence!: number;
  numberOfQuestions!: number;
  status: string = 'AVAILABLE';
  topicConjunctions: TopicConjunction[] = [];

  constructor(jsonObj?: Assessment) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.status = jsonObj.status;
      this.sequence = jsonObj.sequence;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.topicConjunctions = jsonObj.topicConjunctions.map(
        (topicConjunctionsDto: TopicConjunction) => {
          return new TopicConjunction(topicConjunctionsDto);
        }
      );
    }
  }
}
