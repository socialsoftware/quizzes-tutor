export default class Image {
  url!: string;
  width: number | null = null;

  constructor(jsonObj?: Image) {
    if (jsonObj) {
      this.url = jsonObj.url;
      this.width = jsonObj.width;
    }
  }
}
