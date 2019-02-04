INSERT INTO Quizes (Title, QuizDate)
  VALUES ('Quiz genérica', '2001-01-13');

INSERT INTO QuizHasQuestions (QuizID, QuestionID)
  VALUES (1, 3);


-- INSERT INTO Questions (ID, Content, AnswerTime)
--   VALUES (1, 'Isto é uma pergunta parva', 106);

INSERT INTO Questions (Content, AnswerTime)
  VALUES ('Isto é outra pergunta parva', 10);

-- INSERT INTO Answers (QuestionID, ID, Content)
--   VALUES (1, 1, 'Isto é uma resposta estúpida');

INSERT INTO Answers (QuestionID, Content)
  VALUES (1, 'Isto é uma resposta estúpida');

-- INSERT INTO Topics (ID, Name)
--   VALUES (1, 'A Cor do Ceú');

INSERT INTO Topics (Name)
  VALUES ('Outro Topico');

INSERT INTO QuestionHasTopics (QuestionID, TopicID)
  VALUES (1, 1);

INSERT INTO QuestionHasCorrectAnswers (QuestionID, AnswerID)
  VALUES (1, 1);

INSERT INTO QuestionHasImage (QuestionID, Url)
  VALUES (1, '/images/placeholder.svg');

INSERT INTO QuestionHasImage (QuestionID, Url)
  VALUES (2, '/images/placeholder.png');
