import Course from '@/models/user/Course';

export const emptyCourse = new Course({
  academicTerm: undefined,
  acronym: undefined,
  courseExecutionId: undefined,
  courseExecutionType: 'EXTERNAL',
  courseId: undefined,
  courseType: 'EXTERNAL',
  name: undefined,
  endDate: undefined,
  numberOfQuestions: 0,
  numberOfQuizzes: 0,
  numberOfActiveStudents: 0,
  numberOfInactiveStudents: 0,
  numberOfActiveTeachers: 0,
  numberOfInactiveTeachers: 0,
  status: undefined,
  courseExecutionUsers: [],
});

export const filledCourse = new Course({
  academicTerm: 'Spring Semester 20/21',
  acronym: 'ES2021',
  courseExecutionId: 23,
  courseExecutionType: 'EXTERNAL',
  courseId: 1,
  courseType: 'EXTERNAL',
  name: 'Software Engineering',
  endDate: '2021-04-08',
  numberOfQuestions: 23,
  numberOfQuizzes: 12,
  numberOfActiveStudents: 56,
  numberOfInactiveStudents: 0,
  numberOfActiveTeachers: 1,
  numberOfInactiveTeachers: 0,
  status: 'ACTIVE',
  courseExecutionUsers: [],
});
