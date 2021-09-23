import { FraudScores } from '@/models/management/fraud/FraudScores';
import { UserFraudScore } from '@/models/management/fraud/UserFraudScore';

export class QuizFraudInformation {
  users: { [username: string]: UserFraudScore } = {};
  constructor(
    timeScores: FraudScores,
    communicationProducerScores: FraudScores,
    communicationConsumerScores: FraudScores
  ) {
    for (const timeScore of timeScores.fraudScores) {
      this.users[timeScore.username] = {
        ...this.users[timeScore.username],
        username: timeScore.username,
        scoreTime: timeScore.score,
      };
    }
    for (const producerScore of communicationProducerScores.fraudScores) {
      this.users[producerScore.username] = {
        ...this.users[producerScore.username],
        username: producerScore.username,
        scoreCommunicationProducer: producerScore.score,
      };
    }

    for (const consumerScore of communicationConsumerScores.fraudScores) {
      this.users[consumerScore.username] = {
        ...this.users[consumerScore.username],
        username: consumerScore.username,
        scoreCommunicationConsumer: consumerScore.score,
      };
    }
  }
}
