import numpy as np
from scipy import stats
import matplotlib.pyplot as plt
import pandas as pd
import math

DEFAULT_SEQUENCE_WEIGHTS= {0: 1, 1:1, 2:1, 3: 1, 4: 1}
SEQUENCE_CRITERIA = "Sequence"
QUESTION_ID_CRITERIA = "Question"
EQUAL_CRITERIA = "Equal"

class Question_Answer_Scorer:

    def __init__(self,name,function,criteria,sequence_weights=DEFAULT_SEQUENCE_WEIGHTS):
        self.function = function
        self.criteria = criteria
        self.name = name
        self.seq_w = sequence_weights
    
    def get_criteria(self,answer):
        if self.criteria == SEQUENCE_CRITERIA:
            return answer.get_sequence_avg_time()
        elif self.criteria == QUESTION_ID_CRITERIA:
            return answer.get_question_avg_time()
        elif self.criteria == EQUAL_CRITERIA:
            return 60
    
    def get_relative_criteria(self,answer):
        if self.criteria == SEQUENCE_CRITERIA:
            return answer.get_sequence_avg_relative_time()
        elif self.criteria == QUESTION_ID_CRITERIA:
            return answer.get_question_avg_relative_time()
        elif self.criteria == EQUAL_CRITERIA:
            return 0.2

    def get_relative_score(self,answer):
        criteria_value = self.get_relative_criteria(answer)
        answer_time = answer.get_relative_time_taken()
        return self.function(answer_time,criteria_value)
    
    def get_score(self,answer):
        criteria_value = self.get_criteria(answer)
        answer_time = answer.get_time_taken()
        answer_weight = self.seq_w[answer.sequence]
        return self.function(answer_time,criteria_value) * answer_weight


def difference(time_1,time_2):
    return time_1-time_2

def abs_difference(time_1,time_2):
    return abs(time_1 - time_2)

def positive_difference(time_1,time_2):
    return max(time_1 - time_2, 0)

def get_quiz_stat_dataframe(quiz_stat):
    stat_dataframe = pd.DataFrame.from_dict(quiz_stat.scores, orient="index", columns=["score","partial_scores"])
    stat_dataframe["user"] = stat_dataframe.index
    stat_dataframe["quiz"] = quiz_stat.quiz.title
    return stat_dataframe

def get_quiz_scores_dataframe(quiz_stat):
    stat_dataframe = pd.DataFrame.from_dict(quiz_stat.statistic, orient="index",columns= ["score"])
    stat_dataframe["user"] = stat_dataframe.index
    stat_dataframe["quiz"] = quiz_stat.quiz.title
    stat_dataframe["scorer"] = quiz_stat.scorer.name
    return stat_dataframe

def calculate_quiz_answer_score(quiz_answer, scorer, use_relative_scores= False, ):
    result = {'score': 0,'question_answers_scores': {}}
    for question_answer in quiz_answer.answers:
        if not use_relative_scores:
            score = scorer.get_score(question_answer)
        else:
            score = scorer.get_relative_score(question_answer)
        result['question_answers_scores'][question_answer.sequence] = score
        result['score'] += score
    return result

def calculate_quiz_scores(quiz,scorer,use_relative_scores=False):
    result = {}
    for user_answer in quiz.quiz_answers:
        result[user_answer.user_id] = calculate_quiz_answer_score(user_answer,scorer,use_relative_scores)
    return {k: v for k, v in reversed(sorted(result.items(), key=lambda item: item[1]['score']))}


class Quiz_Statistic():
    def __init__(self, quiz, scorer):
        self.quiz = quiz
        self.scorer = scorer
        self.scores = calculate_quiz_scores(quiz,scorer)
        self.statistic = {user: value['score'] for user, value in self.scores.items()}
        self.shapiro = self.test_normal_statistic()
        self.follows_normal_distribution = self.shapiro if not self.shapiro else self.shapiro.pvalue > 0.05

        if(self.follows_normal_distribution):
            self.normal_dist = self.calc_normal_theoretical_dist()

    def calc_normal_theoretical_dist(self):
        values = list(self.statistic.values())
        self.avg = np.average(values)
        self.std = np.std(values)
        
    def get_outliers(self,ppf):
        if (self.follows_normal_distribution):
            ppf = stats.norm.ppf(ppf,self.avg,self.std)
            return list(dict(filter(lambda user: user[1] > ppf , self.statistic.items())).keys())
        else:
            nr_users = len(self.statistic.keys())
            stop_index = math.floor(nr_users - nr_users * ppf)
            return list(self.statistic.keys())[:stop_index]


    def test_normal_statistic(self):
        if (len(self.statistic) < 4):
            return False
        shapiro_test = stats.shapiro(list(self.statistic.values()))
        return shapiro_test

    def plot_statistic(self):
        fig, (ax1, ax2, ax3) = plt.subplots(1, 3, figsize=(30, 5))
        ax1.hist(self.statistic.values(), bins=5,density=True)
        ax1.set_title("bin-size: 5")
        ax2.hist(self.statistic.values(), bins=10,density=True)
        ax2.set_title("bin-size: 10")
        ax3.hist(self.statistic.values(), bins=20,density=True)
        ax3.set_title("bin-size: 20")
        plt.suptitle(
            self.quiz.title + f" {'drawn from normal' if self.follows_normal_distribution else 'NOT drawn from normal'}")
        if(self.follows_normal_distribution):
            norm_color = 'tab:red'
            x = np.linspace(self.avg - 3*self.std, self.avg + 3*self.std, 100)
            pdf = stats.norm.pdf(x, self.avg, self.std)
            ax1.plot(x, pdf,color=norm_color)
            ax2.plot(x, pdf,color=norm_color)
            ax3.plot(x, pdf,color=norm_color)
        fig.tight_layout()
        plt.show()


QUIZ_SCORERS = {
    "Diff. Mean (Question ID)": Question_Answer_Scorer("Diff. Mean (Question ID)",difference,QUESTION_ID_CRITERIA,{0:1, 1:0.6, 2:0.2, 3:-0.2, 4:-0.6}),
    "Positive Diff. Mean (Question ID)": Question_Answer_Scorer("Positive Diff. Mean (Question ID)",positive_difference,QUESTION_ID_CRITERIA,{0:1, 1:0.6, 2:0.2, 3:-0.2, 4:-0.6}),
    "Absolute Diff. Mean (Question ID)": Question_Answer_Scorer("Absolute Diff. Mean (Question ID)",abs_difference,QUESTION_ID_CRITERIA,{0:1, 1:0.6, 2:0.2, 3:-0.2, 4:-0.6}),
    "Diff. Mean (Sequence ID)": Question_Answer_Scorer("Diff. Mean (Sequence ID)",difference,SEQUENCE_CRITERIA,{0:1, 1:0.6, 2:0.2, 3:-0.2, 4:-0.6}),
    "Positive Diff. Mean (Sequence ID)": Question_Answer_Scorer("Positive Diff. Mean (Sequence ID)",positive_difference,SEQUENCE_CRITERIA,{0:1, 1:0.6, 2:0.2, 3:-0.2, 4:-0.6}),
    "Absolute Diff. Mean (Sequence ID)": Question_Answer_Scorer("Absolute Diff. Mean (Sequence ID)",abs_difference,SEQUENCE_CRITERIA,{0:1, 1:0.6, 2:0.2, 3:-0.2, 4:-0.6}),
}
