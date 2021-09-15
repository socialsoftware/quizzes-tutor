import json
import sys

sys.path.append('fraud_graph/')
import create_dataset2 as cd
import get_scores as gs
import fraud_times.quiz_orm as qo
import fraud_times.quiz_statistics as qs
from flask import Flask, Response


app = Flask(__name__)


@app.route('/quiz/<quizId>')
def quizFraudScores(quizId):
    dbConnector = qo.QuizzesDBConnector("mqfd", "5432")
    quiz = dbConnector.get_quiz(quizId)
    statistic = qs.Quiz_Statistic(
        quiz, qs.QUIZ_SCORERS["Diff. Mean (Question ID)"])
    result = [{"username": k, "score": v}
              for k, v in statistic.statistic.items()]
    return Response(json.dumps(result), mimetype="application/json")


@app.route('/graph/quiz/<quizId>')
def quizFraudScoresGraph(quizId):
    data = cd.create_dataset(quizId, "*")
    scores_in, scores_out = gs.create_network("All", data, True, True, 2)
    print(scores_in)
    print(scores_out)
    scores_in = [{"username": k, "score": v}
                 for k, v in scores_in.items()]
    scores_out = [{"username": k, "score": v}
                  for k, v in scores_out.items()]
    return Response(json.dumps({"scoresIn": scores_in, "scoresOut": scores_out}), mimetype="application/json")
