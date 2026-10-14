import flask
import os
from dotenv import load_dotenv
from pathlib import Path
import sys
sys.path.append('fraud_graph/')
import fraud_times.quiz_statistics as quiz_stats
import fraud_times.quiz_orm as quiz_orm
import fraud_times.speed as speed
import get_scores as get_scores
import create_dataset2 as create_dataset
import json
import hmac


# Does not override env vars, so docker-compose shall set env vars if run. Otherwise, sets env vars from .env file
load_dotenv()
app = flask.Flask(__name__)

FRAUD_API_KEY = os.environ.get("FRAUD_API_KEY", "")
if not FRAUD_API_KEY:
    print("WARNING: FRAUD_API_KEY is not set: anyone who can reach this service can read students' data.", file=sys.stderr)


@app.before_request
def only_the_backend():
    """Every call must carry the key the backend was given (and nothing else is exposed)."""
    if not FRAUD_API_KEY:
        return None
    given = flask.request.headers.get("X-API-Key", "")
    if not hmac.compare_digest(given.encode(), FRAUD_API_KEY.encode()):
        return flask.jsonify({"detail": "missing or wrong API key"}), 401
    return None


@app.route('/time/quiz/<int:quizId>')
def quizFraudScores(quizId):
    db = quiz_orm.QuizzesDBConnector()
    quiz = db.get_quiz(quizId)
    statistic = quiz_stats.Quiz_Statistic(
        quiz, quiz_stats.QUIZ_SCORERS["Diff. Mean (Question ID)"])

    result = {"scores": [{"userInfo": db.getAuthUserInformationByUserId(user_id).to_dict(), "score": user_score}
              for user_id, user_score in statistic.statistic.items()]}
    db.close()
    return flask.jsonify(result)


@app.route('/speed/quiz/<int:quizId>')
def quizSpeedScores(quizId):
    """Students whose answers came faster, or more evenly, than a person can read and decide."""
    db = quiz_orm.QuizzesDBConnector()
    try:
        reports = speed.analyse(db.get_speed_rows(quizId))
        users = [{"userInfo": db.getAuthUserInformationByUserId(report.user_id).to_dict(), **report.to_dict()}
                 for report in reports]
    finally:
        db.close()
    return flask.jsonify({"users": users})


@app.route('/communication/quiz/<int:quizId>')
def quizFraudScoresGraph(quizId):
    data = create_dataset.create_dataset(quizId, "*")
    scores_in, scores_out = get_scores.create_network(
        "All", data, True, True, 1)
    db = quiz_orm.QuizzesDBConnector()
    scores_in = [{"userInfo": db.getAuthUserInformationByUsername(username).to_dict(), "score": user_score}
                 for username, user_score in scores_in.items()]
    scores_out = [{"userInfo": db.getAuthUserInformationByUsername(username).to_dict(), "score": user_score}
                  for username, user_score in scores_out.items()]
    db.close()
    return flask.jsonify({"scoresIn": scores_in, "scoresOut": scores_out})
