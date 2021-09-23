
export default class UserFraudScore {
    username!: string;
    scoreTime!: number;
    scoreCommunicationProducer!: number;
    scoreCommunicationConsumer!: number;

    constructor(jsonObj?:UserFraudScore){
        if (jsonObj){
            this.username = jsonObj.username
            this.scoreTime = jsonObj.scoreTime
            this.scoreCommunicationProducer = jsonObj.scoreCommunicationProducer
            this.scoreCommunicationConsumer = jsonObj.scoreCommunicationConsumer
        } 

    }
}