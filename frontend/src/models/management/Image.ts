export default class Image {
  url: string;
  width: number | null;

  constructor(url: string, width: number|null) {
    this.url = url;
    this.width = width;
  }
}
