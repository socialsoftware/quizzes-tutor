import Question from '@/models/management/Question';

export default class QuestionGeneration {
  id!: number;
  courseExecutionId!: number;
  jobId: number | null = null;
  question!: Question;
  status!: string;
  modelId: string | null = null;
  promptVersion: string | null = null;
  groundingMode: string | null = null;
  verificationRetries: number = 0;
  needsHumanAttention: boolean = false;
  explanation: string | null = null;
  sourceChunkIds: string[] = [];
  // Job rewriting the question while REGENERATING ("Regenerate with review")
  regenerationJobId: number | null = null;
  // Lets the Submissions table list generated questions next to the students' ones
  readonly isGenerated = true;
  readonly teacherRead = true;

  constructor(jsonObj?: QuestionGeneration) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.courseExecutionId = jsonObj.courseExecutionId;
      this.jobId = jsonObj.jobId;
      this.question = new Question(jsonObj.question);
      this.status = jsonObj.status;
      this.modelId = jsonObj.modelId;
      this.promptVersion = jsonObj.promptVersion;
      this.groundingMode = jsonObj.groundingMode;
      this.verificationRetries = jsonObj.verificationRetries ?? 0;
      this.needsHumanAttention = jsonObj.needsHumanAttention;
      this.explanation = jsonObj.explanation;
      this.sourceChunkIds = jsonObj.sourceChunkIds ?? [];
      this.regenerationJobId = jsonObj.regenerationJobId ?? null;
    }
  }

  static headers = [
    { title: 'Number', key: 'id', align: 'center', width: '90px' },
    { title: 'Question', key: 'question.title', align: 'start' },
    { title: 'Status', key: 'status', align: 'center', width: '150px' },
    { title: 'Checks', key: 'needsHumanAttention', align: 'center', width: '140px' },
    { title: 'Mode', key: 'groundingMode', align: 'center', width: '110px' },
    { title: 'Model', key: 'modelId', align: 'center', width: '200px' },
  ];

  // Shown in the "Submitted by" column
  get name() {
    return `AI (${this.modelId ?? 'unknown model'})`;
  }

  isRegenerating() {
    return this.status === 'REGENERATING';
  }

  // Only students edit their own questions; a teacher regenerates these instead
  isInRevision() {
    return false;
  }

  isRejected() {
    return this.status === 'REJECTED';
  }

  getStatus() {
    return this.getStatusName();
  }

  isOpen() {
    return this.status === 'IN_REVIEW' || this.status === 'IN_REVISION';
  }

  getStatusName() {
    return this.status.replace(/_/g, ' ');
  }

  getStatusColor() {
    switch (this.status) {
      case 'APPROVED':
        return 'green';
      case 'REJECTED':
        return 'red';
      case 'IN_REVISION':
        return 'yellow';
      case 'REGENERATING':
        return 'purple';
      default:
        return 'blue';
    }
  }
}
