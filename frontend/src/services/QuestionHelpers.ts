import QuestionDetails from '@/models/management/questions/QuestionDetails';
import MultipleChoiceQuestionDetails from '@/models/management/questions/MultipleChoiceQuestionDetails';
import MultipleChoiceAnswerDetails from '@/models/management/questions/MultipleChoiceAnswerDetails';
import AnswerDetails from '@/models/management/questions/AnswerDetails';
import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import MultipleChoiceStatementQuestionDetails from '@/models/statement/questions/MultipleChoiceStatementQuestionDetails';
import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import MultipleChoiceStatementCorrectAnswerDetails from '@/models/statement/questions/MultipleChoiceStatementCorrectAnswerDetails';
import MultipleChoiceStatementAnswerDetails from '@/models/statement/questions/MultipleChoiceStatementAnswerDetails';
import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';

export const enum QuestionTypes {
  MultipleChoice = 'multiple_choice'
}

export function convertToLetter(number: number | null) {
  console.log(number);
  if (number === null) {
    return 'X';
  } else {
    return String.fromCharCode(65 + number);
  }
}

export abstract class QuestionFactory {
  static getFactory(type: string): QuestionFactory {
    switch (type) {
      case QuestionTypes.MultipleChoice:
        return new MultipleChoiceQuestionFactory();
      default:
        throw new Error('Unknown question type.');
    }
  }

  abstract createQuestionDetails(question: any): QuestionDetails;
  abstract createAnswerDetails(question: any): AnswerDetails;
  abstract createStatementQuestionDetails(
    question: any
  ): StatementQuestionDetails;
  abstract createStatementAnswerDetails(details: any): StatementAnswerDetails;
  abstract createStatementCorrectAnswerDetails(
    details: any
  ): StatementCorrectAnswerDetails;
}

class MultipleChoiceQuestionFactory extends QuestionFactory {
  createQuestionDetails(details: any): QuestionDetails {
    return new MultipleChoiceQuestionDetails(details);
  }
  createAnswerDetails(details: any): AnswerDetails {
    return new MultipleChoiceAnswerDetails(details);
  }
  createStatementQuestionDetails(details: any): StatementQuestionDetails {
    return new MultipleChoiceStatementQuestionDetails(details);
  }
  createStatementAnswerDetails(details: any): StatementAnswerDetails {
    return new MultipleChoiceStatementAnswerDetails(details);
  }
  createStatementCorrectAnswerDetails(
    details: any
  ): StatementCorrectAnswerDetails {
    return new MultipleChoiceStatementCorrectAnswerDetails(details);
  }
}
