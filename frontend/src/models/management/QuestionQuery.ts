export default class QuestionQuery {
  content: string = '';
  topics: number[] = [];
  status: string[] = [];
  clarificationsOnly: Boolean = false;
  noAnswersOnly: Boolean = false;
  difficulty: number[] = [0, 100];
  beginCreationDate: string | null = null;
  endCreationDate: string | null = null;
}
