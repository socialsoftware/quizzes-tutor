import TopicConjuctions from "@/models/management/TopicConjuction";

export default class Assessment {
  id: number | null = null;
  title: string = "";
  status: string = "AVAILABLE";
  topicConjuctions: TopicConjuctions[] = [];

  constructor(jsonObj?: Assessment) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.status = jsonObj.status;
      this.topicConjuctions = jsonObj.topicConjuctions.map(
        (topicConjuctionsDto: TopicConjuctions) => {
          return new TopicConjuctions(topicConjuctionsDto);
        }
      );
    }
  }
}
