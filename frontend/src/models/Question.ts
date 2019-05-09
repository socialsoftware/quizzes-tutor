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
  correct_option: number | null;
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
  correct_option!: number | null;

  constructor(jsonObj: ServerQuestion) {
    this.id = jsonObj.id;
    this.content = jsonObj.content;
    this.options = jsonObj.options;
    this.topic = jsonObj.topic;
    this.image = jsonObj.image;
    this.correct_option = jsonObj.correct_option;
  }

  get() {
    axios
      .request<ServerQuestion>({
        url: "localhost:8080/question",
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

  customjson(): any {
    if (this.content && this.options) {
      if (this.image && this.image.width) {
        this.content.replace(
          /\\begin\{center\}[\s\S]*\\end\{center\}/gi,
          "  \n ![image](http://localhost:8080/images/questions/" +
            this.image.url +
            ")"
        );
        return {
          questions: [
            {
              type: "radiogroup",
              name: this.id.toString(),
              title: this.content.replace(
                /\\begin\{center\}[\s\S]*\\end\{center\}/gi,
                "  \n ![image](http://localhost:8080/images/questions/" +
                  this.image.url +
                  ")"
              ),
              choices: this.options.map(option => {
                return {
                  value: option.option,
                  text: option.content
                };
              })
            }
          ]
        };
      } else {
        return {
          questions: [
            {
              type: "radiogroup",
              name: this.id.toString(),
              title: this.content,
              choices: this.options.map(option => {
                return {
                  value: option.option,
                  text: option.content
                };
              })
            }
          ]
        };
      }
    }
    return "";
  }
}
