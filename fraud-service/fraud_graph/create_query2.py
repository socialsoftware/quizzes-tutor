def def_query(quiz_id = "*", course = "*", semester = "*", type_quiz = "*"):
    
    if course != "*":
        #Considering if the chosen option is true or false
        query = "select answer_date, option_id, quiz_id, quiz_question_id, username, correct from question_answer_items \
                inner join options \
                on options.id = question_answer_items.option_id \
                where question_answer_items.quiz_id in (select id from quizzes where course_execution_id = " + str(course) + ") \
                order by quiz_id, username, answer_date desc;"
        
        # query = "select answer_date, option_id, quiz_id, quiz_question_id, username, correct from question_answer_items where quiz_id in (select id from quizzes where course_execution_id = " + str(course) + ")  \
        #     order by quiz_id, username, answer_date desc;"
    elif quiz_id != "*":
       query = "select answer_date, option_id, quiz_id, quiz_question_id, username, correct from question_answer_items \
       inner join options \
       on options.id = question_answer_items.option_id \
       where quiz_id = " + str(quiz_id) + " \
       order by quiz_id, username, answer_date desc;" #date by descending order so we keep the last answer
    
    return query
    
