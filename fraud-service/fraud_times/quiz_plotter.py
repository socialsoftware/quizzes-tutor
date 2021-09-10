import matplotlib.pyplot as plt
import pandas as pd
from quiz_orm import Quiz
import numpy as np


class QuizPlotter():

    stacked = "STACKED"

    @staticmethod
    def plot_quiz(quiz: Quiz):
        fig = plt.figure(figsize=(16, 9))
        for i, quiz_question in enumerate(quiz.quiz_questions):
            answer_times = [
                answer.time_taken for answer in quiz_question.answers]
            times_series = pd.Series(answer_times)
            ax = fig.add_subplot(5, 1, i+1)
            ax.hist(times_series, bins=20)
        plt.show()

    @staticmethod
    def __plot_grouped_quiz_times(title, x_labels, answers_times, color_codes):
        nr_questions = len(answers_times)
        fig, ax = plt.subplots(figsize=(60, 9),ylabel="seconds")
        width = 0.8/nr_questions
        x = np.arange(len(x_labels))
        for q_idx in range(nr_questions):
            ax.bar(x - (nr_questions/2 * width) + width/2 + q_idx*width,
                   answers_times[q_idx], width, label=f"Question {color_codes[q_idx]}")
        ax.set_title(f"Quiz {title}")
        ax.set_xticks(x)
        ax.set_xticklabels(x_labels)
        ax.legend()
        plt.axhline(60, color='black', linestyle='-')
        plt.show()

    @staticmethod
    def __plot_stacked_quiz_times(title, x_labels, answers_times, labels):
        fig, ax = plt.subplots(figsize=(30, 9))
        nr_questions = len(answers_times)
        x = np.arange(len(x_labels))
        cumulative_questions_times = np.zeros(
            len(answers_times[0]))  # values for plotting stacked bars
        for q_idx in range(len(answers_times)):
            answer_times = answers_times[q_idx]
            ax.bar(x, answer_times,
                   label=labels[q_idx], bottom=cumulative_questions_times)
            cumulative_questions_times = np.add(
                cumulative_questions_times, answer_times)
        ax.set_title(f"Quiz {title}")

        
        ax.set_xticks(x)
        
        ax.legend()
        for i in range(nr_questions):
            plt.axhline((i+1)*60, color='black', linestyle='-')
        plt.show()

    @staticmethod
    def plot_stacked_quiz_per_question_sequence(quiz: Quiz):
        questions = quiz.get_answer_per_question_sequence()
        x_labels = list(map(lambda answer: str(
            answer.get_user()), list(questions.values())[0]))
        times = [[answer.get_time_taken() for answer in question]
                 for question in questions.values()]
        labels = [f"Sequence {question}" for question in questions.keys()]
        QuizPlotter.__plot_stacked_quiz_times(
            quiz.title, x_labels, times,labels)

    @staticmethod
    def plot_grouped_quiz_per_question_sequence(quiz: Quiz):
        questions = quiz.get_answer_per_question_sequence()
        x_labels = list(map(lambda answer: str(
            answer.get_user()), list(questions.values())[0]))
        times = [[answer.get_time_taken() for answer in question]
                 for question in questions.values()]
        QuizPlotter.__plot_grouped_quiz_times(
            quiz.title, x_labels, times, list(questions.keys()))

    @staticmethod
    def plot_stacked_quiz_per_question_id(quiz: Quiz):
        questions = quiz.get_answers_per_question_id()
        x_labels = list(map(lambda answer: str(
            answer.get_user()), list(questions.values())[0]))
        times = [[answer.get_time_taken() for answer in question]
                 for question in questions.values()]
        labels = [f"Question {question}" for question in questions.keys()]
        QuizPlotter.__plot_stacked_quiz_times(
            quiz.title, x_labels, times, labels)

    @staticmethod
    def plot_grouped_quiz_per_question_id(quiz: Quiz):
        questions = quiz.get_answers_per_question_id()
        x_labels = list(map(lambda answer: str(
            answer.get_user()), list(questions.values())[0]))
        times = [[answer.get_time_taken() for answer in question]
                 for question in questions.values()]
        QuizPlotter.__plot_grouped_quiz_times(
            quiz.title, x_labels, times, list(questions.keys()))
