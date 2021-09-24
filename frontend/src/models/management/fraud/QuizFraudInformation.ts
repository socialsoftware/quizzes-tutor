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
      this.users[timeScore.userInfo.username] = {
        ...this.users[timeScore.userInfo.username],
        userInfo: timeScore.userInfo,
        scoreTime: timeScore.score,
      };
    }
    for (const producerScore of communicationProducerScores.fraudScores) {
      this.users[producerScore.userInfo.username] = {
        ...this.users[producerScore.userInfo.username],
        userInfo: producerScore.userInfo,
        scoreCommunicationProducer: producerScore.score,
      };
    }

    for (const consumerScore of communicationConsumerScores.fraudScores) {
      this.users[consumerScore.userInfo.username] = {
        ...this.users[consumerScore.userInfo.username],
        userInfo: consumerScore.userInfo,
        scoreCommunicationConsumer: consumerScore.score,
      };
    }
  }
}
