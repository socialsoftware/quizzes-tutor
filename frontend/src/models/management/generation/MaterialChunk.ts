// A piece of a course material (about a page of text) and the topic it is under, if any
export default class MaterialChunk {
  id!: string;
  position!: number;
  // The headings above the piece ("Part I > 1 Numbers"), only to find one's way in the document
  heading: string | null = null;
  text!: string;
  topicId: number | null = null;

  constructor(jsonObj?: MaterialChunk) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.position = jsonObj.position;
      this.heading = jsonObj.heading ?? null;
      this.text = jsonObj.text;
      this.topicId = jsonObj.topicId ?? null;
    }
  }
}
