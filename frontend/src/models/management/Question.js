import Option from '@/models/management/Option';
import Topic from '@/models/management/Topic';
import { ISOtoString } from '@/services/ConvertDateService';
export default class Question {
    constructor(jsonObj) {
        this.id = null;
        this.title = '';
        this.status = 'AVAILABLE';
        this.content = '';
        this.image = null;
        this.sequence = null;
        this.options = [new Option(), new Option(), new Option(), new Option()];
        this.topics = [];
        if (jsonObj) {
            this.id = jsonObj.id;
            this.title = jsonObj.title;
            this.status = jsonObj.status;
            this.numberOfAnswers = jsonObj.numberOfAnswers;
            this.numberOfGeneratedQuizzes = jsonObj.numberOfGeneratedQuizzes;
            this.numberOfNonGeneratedQuizzes = jsonObj.numberOfNonGeneratedQuizzes;
            this.numberOfCorrect = jsonObj.numberOfCorrect;
            this.difficulty = jsonObj.difficulty;
            this.content = jsonObj.content;
            this.creationDate = ISOtoString(jsonObj.creationDate);
            this.image = jsonObj.image;
            this.options = jsonObj.options.map((option) => new Option(option));
            this.topics = jsonObj.topics.map((topic) => new Topic(topic));
        }
    }
}
//# sourceMappingURL=Question.js.map