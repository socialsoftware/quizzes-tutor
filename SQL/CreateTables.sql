CREATE TABLE Quizes (
    ID serial not Null,
    Title VARCHAR(255),
    QuizDate DATE,
    PRIMARY KEY (ID)
);

CREATE TABLE Questions (
    ID serial NOT NULL,
    Content Text,
    AnswerTime integer,
    PRIMARY KEY (ID)
);

CREATE TABLE QuizHasQuestions (
    QuizID int not null REFERENCES Quizes on Delete cascade,
    QuestionID int not null REFERENCES Questions on Delete cascade,
    Primary Key (QuizID, QuestionID)
);

CREATE TABLE Answers (
  ID serial NOT NULL,
  QuestionID int NOT NULL REFERENCES Questions ON DELETE CASCADE,
  Content Text,
  PRIMARY KEY (ID)
);

CREATE TABLE Topics (
  ID serial NOT NULL,
  Name VARCHAR(255) UNIQUE,
  PRIMARY KEY (ID)
);

CREATE TABLE QuestionHasTopics (
  ID serial NOT NULL,
  QuestionID int NOT NULL REFERENCES Questions ON DELETE CASCADE,
  TopicID int NOT NULL REFERENCES Topics ON DELETE CASCADE,
  PRIMARY KEY (ID),
  UNIQUE(QuestionID, TopicID)
);

CREATE TABLE QuestionHasImage (
  ID serial NOT NULL,
  QuestionID int NOT NULL REFERENCES Questions ON DELETE CASCADE,
  Url VARCHAR(255),
  PRIMARY KEY (ID),
  UNIQUE(QuestionID)
);

CREATE TABLE QuestionHasCorrectAnswers (
  ID serial NOT NULL,
  QuestionID int NOT NULL REFERENCES Questions ON DELETE CASCADE,
  AnswerID int NOT NULL,
  FOREIGN KEY (AnswerID) REFERENCES Answers ON DELETE CASCADE,
  PRIMARY KEY (ID),
  UNIQUE(QuestionID, AnswerID)
);
