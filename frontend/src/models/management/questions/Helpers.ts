import QuestionType from '@/models/management/questions/QuestionType';
import MultipleChoiceQuestionType from '@/models/management/questions/MultipleChoiceQuestionType';
import MultipleChoiceAnswerType from '@/models/management/questions/MultipleChoiceAnswerType';
import AnswerType from '@/models/management/questions/AnswerType';

export const enum QuestionTypes {
  MultipleChoice = 'multiple_choice'
}

export function createQuestionType(question: any): QuestionType {
  switch (question.type) {
    case QuestionTypes.MultipleChoice:
      return new MultipleChoiceQuestionType(question);
    default:
      throw new Error('Unknown question type.');
  }
}

export function createAnswerType(question: any): AnswerType {
  switch (question.type) {
    case QuestionTypes.MultipleChoice:
      return new MultipleChoiceAnswerType(question);
    default:
      throw new Error('Unknown question type.');
  }
}
