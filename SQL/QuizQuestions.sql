-- questions not in quiz
select * from questions
  where id not in (select questionid from quizhasquestions where quizid = 1);

-- answers not in quiz
select * from answers
  where questionid not in (select questionid from quizhasquestions where quizid = 1);

-- correct answers for questions not in quiz
select * from QuestionHasCorrectAnswers
  where questionid not in (select questionid from quizhasquestions where quizid = 1);


-- questions in quiz
select * from questions
  where id in (select questionid from QuizHasQuestions where quizid = 1);

-- answers in quiz
select * from answers
  where questionid in (select questionid from quizhasquestions where quizid = 1);

-- correct answers for questions in quiz
select * from QuestionHasCorrectAnswers
  where questionid in (select questionid from quizhasquestions where quizid = 1);
