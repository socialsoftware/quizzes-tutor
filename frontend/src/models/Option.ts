interface ServerResponse {
  data: ServerOption;
}

interface ServerOption {
  content: string | null;
  correct: Boolean | null;
  option: number | null;
}

export default class Option implements ServerOption {
  content: string | null;
  correct: Boolean | null;
  option: number | null;

  constructor(jsonObj: any) {
    this.content = jsonObj.content;
    this.correct = jsonObj.correct;
    this.option = jsonObj.option;
  }
}
