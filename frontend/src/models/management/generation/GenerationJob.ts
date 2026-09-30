import { ISOtoString } from '@/services/ConvertDateService';

export interface GenerationRequest {
  topicId: number | null;
  topic: string;
  difficulty: string;
  count: number;
  groundingMode: string;
  materialIds: string[];
}

export default class GenerationJob {
  id!: number;
  topic!: string;
  topicId: number | null = null;
  requestedCount!: number;
  difficulty!: string;
  groundingMode!: string;
  // REQUESTED while the service works, IMPORTED once the drafts are in the review list
  status!: string;
  generationStatus: string | null = null;
  importedCount: number = 0;
  skippedCount: number = 0;
  error: string | null = null;
  creationDate!: string;

  constructor(jsonObj?: GenerationJob) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.topic = jsonObj.topic;
      this.topicId = jsonObj.topicId;
      this.requestedCount = jsonObj.requestedCount;
      this.difficulty = jsonObj.difficulty;
      this.groundingMode = jsonObj.groundingMode;
      this.status = jsonObj.status;
      this.generationStatus = jsonObj.generationStatus;
      this.importedCount = jsonObj.importedCount ?? 0;
      this.skippedCount = jsonObj.skippedCount ?? 0;
      this.error = jsonObj.error;
      this.creationDate = ISOtoString(jsonObj.creationDate);
    }
  }

  static headers = [
    { title: 'Topic', key: 'topic', align: 'start' },
    { title: 'Difficulty', key: 'difficulty', align: 'center', width: '110px' },
    { title: 'Mode', key: 'groundingMode', align: 'center', width: '110px' },
    { title: 'Requested', key: 'requestedCount', align: 'center', width: '110px' },
    { title: 'Status', key: 'status', align: 'center', width: '200px' },
    { title: 'Created', key: 'creationDate', align: 'center', width: '150px' },
  ];

  isPending() {
    return this.status === 'REQUESTED';
  }

  getStatusName() {
    if (this.status === 'IMPORTED') return 'READY FOR REVIEW';
    if (this.status === 'FAILED') return 'FAILED';
    return this.generationStatus === 'RUNNING' ? 'GENERATING' : 'QUEUED';
  }

  getStatusColor() {
    if (this.status === 'IMPORTED') return 'green';
    if (this.status === 'FAILED') return 'red';
    return 'blue';
  }
}
