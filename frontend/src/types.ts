import Option from "@/models/Option";

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

export interface ServerQuestion {
  id: number;
  content: string | null;
  options: Option[] | null;
  //topic: string | null;
  image: Image | null;
}

export interface Image {
  url: string;
  width: number | null;
}
