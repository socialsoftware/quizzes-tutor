import QuizFraudScores from '@/models/management/fraud/QuizFraudScores';
import UserFraudScore from '@/models/management/fraud/UserFraudScore';

export class QuizFraudInformation {
  users: { [username: string]: UserFraudScore } = {};
  constructor(
    timeScores: QuizFraudScores,
    communicationProducerScores: QuizFraudScores,
    communicationConsumerScores: QuizFraudScores
  ) {
    for (let timeScore of timeScores.fraudScores) {
      this.users[timeScore.username] = {
        ...this.users[timeScore.username],
        username: timeScore.username,
        scoreTime: timeScore.score,
      };
    }
    for (let producerScore of communicationProducerScores.fraudScores) {
      this.users[producerScore.username] = {
        ...this.users[producerScore.username],
        username: producerScore.username,
        scoreCommunicationProducer: producerScore.score,
      };
    }

    for (let consumerScore of communicationConsumerScores.fraudScores) {
      this.users[consumerScore.username] = {
        ...this.users[consumerScore.username],
        username: consumerScore.username,
        scoreCommunicationConsumer: consumerScore.score,
      };
    }
  }
}
