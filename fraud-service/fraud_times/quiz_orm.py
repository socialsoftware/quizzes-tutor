from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, Float, DateTime, Boolean, ForeignKey
from sqlalchemy.orm import sessionmaker, relationship
from sqlalchemy.orm import column_property
import pandas as pd
import numpy as np
from sqlalchemy.orm.base import state_attribute_str
from sqlalchemy.sql.schema import Table
from datetime import timedelta
import urllib.parse
import os
Base = declarative_base()


class Option(Base):
    __tablename__ = 'options'

    id = Column(Integer, primary_key=True)

    correct = Column(Boolean)
    sequence = Column(Integer)

    def is_correct(self):
        return self.correct

    def to_dict(self):
        return {
            "id": self.id,
            "correct": self.correct,
            "sequence": self.sequence
        }


class Answer_Details(Base):
    __tablename__ = 'answer_details'

    id = Column(Integer, primary_key=True)

    question_answer_id = Column(Integer, ForeignKey("question_answers.id"))
    option_id = Column(Integer, ForeignKey("options.id"))

    option = relationship("Option")
    question_answer = relationship(
        "Question_Answer", back_populates="details")

    def is_answered(self):
        return self.option != None

    def is_correct(self):
        if not self.option:
            return False
        return self.option.is_correct()

    def __repr__(self):
        return f"<Answer Details(id: {self.id},question_answer_id: {self.question_answer_id},option: {self.option}>"

    def to_dict(self):
        return {
            "id": self.id,
            "question_answer_id": self.question_answer_id,
            "option_id": self.option_id,
            "option": self.option,
            "question_answer": self.question_answer
        }


class Question(Base):
    __tablename__ = 'questions'

    id = Column(Integer, primary_key=True)
    number_of_answers = Column(Integer)
    number_of_correct = Column(Integer)

    quiz_questions = relationship("Quiz_Question", back_populates="question")

    def to_dict(self):
        return {
            "id": self.id,
            "number_of_answers": self.number_of_answers,
            "number_of_correct": self.number_of_correct,
            "quiz_questions": self.quiz_questions
        }


class Question_Answer(Base):
    __tablename__ = 'question_answers'

    id = Column(Integer, primary_key=True)
    time_taken = Column(Float)
    quiz_answer_id = Column(Integer, ForeignKey("quiz_answers.id"))
    quiz_question_id = Column(Integer, ForeignKey("quiz_questions.id"))
    sequence = Column(Integer)

    details = relationship(
        "Answer_Details")

    quiz_question = relationship(
        "Quiz_Question", back_populates="answers")  # Many to One
    quiz_answer = relationship(
        "Quiz_Answer", back_populates="answers")  # Many to One

    def __repr__(self):
        return f"<Answer(sequence: {self.sequence},time_taken: {self.time_taken}, user_id: {self.quiz_answer.user_id if self.quiz_answer else 'N/A'}, quiz_question_id: {self.quiz_question_id}, quiz_answer_id: {self.quiz_answer_id})>"

    def is_answered(self):
        if self.details:
            return self.details[0].is_answered()
        return False

    def is_correct(self):
        if self.details:
            return self.details[0].is_correct()
        else:
            return False

    def get_user(self):
        return self.quiz_answer.user_id

    def get_time_taken(self):
        return self.time_taken/1000 if self.time_taken else 0

    def get_relative_time_taken(self):
        if self.time_taken:
            return self.get_time_taken() / self.quiz_answer.get_time_taken()
        else:
            return 0

    def get_relative_time_taken_by_quiz_time(self):
        if self.time_taken:
            return timedelta(seconds=self.get_time_taken()) / (self.quiz_question.quiz.conclusion_date - self.quiz_question.quiz.available_date)
        else:
            return 0

    def get_question_avg_time(self):
        return self.quiz_question.get_avg_time()

    def get_sequence_avg_time(self):
        return self.quiz_answer.quiz.get_sequence_avg_time(self.sequence)

    def get_question_avg_relative_time(self):
        return self.quiz_question.get_avg_relative_time()

    def get_sequence_avg_relative_time(self):
        return self.quiz_answer.quiz.get_sequence_avg_relative_time(self.sequence)

    def to_dict(self):
        return {
            "id": self.id,
            "time_taken": self.time_taken,
            "quiz_answer_id": self.quiz_answer_id,
            "quiz_question_id": self.quiz_question_id,
            "sequence": self.sequence,
            "details": self.details,
            "quiz_question": self.quiz_question,
            "quiz_answer": self.quiz_answer
        }


class Quiz_Question(Base):
    __tablename__ = "quiz_questions"

    id = Column(Integer, primary_key=True)
    question_id = Column(Integer, ForeignKey("questions.id"))
    quiz_id = Column(Integer, ForeignKey("quizzes.id"))
    sequence = Column(Integer)

    question = relationship("Question", back_populates="quiz_questions")
    quiz = relationship("Quiz", back_populates="quiz_questions")  # Many to One
    answers = relationship("Question_Answer", order_by=Question_Answer.quiz_question_id,
                           back_populates="quiz_question")  # One to Many
    avg_time = None
    relative_avg_time = None

    def __repr__(self):
        return f"<Quiz_question(question_id: {self.question_id}, quiz_id: {self.quiz_id})>"

    def get_avg_time(self):
        if not self.avg_time:
            self.avg_time = np.average(
                [answer.get_time_taken() for answer in self.answers])
        return self.avg_time

    def get_avg_relative_time(self):
        if not self.relative_avg_time:
            self.relative_avg_time = np.average(
                [answer.get_relative_time_taken() for answer in self.answers])
        return self.relative_avg_time

    def get_difficulty_score(self):
        answers = self.question.number_of_answers
        correct = self.question.number_of_correct
        return 1 - (correct / answers) if answers > 0 else None

    def to_dict(self):
        return {
            "id": self.id,
            "question_id": self.question_id,
            "quiz_id": self.quiz_id,
            "sequence": self.sequence,
            "question": self.question,
            "quiz": self.quiz,
            "answers": self.answers
        }


class Quiz_Answer(Base):
    __tablename__ = "quiz_answers"

    id = Column(Integer, primary_key=True)
    quiz_id = Column(Integer, ForeignKey("quizzes.id"))
    user_id = Column(Integer, ForeignKey("users.id"))

    quiz = relationship("Quiz", back_populates="quiz_answers")  # Many to One
    answers = relationship("Question_Answer", order_by=Question_Answer.sequence,
                           back_populates="quiz_answer")  # One to Many
    user = relationship("User", back_populates="quiz_answers")

    def __repr__(self):
        return f"<Quiz_answer(user_id: {self.user_id}, quiz_id: {self.quiz_id})>"

    def get_grade(self):
        grade = 0
        for answer in self.answers:
            if answer.is_correct():
                grade += 1
        return grade/len(self.answers) if self.answers else 0

    def is_fully_answered(self):
        if self.answers:
            return all([answer.is_answered() for answer in self.answers])
        return False

    def get_nr_answered_questions(self):
        return len([ans for ans in self.answers if ans.is_answered()])

    def get_time_taken(self):
        return sum([answer.get_time_taken() for answer in self.answers])

    def to_dict(self):
        return {
            "id": self.id,
            "quiz_id": self.quiz_id,
            "user_id": self.user_id,
            "quiz": self.quiz,
            "answers": self.answers,
            "user": self.user
        }


class Quiz(Base):
    __tablename__ = "quizzes"

    id = Column(Integer, primary_key=True)

    available_date = Column(DateTime)
    conclusion_date = Column(DateTime)
    scramble = Column(Boolean)
    one_way = Column(Boolean)
    course_execution_id = Column(Integer)
    title = Column(String)

    sequence_avg_relative_time = None
    sequence_avg_time = None

    type = Column(String)

    quiz_questions = relationship(
        "Quiz_Question", order_by=Quiz_Question.sequence, back_populates="quiz")
    quiz_answers = relationship(
        "Quiz_Answer", order_by=Quiz_Answer.user_id, back_populates="quiz")

    def __repr__(self):
        return f"<Quiz(id: {self.id}, title: {self.title}, type: {self.type}, scramble: {self.scramble}, one_way: {self.one_way}, course exec. id: {self.course_execution_id}, avail. date: {self.available_date}, conc. date: {self.conclusion_date})>"

    def get_title(self):
        return self.title.upper()

    def get_quiz_questions(self):
        return sorted(self.quiz_questions, lambda q_q: 1 - q_q.get_difficulty_score())

    def get_length(self):
        return self.conclusion_date - self.available_date

    def get_users(self):
        return [quiz_answer.user_id for quiz_answer in self.quiz_answers]

    def get_answers(self):
        return [ans for q_a in self.quiz_answers for ans in q_a.answers]

    def get_answers_per_user(self, users=None):
        return {q_a.user_id: q_a.answers for q_a in self.quiz_answers if (not users or q_a.user_id in users)}

    def get_answers_per_question_id(self, quiz_questions=None):
        questions_answers = {q_q.id: q_q.answers for q_q in self.quiz_questions if (
            not quiz_questions or q_q.id in quiz_questions)}
        for answers in questions_answers.values():
            answers.sort(key=lambda answer: answer.get_user())
        return questions_answers

    def get_answer_per_question_sequence(self, question_numbers=None):
        #  Get all answers
        answers = [ans for q_a in self.quiz_answers for ans in q_a.answers if (
            not question_numbers or ans.sequence in question_numbers)]

        #  Get answers into the respective sequence list
        result = {}
        for answer in answers:
            if answer.sequence not in result:
                result[answer.sequence] = []
            result[answer.sequence].append(answer)
        return result

    def get_questions_ids(self):
        return [q_q.id for q_q in self.quiz_questions]

    def get_questions_avg_time(self):
        return {question.id: question.get_avg_time() for question in self.quiz_questions}

    def get_questions_avg_relative_time(self):
        return {question.id: question.get_avg_time() for question in self.quiz_questions}

    def get_sequence_avg_relative_time(self, sequence):
        if not self.sequence_avg_relative_time:
            self.sequence_avg_relative_time = np.average([answer.get_relative_time_taken(
            ) for quiz_answer in self.quiz_answers for answer in quiz_answer.answers if answer.sequence == sequence])
        return self.sequence_avg_relative_time

    def get_sequence_avg_time(self, sequence):
        if not self.sequence_avg_time:
            self.sequence_avg_time = np.average([answer.get_time_taken(
            ) for quiz_answer in self.quiz_answers for answer in quiz_answer.answers if answer.sequence == sequence])
        return self.sequence_avg_time

    def to_dict(self):
        return {
            "id": self.id,
            "available_date": self.available_date,
            "conclusion_date": self.conclusion_date,
            "scramble": self.scramble,
            "one_way": self.one_way,
            "course_execution_id": self.course_execution_id,
            "title": self.title,
            "type": self.type
        }


user_course_executions_table = Table('users_course_executions', Base.metadata,
                                     Column('users_id', Integer,
                                            ForeignKey('users.id')),
                                     Column('course_executions_id', Integer,
                                            ForeignKey('course_executions.id'))
                                     )


class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True)
    role = Column(String)
    name= Column(String)
    number_of_student_quizzes = Column(Integer)
    number_of_in_class_answers = Column(Integer)
    number_of_correct_in_class_answers = Column(Integer)
    number_of_in_class_quizzes = Column(Integer)
    quiz_answers = relationship("Quiz_Answer", back_populates="user")
    auth_user = relationship("Auth_User", back_populates="user")
    course_executions = relationship(
        "Course_Execution", secondary=user_course_executions_table, back_populates="users")

    def __repr__(self):
        return f"<User(id: {self.id}, role: {self.role}), name: {self.name}>"

    def get_in_class_quiz_answers(self, course_execution_id=False):
        if course_execution_id:
            return [quiz_answer for quiz_answer in self.quiz_answers if quiz_answer.quiz.type == "IN_CLASS" and quiz_answer.quiz.course_execution_id == course_execution_id]
        else:
            return [quiz_answer for quiz_answer in self.quiz_answers if quiz_answer.quiz.type == "IN_CLASS"]

    def get_all_questions_answers(self):
        return [answer for quiz_answer in self.quiz_answers for answer in quiz_answer.answers if answer.details if quiz_answer.quiz.type == "IN_CLASS"]

    def get_quiz_answers(self, quiz_id):
        return [answer for quiz_answer in self.quiz_answers for answer in quiz_answer.answers if answer.details and quiz_answer.quiz.id == quiz_id]

    def to_dict(self, deep=False):
        result = {
            "id": self.id,
            "role": self.role,
            "name": self.name,
            "number_of_student_quizzes": self.number_of_student_quizzes,
            "number_of_in_class_answers": self.number_of_in_class_answers,
            "number_of_correct_in_class_answers": self.number_of_correct_in_class_answers,
            "number_of_in_class_quizzes": self.number_of_in_class_quizzes,
            "auth_user": self.auth_user.to_dict() if self.auth_user else None
            }
        if deep:
            result["quiz_answers"] = self.quiz_answers
        return result

class Auth_User(Base):
    __tablename__ = "auth_users"

    id = Column(Integer, primary_key=True)
    auth_type = Column(String)
    email = Column(String)
    username = Column(String)
    user_id = Column(Integer, ForeignKey('users.id'))

    user = relationship("User", back_populates="auth_user", uselist=False)

    def __repr__(self):
        return f"<Auth_User(id: {self.id}, auth_type: {self.auth_type}), email: {self.email}>"

    def to_dict(self):
        return {
            "id": self.id,
            "auth_type": self.auth_type,
            "email": self.email,
            "username": self.username,
            "user_id": self.user_id,
            "name": self.user.name
        }
    
    def __repr__(self):
        return f"<Auth_User(id: {self.id}, auth_type: {self.auth_type}, username: {self.username}, user_id: {self.user_id})>"


class Course_Execution(Base):
    __tablename__ = "course_executions"

    id = Column(Integer, primary_key=True)
    academic_term = Column(String)
    status = Column(String)
    acronym = Column(String)
    course_id = Column(Integer)
    type = Column(String)
    end_date = Column(DateTime)
    users = relationship("User",
                         secondary=user_course_executions_table,
                         back_populates="course_executions")

    def __repr__(self):
        return f"<Course Execution(id: {self.id},type:{self.type}, academic term: {self.academic_term}, status: {self.status},acronym: {self.acronym},course id: {self.course_id},)>"


class QuizzesDBConnector():

    def __init__(self):
        user = urllib.parse.quote_plus(os.getenv("POSTGRES_USER", "engineer"))
        password = urllib.parse.quote_plus(os.getenv("POSTGRES_PASSWORD", "password123"))
        host = urllib.parse.quote_plus(os.getenv('POSTGRES_HOST', 'localhost'))
        port = urllib.parse.quote_plus(os.getenv("POSTGRES_PORT", "5432"))
        database = urllib.parse.quote_plus(os.getenv("POSTGRES_DB", "tutordb"))
        engine = create_engine(
            f'postgresql://{user}:{password}@{host}:{port}/{database}')
        Session = sessionmaker(bind=engine)
        self.session = Session()

    def __end__(self):
        if (self.session):
            self.session.close()

    def close(self):
        self.session.close()

    def get_quiz(self, quiz_id):
        return self.session.query(Quiz).filter(Quiz.id == quiz_id).one()

    def get_quizzes_in_class_from_course_execution(self, course_execution):
        return self.session.query(Quiz).filter(Quiz.course_execution_id == course_execution).filter(Quiz.type == "IN_CLASS").filter(Quiz.conclusion_date < '2021-07-19').all()

    def get_user(self, user_id):
        return self.session.query(User).filter(User.id == user_id).one()

    def get_course_execution(self, course_execution):
        return self.session.query(Course_Execution).filter(Course_Execution.id == course_execution).one()

    def getAuthUserInformationByUserId(self, user_id):
        return self.session.query(Auth_User).join(User).filter(User.id == user_id).one()
    
    def getAuthUserInformationByUsername(self, username):
        return self.session.query(Auth_User).filter(Auth_User.username == username).one()

    def get_answers_from_course_execution(self, course_execution):
        return self.session.query(Question_Answer).join(Quiz_Answer).join(Quiz).filter(Quiz.course_execution_id == course_execution).filter(Quiz.type == "IN_CLASS").filter(Quiz.conclusion_date < '2021-07-19').filter(Question_Answer.time_taken != None).all()
