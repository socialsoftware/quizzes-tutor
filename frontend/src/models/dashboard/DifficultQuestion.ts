import Question from '@/models/management/Question';

export default class DifficultQuestion {
  id!: number;
  percentage!: number;
  questionDto!: Question;

  constructor(jsonObj?: DifficultQuestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.percentage = jsonObj.percentage;
      this.questionDto = new Question(jsonObj.questionDto);
    }
  }
}
