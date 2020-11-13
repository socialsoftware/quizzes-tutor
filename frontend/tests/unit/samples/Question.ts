import Question from '@/models/management/Question';
import { QuestionTypes } from '@/services/QuestionHelpers';
import MultipleChoiceQuestionDetails from '@/models/management/questions/MultipleChoiceQuestionDetails';

export const questionWithFigure = new Question({
  id: 395,
  title: 'GraphiteDecompositionMemcached',
  content:
    'Consider the following decomposition view of the Graphite system where module Store Graphs is responsible for managing the storage of datapoints and graphs and module Present Graphs for graphs generation and presentation. Memcache is a library that maintains datapoints in memory to reduce the overhead of obtaining them from the file system.   \n![image][image]  \n',
  difficulty: 54,
  numberOfGeneratedQuizzes: 2,
  numberOfNonGeneratedQuizzes: 2,
  numberOfAnswers: 94,
  numberOfCorrect: 51,
  creationDate: null,
  status: 'AVAILABLE',
  questionDetailsDto: {
    type: QuestionTypes.MultipleChoice,
    options: [
      {
        id: 1578,
        sequence: 1,
        correct: false,
        content:
          'Memcached can be considered a sub-module of the Present Graphs module.'
      },
      {
        id: 1579,
        sequence: 2,
        correct: false,
        content:
          'Memcached can be considered a direct sub-module of the top Graphite module.'
      },
      {
        id: 1580,
        sequence: 3,
        correct: false,
        content: 'Memcached is not a module.'
      },
      {
        id: 1577,
        sequence: 0,
        correct: true,
        content:
          'Memcached can be considered a sub-module of the Store Graphs module.'
      }
    ],
    setAsNew: MultipleChoiceQuestionDetails.prototype.setAsNew
  } as MultipleChoiceQuestionDetails,
  image: {
    url: '395.png',
    width: 100
  },
  topics: [
    {
      id: 48,
      name: 'Graphite',
      numberOfQuestions: 27
    },
    {
      id: 4,
      name: 'Module viewtype',
      numberOfQuestions: 94
    }
  ],
  sequence: null
});

export const questionWithoutFigure = new Question({
  id: 395,
  title: 'GraphiteDecompositionMemcached',
  content:
    'Consider the following decomposition view of the Graphite system where module Store Graphs is responsible for managing the storage of datapoints and graphs and module Present Graphs for graphs generation and presentation. Memcache is a library that maintains datapoints in memory to reduce the overhead of obtaining them from the file system.',
  difficulty: 54,
  numberOfGeneratedQuizzes: 2,
  numberOfNonGeneratedQuizzes: 2,
  numberOfAnswers: 94,
  numberOfCorrect: 51,
  creationDate: null,
  status: 'AVAILABLE',
  questionDetailsDto: {
    type: QuestionTypes.MultipleChoice,
    options: [
      {
        id: 1578,
        sequence: 1,
        correct: false,
        content:
          'Memcached can be considered a sub-module of the Present Graphs module.'
      },
      {
        id: 1579,
        sequence: 2,
        correct: false,
        content:
          'Memcached can be considered a direct sub-module of the top Graphite module.'
      },
      {
        id: 1580,
        sequence: 3,
        correct: false,
        content: 'Memcached is not a module.'
      },
      {
        id: 1577,
        sequence: 0,
        correct: true,
        content:
          'Memcached can be considered a sub-module of the Store Graphs module.'
      }
    ],
    setAsNew: MultipleChoiceQuestionDetails.prototype.setAsNew
  } as MultipleChoiceQuestionDetails,
  image: null,
  topics: [
    {
      id: 48,
      name: 'Graphite',
      numberOfQuestions: 27
    },
    {
      id: 4,
      name: 'Module viewtype',
      numberOfQuestions: 94
    }
  ],
  sequence: null
});
