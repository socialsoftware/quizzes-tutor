DROP TABLE IF EXISTS Answers CASCADE;
DROP TABLE IF EXISTS Options CASCADE;
DROP TABLE IF EXISTS Images CASCADE;
DROP TABLE IF EXISTS QuestionHasTopic CASCADE;
DROP TABLE IF EXISTS Topics CASCADE;
DROP TABLE IF EXISTS QuizHasQuestion CASCADE;
DROP TABLE IF EXISTS Questions CASCADE;
DROP TABLE IF EXISTS Quizzes CASCADE;
DROP TABLE IF EXISTS Students CASCADE;

CREATE TABLE Quizzes (
    ID serial NOT Null,
    Title VARCHAR(255),
    QuizDate TIMESTAMP,
    PRIMARY KEY (ID)
);

CREATE TABLE Questions (
    ID serial NOT NULL,
    NewID INT REFERENCES Questions on Delete cascade,
    Content Text,
    Difficulty INT,
    PRIMARY KEY (ID)
);

CREATE TABLE QuizHasQuestion (
    QuizID INT NOT NULL REFERENCES Quizzes on Delete cascade,
    QuestionID INT NOT NULL REFERENCES Questions on Delete cascade,
    Primary Key (QuizID, QuestionID)
);

CREATE TABLE Options (
  QuestionID INT NOT NULL REFERENCES Questions ON DELETE CASCADE,
  Option INT NOT NULL,
  Content Text,
  IsCorrect Boolean DEFAULT FALSE,
  PRIMARY KEY (QuestionID, Option)
);

CREATE TABLE Topics (
  ID serial NOT NULL,
  Name VARCHAR(255) UNIQUE,
  PRIMARY KEY (ID)
);

CREATE TABLE QuestionHasTopic (
  TopicID INT NOT NULL REFERENCES Topics ON DELETE CASCADE,
  QuestionID INT NOT NULL REFERENCES Questions ON DELETE CASCADE,
  Votes INT,
  Score INT,
  PRIMARY KEY (TopicID, QuestionID)
);

CREATE TABLE Images (
  QuestionID INT NOT NULL REFERENCES Questions ON DELETE CASCADE,
  Url VARCHAR(255),
  Width INT,
  PRIMARY KEY (QuestionID)
);

CREATE TABLE Students (
  ID serial NOT NULL,
  PRIMARY KEY (ID)
);


CREATE TABLE Answers (
  StudentID INT NOT NULL REFERENCES Students ON DELETE CASCADE,
  QuestionID INT NOT NULL REFERENCES Questions ON DELETE CASCADE,
  AnswerDate TIMESTAMP,
  QuizID INT REFERENCES Quizzes ON DELETE CASCADE,
  TimeTaken INT,
  Option INT NOT NULL,
  PRIMARY KEY (StudentID, QuestionID, AnswerDate)
);
