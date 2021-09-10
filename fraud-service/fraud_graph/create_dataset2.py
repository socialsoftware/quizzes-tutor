import pandas as pd
import queries_database2
import create_query2


def create_dataset (quiz_id, course_id): #id course AS = 92, id ES = 117   # course - ("*", 92)  #quiz - (14225), 14947
    dataframe = queries_database2.fetch_data(create_query2.def_query(quiz_id, course_id))

    #list of quizzes dataframes to concatenate at the end
    list_quizzes_dataframes = []

    for i in dataframe.quiz_id.unique(): #run the list of possible quizzes in order to select just the last answers

        #get the subdataset for each quiz
        dataset_quiz_i = dataframe.loc[dataframe['quiz_id'] == i]

        #initialize auxiliary variables
        username_before = 0
        quiz_question_id_before = 0
        option_id_before = 0
        obs_to_remove = [] #list with index of observations of dataframe to drop (keep only the last answer)


        for index in dataset_quiz_i.index: #run the dataset_quiz_i indexes

            #get current values of user and question_id
            username_now = dataset_quiz_i["username"][index]
            quiz_question_id_now = dataset_quiz_i["quiz_question_id"][index]

            #if the user and question_id are the same, delete row
            if username_now == username_before and quiz_question_id_now == quiz_question_id_before:
                obs_to_remove += [index]

            #update values of auxiliary variables
            username_before = username_now
            quiz_question_id_before = quiz_question_id_now


        #drop the observations correspondent to answers which were not the last ones for this quiz
        dataset_quiz_i = dataset_quiz_i.drop(obs_to_remove)

        list_quizzes_dataframes += [dataset_quiz_i]

    dataset = pd.concat(list_quizzes_dataframes)

    return dataset

