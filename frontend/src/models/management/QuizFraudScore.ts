export default class QuizFraudScore{
    userId!: string;
    score!: number;

    constructor(jsonObj?:QuizFraudScore){
        if(jsonObj){
            this.userId = jsonObj.userId;
            this.score = jsonObj.score;
        }
    }
}