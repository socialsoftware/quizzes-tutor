import axios from "axios";
import Option from "@/models/Option";

interface ServerResponse {
  data: ServerQuestion;
}

interface ServerQuestion {
  id: number;
  content: string | null;
  options: Option[] | null;
  topic: string | null;
  correctOption: number | null;
  image: Image | null;
}

interface Image {
  url: string | null;
  width: number | null;
}

export default class Question implements ServerQuestion {
  id!: number;
  content!: string | null;
  options!: Option[] | null;
  topic!: string | null;
  image: Image | null;
  correctOption!: number | null;

  constructor(jsonObj: ServerQuestion) {
    this.id = jsonObj.id;
    this.content = jsonObj.content;
    this.options = jsonObj.options;
    this.topic = jsonObj.topic;
    this.image = jsonObj.image;
    this.correctOption = jsonObj.correctOption;
  }

  get() {
    axios
      .request<ServerQuestion>({
        url: process.env.VUE_APP_ROOT_API + "/question",
        transformResponse: (r: ServerResponse) => r.data
      })
      .then(response => {
        // `response` is of type `AxiosResponse<ServerData>`
        const { data } = response;
        // `data` is of type ServerData, correctly inferred
      })
      .catch(function(error) {
        // Handle error
      });
  }
}
