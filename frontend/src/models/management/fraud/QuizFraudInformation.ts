import QuizFraudScores from '@/models/management/fraud/QuizFraudScores';
import UserFraudScore from '@/models/management/fraud/UserFraudScore';

export class QuizFraudInformation {
  users: { [username: string]: UserFraudScore } = {};
  constructor(
    fraudTimeScores: QuizFraudScores,
    fraudCommunicationProducerScores: QuizFraudScores,
    fraudCommunicationConsumerScores: QuizFraudScores
  ) {
    for (let fraudTimeScore of fraudTimeScores.fraudScores) {
      this.users[fraudTimeScore.username] = {
        ...this.users[fraudTimeScore.username],
        username: fraudTimeScore.username,
        scoreTime: fraudTimeScore.score,
      };
    }
    for (let fraudProducerScore of fraudCommunicationProducerScores.fraudScores) {
      this.users[fraudProducerScore.username] = {
        ...this.users[fraudProducerScore.username],
        username: fraudProducerScore.username,
        scoreCommunicationProducer: fraudProducerScore.score,
      };
    }

    for (let fraudConsumerScore of fraudCommunicationConsumerScores.fraudScores) {
      this.users[fraudConsumerScore.username] = {
        ...this.users[fraudConsumerScore.username],
        username: fraudConsumerScore.username,
        scoreCommunicationConsumer: fraudConsumerScore.score,
      };
    }
  }
}
