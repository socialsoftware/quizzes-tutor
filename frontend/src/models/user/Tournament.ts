import User from '@/models/user/User';
import Topic from '@/models/management/Topic';
import { ISOtoString } from '@/services/ConvertDateService';
import TournamentParticipant from '@/models/user/TournamentParticipant';

export default class Tournament {
  id!: number;
  courseAcronym!: string;
  quizId!: number;
  startTime!: string;
  endTime!: string;
  numberOfQuestions!: number;
  canceled!: boolean;
  enrolled!: boolean;
  topics!: String[];
  topicsDto!: Topic[];
  creator!: User;
  participants!: TournamentParticipant[];
  privateTournament!: boolean;
  password!: string;
  isClosed!: boolean;

  constructor(jsonObj?: Tournament, user?: User) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.courseAcronym = jsonObj.courseAcronym;
      this.quizId = jsonObj.quizId;
      this.startTime = ISOtoString(jsonObj.startTime);
      this.endTime = ISOtoString(jsonObj.endTime);
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.canceled = jsonObj.canceled;
      this.topics = [];

      if (jsonObj.topicsDto) {
        jsonObj.topicsDto.forEach((topic: Topic) => {
          if (!this.topics!.includes(topic.name)) {
            this.topics!.push(topic.name);
          }
        });
      }

      this.creator = jsonObj.creator;
      this.participants = jsonObj.participants;
      this.privateTournament = jsonObj.privateTournament;
      this.password = jsonObj.password;
      this.isClosed = jsonObj.isClosed;

      if (user) {
        this.enrolled = this.participants.some(
          pUser => pUser.username === user.username
        );
      }
    }
  }

  static sortById(a: Tournament, b: Tournament) {
    if (a.id && b.id) return a.id > b.id ? 1 : -1;
    else return 0;
  }

  getStateColor() {
    if (this.isClosed) return 'orange';
    else if (!this.canceled) return 'green';
    else return 'red';
  }

  getStateName() {
    if (this.isClosed) return 'CLOSED';
    else if (!this.canceled) return 'OPEN';
    else return 'CANCELLED';
  }

  getEnrolledColor() {
    if (this.enrolled) return 'green';
    else return 'red';
  }

  getEnrolledName() {
    if (this.enrolled) return 'YOU ARE IN';
    else return 'YOU NEED TO JOIN';
  }

  getPrivateColor() {
    if (this.privateTournament) return 'red';
    else return 'green';
  }

  getPrivateName() {
    if (this.privateTournament) return 'PRIVATE';
    else return 'PUBLIC';
  }

  isNotEnrolled() {
    return !this.enrolled;
  }

  isNotCanceled() {
    return !this.canceled;
  }

  isPrivate() {
    return this.privateTournament;
  }
}
