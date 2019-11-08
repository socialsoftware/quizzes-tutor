import TopicConjunctions from "@/models/management/TopicConjunction";

export default class Assessment {
  id: number | null = null;
  title: string = "";
  status: string = "AVAILABLE";
  topicConjunctions: TopicConjunctions[] = [];

  constructor(jsonObj?: Assessment) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.status = jsonObj.status;
      this.topicConjunctions = jsonObj.topicConjunctions.map(
        (topicConjunctionsDto: TopicConjunctions) => {
          return new TopicConjunctions(topicConjunctionsDto);
        }
      );
    }
  }
}
