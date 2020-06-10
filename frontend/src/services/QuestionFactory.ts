import Question from '@/models/management/Question';
import MultipleChoiceQuestion from '@/models/management/questions/MultipleChoiceQuestion';
import CodeFillInQuestion from '@/models/management/questions/CodeFillInQuestion';
import StatementQuestionCodeFillIn from '@/models/statement/StatementQuestionCodeFillIn';
import StatementQuestionMultipleChoice from '@/models/statement/StatementQuestionMultipleChoice';
import StatementQuestion from '@/models/statement/StatementQuestion';

export class QuestionFactory {
  static createQuestion(question: any): Question {
    if (question.type === 'multiple_choice') {
      return new MultipleChoiceQuestion(question);
    } else if (question.type === 'code_fill_in') {
      return new CodeFillInQuestion(question);
    } else {
      throw new Error('Unknown question type.');
    }
  }

  static createStatementQuestion(question: any): StatementQuestion {
    if (question.type === 'multiple_choice') {
      return new StatementQuestionMultipleChoice(question);
    } else if (question.type === 'code_fill_in') {
      return new StatementQuestionCodeFillIn(question);
    } else {
      throw new Error('Unknown question type.');
    }
  }
}
