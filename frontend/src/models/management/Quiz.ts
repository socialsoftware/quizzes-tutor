import { Question } from "@/models/management/Question";

export class Quiz {
  id!: number;
  number!: number;
  scramble!: boolean;
  title!: string;
  date!: Date;
  creationDate!: Date;
  availableDate!: Date;
  conclusionDate!: Date;
  type!: string;
  year!: number;
  series!: number;
  version!: string;
  numberOfQuestions!: number;
  numberOfAnswers!: number;
  questions!: Question[] | null;
}
