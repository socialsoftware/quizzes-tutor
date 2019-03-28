import axios from "axios";


interface ServerResponse {
    data: ServerQuestion
}

interface ServerQuestion {
    id: number|null;
    content: string|null;
    answers: string[]|null;
    correctAnswer: number|null;
    version: number|null;
    topic: string|null;
    difficulty: number|null;
}

export default class Question implements ServerQuestion{
    id: number|null;
    content: string|null;
    answers: string[]|null;
    correctAnswer: number|null;
    version: number|null;
    topic: string|null;
    difficulty: number|null;

    constructor(jsonObj: any) {

        this.id = jsonObj.id;
        this.content = jsonObj.content;
        this.answers = jsonObj.answers;
        this.correctAnswer = jsonObj.correctAnswer;
        this.version = jsonObj.version;
        this.topic = jsonObj.topic;
        this.difficulty = jsonObj.difficulty;
    }

    get() {
        axios.request<ServerQuestion>({
            url: 'localhost:8080/question',
            transformResponse: (r: ServerResponse) => r.data
        }).then((response) => {
            // `response` is of type `AxiosResponse<ServerData>`
            const { data } = response
            // `data` is of type ServerData, correctly inferred
        }).catch(function(error){
            // Handle error
        });
    }

    customjson(): any{
        let a = {
            questions: [
                {
                    type: "radiogroup",
                    title: this.content,
                    choicesOrder: "random",
                    choices: this.answers
                }
            ]
        }
        return a;
    }

}