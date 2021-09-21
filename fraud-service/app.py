from pathlib import Path
from dotenv import load_dotenv
import os
import flask
import sys
sys.path.append('fraud_graph/')
import fraud_times.quiz_statistics as quiz_stats
import fraud_times.quiz_orm as quiz_orm
import get_scores as get_scores
import create_dataset2 as create_dataset
import json




# Does not override env vars, so docker-compose shall set env vars if run. Otherwise, sets env vars from .env file
load_dotenv()
app = flask.Flask(__name__)


@app.route('/quiz/<quizId>')
def quizFraudScores(quizId):
    dbConnector = quiz_orm.QuizzesDBConnector()
    quiz = dbConnector.get_quiz(quizId)
    statistic = quiz_stats.Quiz_Statistic(
        quiz, quiz_stats.QUIZ_SCORERS["Diff. Mean (Question ID)"])
    dbConnector.close()
    
    result = [{"username": k, "score": v}
              for k, v in statistic.statistic.items()]
    return flask.jsonify(result)


@app.route('/graph/quiz/<quizId>')
def quizFraudScoresGraph(quizId):
    data = create_dataset.create_dataset(quizId, "*")
    scores_in, scores_out = get_scores.create_network(
        "All", data, True, True, 1)
    scores_in = [{"username": k, "score": v}
                 for k, v in scores_in.items()]
    scores_out = [{"username": k, "score": v}
                  for k, v in scores_out.items()]
    return flask.jsonify({"scoresIn": scores_in, "scoresOut": scores_out})
