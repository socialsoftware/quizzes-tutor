import Option from "@/models/student/Option";

export interface RootState {
  version: string;
}

export interface ProfileState {
  user?: string;
  error: boolean;
  status: string;
  token: string;
}

export interface TokenAndUser {
  token: string;
  user: string;
}

export interface ServerAnswer {
  questionId: number;
  correctOption: number;
}
