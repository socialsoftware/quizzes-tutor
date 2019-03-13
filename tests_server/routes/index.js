var express = require('express');
var router = express.Router();

var db = require('../queries/database');

var questions = require('../queries/questions')(db.db);
var answers = require('../queries/answers')(db.db);
var topics = require('../queries/topics')(db.db);
var correctAnswers = require('../queries/correctAnswers')(db.db);
var questionTopics = require('../queries/questionTopics')(db.db);

// edit question
var getEditQuestion = require('../queries/getEditQuestion')(db.db);
var editQuestion = require('../queries/editQuestion')(db.db);
var editQuizQuestion = require('../queries/editQuizQuestion')(db.db);

// update questions
var updateQuestion = require('../queries/updateQuestion')(db.db);

//quizes
var quizes = require('../queries/quizes')(db.db);
var editQuiz = require('../queries/editQuiz')(db.db);

var listQuizes = require('../queries/listQuizes')(db.db);

var listQuestions = require('../queries/listQuestions')(db.db);

var exportArsNova = require('../queries/exportArsNova')(db.db);

var start = require('../queries/start')(db.db);

var updateQuiz = require('../queries/updateQuiz')(db.db);

var createQuestion = require('../queries/createQuestion')(db.db);

var createNewQuestion = require('../queries/createNewQuestion')(db.db);

var createQuiz = require('../queries/createQuiz')(db.db);

var createNewQuiz = require('../queries/createNewQuiz')(db.db);

var createQuizQuestion = require('../queries/createQuizQuestion')(db.db);

var createNewQuizQuestion = require('../queries/createNewQuizQuestion')(db.db);

var addQuizQuestion = require('../queries/addQuizQuestion')(db.db);

var copyQuestionIntoQuiz = require('../queries/copyQuestionIntoQuiz')(db.db);

/* GET login */
router.get('/login', function(req, res, next) {
  res.render('login');
});

/* GET home page. */
// router.get('/', function(req, res, next) {
//   res.render('listQuiz', { title: 'Express' });
// });


// Questions API endpoints
router.get('/api/questions', questions.getAll);
router.get('/api/questions/:id', questions.get);
router.post('/api/questions', questions.create);
router.put('/api/questions/:id', questions.update);
router.delete('/api/questions/:id', questions.remove);
router.get('/api/previousQuestion/:id', questions.previous);
router.get('/api/nextQuestion/:id', questions.next);



// Answers API endpoints
router.get('/api/answers', answers.getAll);
router.get('/api/answers/:id', answers.get);
router.post('/api/answers', answers.create);
router.put('/api/answers/:id', answers.update);
router.delete('/api/answers/:id', answers.remove);

// Answers for Questions API endpoints
router.get('/api/answersforquestion/:id', answers.getForQuestion);


// Topics API endpoints
router.get('/api/topics', topics.getAll);
router.get('/api/topics/:id', topics.get);
router.post('/api/topics', topics.create);
router.put('/api/topics/:id', topics.update);
router.delete('/api/topics/:id', topics.remove);

// Correct Answers API endpoints
router.get('/api/correctanswers', correctAnswers.getAll);
router.get('/api/correctanswers/:id', correctAnswers.get);
router.post('/api/correctanswers', correctAnswers.create);
router.put('/api/correctanswers/:id', correctAnswers.update);
router.delete('/api/correctanswers/:id', correctAnswers.remove);


// Correct Answers for Questions API endpoints
router.get('/api/correctanswersforquestion/:id', correctAnswers.getForQuestion);


// Question Topics API endpoints
router.get('/api/questiontopics', questionTopics.getAll);
router.get('/api/questiontopics/:id', questionTopics.get);
router.post('/api/questiontopics', questionTopics.create);
router.put('/api/questiontopics/:id', questionTopics.update);
router.delete('/api/questiontopics/:id', questionTopics.remove);

// Topics for Questions API endpoints
router.get('/api/topicsforquestion/:id', questionTopics.getForQuestion);

// Edit question
router.get('/api/geteditquestion/:id', getEditQuestion.get);
router.get('/api/getquestiondata', getEditQuestion.getAll);

router.get('/editquestion/:id', editQuestion.get);

router.get('/editquizquestion/:quizid/:questionid', editQuizQuestion.get);

// Update question
router.put('/api/updatequestion/:id', updateQuestion.update);

// List quizes
router.get('/api/quizes', quizes.getAll);
router.delete('/api/quizes/:id', quizes.remove);
router.get('/api/previousQuiz/:id', quizes.previous);
router.get('/api/nextQuiz/:id', quizes.next);

// quizes view
router.get('/listQuizes', listQuizes.get);

// questions view
router.get('/listQuestions', listQuestions.get);

// edit Quiz
router.get('/editquiz/:id', editQuiz.get);

// export ArsNova
router.get('/api/exportArsNova/:id', exportArsNova.get);

// start view
router.get('/start', start.get);

// update quiz endpoint
router.put('/api/updatequiz/:id', updateQuiz.update);

// create question view
router.get('/createQuestion', createQuestion.get);

// create question endpoint
router.post('/api/createNewQuestion', createNewQuestion.post);

// create quiz view
router.get('/createQuiz', createQuiz.get);

// create quiz
router.post('/api/createNewQuiz', createNewQuiz.post);

// create question in the context of a quiz
router.get('/createquizquestion/:quizid', createQuizQuestion.get);

// create question endpoint
router.post('/api/createNewQuizQuestion/:quizid', createNewQuizQuestion.post);

// add question in the context of a quiz
router.get('/addquizquestion/:id', addQuizQuestion.get);

// add question in the context of a quiz
router.post('/api/copyQuestionIntoQuiz', copyQuestionIntoQuiz.post);

module.exports = router;
