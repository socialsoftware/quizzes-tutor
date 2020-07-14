import User from '@/models/user/User';
import Topic from '@/models/management/Topic';
import { ISOtoString } from '@/services/ConvertDateService';
import TopicConjunction from '@/models/management/TopicConjunction';

export default class Tournament {
  id!: number;
  startTime!: string;
  endTime!: string;
  numberOfQuestions!: number;
  state!: string;
  courseAcronym!: string;
  enrolled!: boolean;
  topicConjunction!: TopicConjunction;
  topics!: String[];
  participants!: User[];
  quizId!: number;
  privateTournament!: boolean;
  password!: string;

  constructor(jsonObj?: Tournament, user?: User) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.startTime = ISOtoString(jsonObj.startTime);
      this.endTime = ISOtoString(jsonObj.endTime);
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.state = jsonObj.state;
      this.courseAcronym = jsonObj.courseAcronym;
      this.topics = [];

      this.topicConjunction = jsonObj.topicConjunction;
      if (this.topicConjunction)
        this.topicConjunction.topics.forEach((topic: Topic) => {
          if (!this.topics!.includes(topic.name)) {
            this.topics!.push(topic.name);
          }
        });

      this.participants = jsonObj.participants;
      this.enrolled = false;
      this.quizId = jsonObj.quizId;
      this.privateTournament = jsonObj.privateTournament;
      this.password = jsonObj.password;
      if (user) {
        this.participants.forEach(pUser => {
          if (pUser.id == user.id) {
            this.enrolled = true;
            return;
          }
        });
        for (let i = 0; i < this.participants.length; i++) {
          if (user.id == this.participants[i].id) {
            this.enrolled = true;
            return;
          }
        }
      }
    }
  }
}
