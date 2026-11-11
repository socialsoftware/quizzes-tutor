// What each course asked of question generation in the last days (counts per day, oldest first)

export interface GenerationUsageRow {
  courseExecutionId: number;
  course: string;
  academicTerm: string;
  requests: number[];
  questions: number[];
  totalRequests: number;
  totalQuestions: number;
}

export interface GenerationUsage {
  // ISO dates, oldest first
  days: string[];
  courses: GenerationUsageRow[];
}
