DROP TABLE IF EXISTS user_has_achievement CASCADE;
DROP TABLE IF EXISTS achievements CASCADE;
DROP TABLE IF EXISTS suggestions CASCADE;
DROP TABLE IF EXISTS explanations CASCADE;
DROP TABLE IF EXISTS answers CASCADE;
DROP TABLE IF EXISTS images CASCADE;
DROP TABLE IF EXISTS question_has_topic CASCADE;
DROP TABLE IF EXISTS topics CASCADE;
DROP TABLE IF EXISTS options CASCADE;
DROP TABLE IF EXISTS quiz_has_question CASCADE;
DROP TABLE IF EXISTS questions CASCADE;
DROP TABLE IF EXISTS quizzes CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
  id SERIAL NOT NULL,
  name VARCHAR(255),
  role VARCHAR(255), -- student, teacher, developer
  username VARCHAR(255),
  year INT,
  PRIMARY KEY (id)
);

CREATE TABLE quizzes (
    id SERIAL NOT NULL,
    title VARCHAR(255),
    year INT,
    type VARCHAR(255), -- E exame or T test or G generated
    series INT, -- Test 1, Test 2
    version VARCHAR(5), -- A, B, C
    generated_by INT REFERENCES users ON DELETE CASCADE,
    completed BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);

CREATE TABLE questions (
    id SERIAL NOT NULL,
    name VARCHAR(255),
    new_id INT REFERENCES questions ON DELETE CASCADE,
    content TEXT NOT NULL,
    difficulty INT,
    active BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id)
);

CREATE TABLE quiz_has_question (
    quiz_id INT NOT NULL REFERENCES quizzes ON DELETE CASCADE,
    question_id INT NOT NULL REFERENCES questions ON DELETE CASCADE,
    sequence INT NOT NULL,
    PRIMARY KEY (quiz_id, question_id)
);

CREATE TABLE options (
  question_id INT NOT NULL REFERENCES questions ON DELETE CASCADE,
  option INT NOT NULL,
  content TEXT NOT NULL,
  correct BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (question_id, option)
);

CREATE TABLE topics (
  id SERIAL NOT NULL,
  parent INT REFERENCES topics,
  name VARCHAR(255) UNIQUE NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE question_has_topic (
  topic_id INT NOT NULL REFERENCES topics ON DELETE CASCADE,
  question_id INT NOT NULL REFERENCES questions ON DELETE CASCADE,
  votes INT,
  score INT,
  PRIMARY KEY (topic_id, question_id)
);

CREATE TABLE images (
  question_id INT NOT NULL REFERENCES questions ON DELETE CASCADE,
  url VARCHAR(255) NOT NULL,
  width INT,
  PRIMARY KEY (question_id)
);

CREATE TABLE answers (
  user_id INT NOT NULL REFERENCES users ON DELETE CASCADE,
  question_id INT NOT NULL REFERENCES questions ON DELETE CASCADE,
  quiz_id INT NOT NULL REFERENCES quizzes ON DELETE CASCADE,
  answer_date TIMESTAMP,
  time_taken TIMESTAMP,
  option INT,
  PRIMARY KEY (user_id, question_id, quiz_id)
);

CREATE TABLE explanations (
  question_id INT NOT NULL REFERENCES questions ON DELETE CASCADE,
  content TEXT NOT NULL,
  PRIMARY KEY (question_id)
);

CREATE TABLE suggestions (
  user_id INT NOT NULL REFERENCES users ON DELETE CASCADE,
  question_id INT NOT NULL REFERENCES questions ON DELETE CASCADE,
  topic_id INT NOT NULL REFERENCES topics ON DELETE CASCADE,
  PRIMARY KEY (user_id, question_id)
);

CREATE TABLE achievements (
  id SERIAL NOT NULL,
  name VARCHAR(255) UNIQUE NOT NULL,
  image_url VARCHAR(255) NOT NULL,
  achieved_image_url VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE user_has_achievement (
    user_id INT NOT NULL REFERENCES users ON DELETE CASCADE,
    achievement_id INT NOT NULL REFERENCES achievements ON DELETE CASCADE,
    achieved_date TIMESTAMP,
    PRIMARY KEY (user_id, achievement_id)
);