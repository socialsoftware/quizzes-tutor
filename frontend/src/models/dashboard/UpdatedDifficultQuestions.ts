import { ISOtoString } from '@/services/ConvertDateService';
import DifficultQuestion from '@/models/dashboard/DifficultQuestion';

export default class UpdatedDifficultQuestions {
  lastCheckDifficultQuestions!: string;
  difficultQuestions!: DifficultQuestion[];

  constructor(jsonObj?: UpdatedDifficultQuestions) {
    if (jsonObj) {
      this.lastCheckDifficultQuestions = ISOtoString(
        jsonObj.lastCheckDifficultQuestions
      );
      this.difficultQuestions = jsonObj.difficultQuestions.map(
        (difficultQuestion: any) => {
          return new DifficultQuestion(difficultQuestion);
        }
      );
    }
  }
}
