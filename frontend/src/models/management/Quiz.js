import Question from '@/models/management/Question';
import { ISOtoString } from '@/services/ConvertDateService';
export class Quiz {
    constructor(jsonObj) {
        this.questions = [];
        if (jsonObj) {
            this.id = jsonObj.id;
            this.number = jsonObj.number;
            this.scramble = jsonObj.scramble;
            this.qrCodeOnly = jsonObj.qrCodeOnly;
            this.timed = jsonObj.timed;
            this.oneWay = jsonObj.oneWay;
            this.title = jsonObj.title;
            this.type = jsonObj.type;
            this.series = jsonObj.series;
            this.version = jsonObj.version;
            this.numberOfQuestions = jsonObj.numberOfQuestions;
            this.numberOfAnswers = jsonObj.numberOfAnswers;
            this.timeToConclusion = jsonObj.timeToConclusion;
            if (jsonObj.creationDate)
                this.creationDate = ISOtoString(jsonObj.creationDate);
            if (jsonObj.availableDate)
                this.availableDate = ISOtoString(jsonObj.availableDate);
            if (jsonObj.conclusionDate)
                this.conclusionDate = ISOtoString(jsonObj.conclusionDate);
            if (jsonObj.resultsDate)
                this.resultsDate = ISOtoString(jsonObj.resultsDate);
            if (jsonObj.questions) {
                this.questions = jsonObj.questions.map((question) => new Question(question));
            }
        }
    }
}
//# sourceMappingURL=Quiz.js.map