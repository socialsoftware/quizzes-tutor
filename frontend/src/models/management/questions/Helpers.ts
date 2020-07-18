import QuestionDetails from '@/models/management/questions/QuestionDetails';
import MultipleChoiceQuestionDetails from '@/models/management/questions/MultipleChoiceQuestionDetails';
import MultipleChoiceAnswerDetails from '@/models/management/questions/MultipleChoiceAnswerDetails';
import AnswerDetails from '@/models/management/questions/AnswerDetails';
import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import MultipleChoiceStatementQuestionDetails from '@/models/statement/questions/MultipleChoiceStatementQuestionDetails';
import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import MultipleChoiceStatementCorrectAnswerDetails from '@/models/statement/questions/MultipleChoiceStatementCorrectAnswerDetails';
import MultipleChoiceStatementAnswerDetails from '@/models/statement/questions/MultipleChoiceStatementAnswerDetails';

// TODO: MOVE TO SERVICES
// TODO: CREATE A BETTER FACTORY, USE POLYMORPHISM

export const enum QuestionTypes {
  MultipleChoice = 'multiple_choice'
}

export function createQuestionDetails(question: any): QuestionDetails {
  switch (question.type) {
    case QuestionTypes.MultipleChoice:
      return new MultipleChoiceQuestionDetails(question);
    default:
      throw new Error('Unknown question type.');
  }
}

export function createAnswerDetails(question: any): AnswerDetails {
  switch (question.type) {
    case QuestionTypes.MultipleChoice:
      return new MultipleChoiceAnswerDetails(question);
    default:
      throw new Error('Unknown question type.');
  }
}

export function createStatementQuestionDetails(
  question: any
): StatementQuestionDetails {
  switch (question.type) {
    case QuestionTypes.MultipleChoice:
      return new MultipleChoiceStatementQuestionDetails(question);
    default:
      throw new Error('Unknown question type.');
  }
}

export function createStatementAnswerDetails(
  question: any
): StatementAnswerDetails {
  switch (question.type) {
    case QuestionTypes.MultipleChoice:
      return new MultipleChoiceStatementAnswerDetails(question);
    default:
      throw new Error('Unknown question type.');
  }
}

export function createStatementCorrectAnswerDetails(
  details: any
): StatementAnswerDetails {
  switch (details.type) {
    case QuestionTypes.MultipleChoice:
      return new MultipleChoiceStatementCorrectAnswerDetails(details);
    default:
      throw new Error('Unknown question type.');
  }
}
