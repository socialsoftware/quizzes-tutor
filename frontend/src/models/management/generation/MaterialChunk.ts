// A piece of a course material (about a page of text) and the topics it is under (none: under no topic)
export default class MaterialChunk {
  id!: string;
  position!: number;
  // The headings above the piece ("Part I > 1 Numbers"), only to find one's way in the document
  heading: string | null = null;
  text!: string;
  topicIds: number[] = [];

  constructor(jsonObj?: MaterialChunk) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.position = jsonObj.position;
      this.heading = jsonObj.heading ?? null;
      this.text = jsonObj.text;
      this.topicIds = jsonObj.topicIds ?? [];
    }
  }
}
