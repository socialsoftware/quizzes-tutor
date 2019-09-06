import { Question } from "@/models/management/Question";

export class Quiz {
  id!: number;
  number!: number;
  title!: string;
  date!: Date;
  availableDate!: Date;
  type!: string;
  year!: number;
  series!: number;
  version!: string;
  numberOfQuestions!: number;
  numberOfAnswers!: number;
  questions!: Question[] | null;
}
