import flask
import os
from dotenv import load_dotenv
from pathlib import Path
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


@app.route('/time/quiz/<quizId>')
def quizFraudScores(quizId):
    db = quiz_orm.QuizzesDBConnector()
    quiz = db.get_quiz(quizId)
    statistic = quiz_stats.Quiz_Statistic(
        quiz, quiz_stats.QUIZ_SCORERS["Diff. Mean (Question ID)"])

    result = {"scores": [{"userInfo": db.getAuthUserInformationByUserId(user_id).to_dict(), "score": user_score}
              for user_id, user_score in statistic.statistic.items()]}
    return flask.jsonify(result)


@app.route('/communication/quiz/<quizId>')
def quizFraudScoresGraph(quizId):
    data = create_dataset.create_dataset(quizId, "*")
    scores_in, scores_out = get_scores.create_network(
        "All", data, True, True, 1)
    db = quiz_orm.QuizzesDBConnector()
    scores_in = [{"userInfo": db.getAuthUserInformationByUsername(username).to_dict(), "score": user_score}
                 for username, user_score in scores_in.items()]
    scores_out = [{"userInfo": db.getAuthUserInformationByUsername(username).to_dict(), "score": user_score}
                  for username, user_score in scores_out.items()]
    return flask.jsonify({"scoresIn": scores_in, "scoresOut": scores_out})
