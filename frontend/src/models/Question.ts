import axios from "axios";
import Option from "@/models/Option";

interface ServerResponse {
  data: ServerQuestion;
}

interface ServerQuestion {
  id: number | null;
  content: string | null;
  options: Option[] | null;
  version: number | null;
  topic: string | null;
  difficulty: number | null;
}

interface Image {
  url: string | null;
  width: number | null;
}

export default class Question implements ServerQuestion {
  id: number | null;
  content: string | null;
  options: Option[] | null;
  version: number | null;
  topic: string | null;
  difficulty: number | null;
  image: Image | null;

  constructor(jsonObj: any) {
    this.id = jsonObj.id;
    this.content = jsonObj.content;
    this.options = jsonObj.options;
    this.version = jsonObj.version;
    this.topic = jsonObj.topic;
    this.difficulty = jsonObj.difficulty;
    this.image = jsonObj.image;
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
        console.log(
          this.content.replace(
            /\\begin\{center\}[\s\S]*\\end\{center\}/gi,
            "  \n ![image](http://localhost:8080/images/questions/" +
              this.image.url +
              ")"
          )
        );
        console.log(" ");
        return {
          questions: [
            {
              type: "radiogroup",
              title: this.content.replace(
                /\\begin\{center\}[\s\S]*\\end\{center\}/gi,
                "  \n ![image](http://localhost:8080/images/questions/" +
                  this.image.url +
                  ")"
              ),
              choicesOrder: "random",
              choices: this.options.map(option => option.content)
            }
          ]
        };
      } else {
        return {
          questions: [
            {
              type: "radiogroup",
              title: this.content,
              choicesOrder: "random",
              choices: this.options.map(option => option.content)
            }
          ]
        };
      }
    }
    return "";
  }
}
