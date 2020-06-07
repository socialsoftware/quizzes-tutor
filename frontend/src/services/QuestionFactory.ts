import Question from '@/models/management/Question';
import MultipleChoiceQuestion from '@/models/management/questions/MultipleChoiceQuestion';
import CodeFillInQuestion from '@/models/management/questions/CodeFillInQuestion';

export class QuestionFactory {
  static createQuestion(question: any): Question {
    if (question.type === 'multiple_choice') {
      console.log("no cool");
      return new MultipleChoiceQuestion(question);
    } else if (question.type === 'code_fill_in') {
      console.log("cool");
      return new CodeFillInQuestion(question);
    } else {
      throw new Error('Unknown question type.');
    }
  }
}
