select questions.id, questions.content as question,
  answers.id as answerid,
   answers.content as answer
from questions, answers
where questions.id = answers.questionid;
