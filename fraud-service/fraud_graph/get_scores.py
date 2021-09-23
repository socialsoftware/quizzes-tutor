import networkx as nx
import numpy as np
import math


def create_network (correct_answers, data, p_factor, realmodelQ, n_edges_score):

    #correct_answers is a string which assumes the following values: True, False, "All"
    #p_factor is a bool that assumes the value True if the factor (1-p) is to be considered for the weights and False otherwise
    #realmodelQ is a Bool identifying if the network is for a real model (True) or not (False)
    #n_edges_score is the number of edges to be considered when computing the score

    #Load dataset
    dataset = data

    #initialize the directed graph (A -> B iff A answered to question x before B and both chose the same option;
    #the weight is a value >0 and <1, and multiple of 1/num_questions)
    connected_students = nx.DiGraph()

    #Get list of usernames
    students = dataset.username.unique()

    #Add the students (nodes) to the graph
    connected_students.add_nodes_from(students)

    #Get number of quizzes
    num_quizzes = len(dataset.quiz_id.unique())

    #Get total number of questions considering all the quizzes
    total_num_questions = len(dataset.quiz_question_id.unique())

    #Initialize a dictionary with the students' performance (% of correct answers)
    students_performance = {}

    #Initialize a dictionary with the % of choice for each option_id in the set of all quizzes and questions
    percent_options = {}

    #Initialize a dictionary with the % of correct answers for each question in the set of all quizzes
    percent_correct_questions = {}

    #Initialize a dictionary of the edge colors
    edge_colors = {}

    #Initialize a dictionary with the correspondence of -> question: quiz
    questions_by_quiz = {}

    #Initialize a dictionary with the ranks of quizzes
    rank_quizzes = {}
    #Initialize the rank var
    rank = 0

    #Initialize a dictionary with the correspondence of -> quiz: number of questions
    num_quest_by_quiz = {}

    #Initialize a dictionary with the number of quizzes each student participated
    num_question_participations = {}

    #Initialize a dictionary that has as keys the questions and as values dicionaries with keys the student and values 1 if
    #his/her answer is correct or 0 otherwise
    correct_question_per_student = {}

    #Initialize a dictionary that has as keys the questions and as values their solution frequency (higher values means that the question is easier)
    sol_freq_per_question = {}

    #Initialize a dictionary that has as keys the questions and as values their solution frequency penalized (higher values means that the question is easier)
    sol_freq_per_question_penalized = {}

    for i in dataset.quiz_id.unique(): #run the list of possible quizzes to compute edges and weights

        #print("quiz_id =", i)

        #Get the subdataset for each quiz
        dataset_quiz_i = dataset.loc[dataset['quiz_id'] == i]

        #Update the dictionary with the rank of quizzes
        rank_quizzes[i] = rank

        #Get number of questions of this quiz(to compute edge's weights) - each quiz has its won factor, given the number of questions
        num_questions = len(dataset_quiz_i.quiz_question_id.unique())

        #Store the number of questions on this quiz
        num_quest_by_quiz[str(i)] = num_questions

        #Sort dataset for quiz i by quiz_question_id and answer_date
        dataset_quiz_i = dataset_quiz_i.sort_values(by=['quiz_question_id', 'answer_date'], ascending = [True, True])



        for question in dataset_quiz_i.quiz_question_id.unique(): #run the list of possible question_id

            #Initialize the empty dictionary for this question
            correct_question_per_student[question] = {}

            #print("question =", question)

            #Get the subdataset for each question_id
            dataset_qi = dataset_quiz_i.loc[dataset_quiz_i['quiz_question_id'] == question]

            #Get list of students which participated in this question
            participating_students = dataset_qi.username.unique()

            for participant in participating_students:
                if participant in num_question_participations.keys():
                    num_question_participations[participant] += 1
                else:
                    num_question_participations[participant] = 1


            #Update the dictionary with the correspondence of -> question: quiz
            questions_by_quiz[question] = i

            #Initialize the percentage of correct answers for this question
            percent_correct_answers = 0

            #Get the percentage for each option_id/correct answers in this question
            for user in range(len(dataset_qi)):

                #Get user name
                username = dataset_qi['username'].iloc[user]

                #Get the option_id chosen by this user
                option_chosen = dataset_qi['option_id'].iloc[user]

                #Check if the option chosen is correct or not
                is_correct = dataset_qi['correct'].iloc[user]

                #If the option chosen is correct, update the percentage of correct answers value
                if is_correct:
                    percent_correct_answers += 1/len(dataset_qi)

                    #save the information on this student's answer
                    correct_question_per_student[question][username] = 1
                else:
                    #save the information on this student's answer
                    correct_question_per_student[question][username] = 0


                #if the option_id is not in the percent's dictionary add it
                if option_chosen not in percent_options:
                    percent_options[option_chosen] = 1/len(dataset_qi)

                #else update its percentage
                else:
                    percent_options[option_chosen] += 1/len(dataset_qi)
                    if percent_options[option_chosen]>1:
                        #Do not let this percentage to be greater than 1
                        percent_options[option_chosen] = 1

                #Add to the dictionary the percentage of correct answers for this question
            percent_correct_questions[question] = percent_correct_answers

            #Evaluate which kind of connections we wish to analyse: only the True/False or All of them
            if isinstance(correct_answers, bool):

                for j in range(len(dataset_qi)):
                    userj = dataset_qi['username'].iloc[j]

                    #Get the option_id chosen by userj
                    option_chosen_j = dataset_qi['option_id'].iloc[j]

                    #if the answer is correct
                    if dataset_qi['correct'].iloc[j]:
                        value = 1
                    #if the answer is incorrect
                    else:
                        value = 0

                    #if the user is not in the performance's dictionary add it
                    if userj not in students_performance:
                        students_performance[userj] = value

                    #else update its performance
                    else:
                        students_performance[userj] += value

                        #if its response is in accordance with the value of correct_answers, study the following users
                    if dataset_qi['correct'].iloc[j] == correct_answers:

                        #create an edge between every student wich answered after the current one and chose the same option_id
                        for k in range(j+1, len(dataset_qi)):
                            userk = dataset_qi['username'].iloc[k]

                            #Get the option_id chosen by userk
                            option_chosen_k = dataset_qi['option_id'].iloc[k]

                            #if both students chose the same option
                            if option_chosen_j == option_chosen_k:

                                #if the edge already exists, update its weight
                                if connected_students.has_edge(userj, userk):
                                    if p_factor:
                                        connected_students[userj][userk]['weight'] += 1/num_questions * (1 - percent_options[option_chosen_j])
                                    else:
                                        connected_students[userj][userk]['weight'] += 1/num_questions
                                #if the edge does not exist, add it
                                else:
                                    if p_factor:
                                        connected_students.add_weighted_edges_from([(userj, userk, 1/num_questions * (1 - percent_options[option_chosen_j]))])
                                    else:
                                        connected_students.add_weighted_edges_from([(userj, userk, 1/num_questions)])

            elif correct_answers == "All":

                #run then subdataset for question_id=i to create edges between students
                for j in range(len(dataset_qi)):
                    userj = dataset_qi['username'].iloc[j]

                    #Get the option_id chosen by userj
                    option_chosen_j = dataset_qi['option_id'].iloc[j]

                    #if the answer is correct
                    if dataset_qi['correct'].iloc[j]:
                        value = 1
                    #else the answer is incorrect
                    else:
                        value = 0

                    #if the user is not in the performance's dictionary add it
                    if userj not in students_performance:
                        students_performance[userj] = value

                    #else update its performance
                    else:
                        students_performance[userj] += value

                    #create an edge between every student wich answered after the current one and chose the same option_id
                    for k in range(j+1, len(dataset_qi)):
                        userk = dataset_qi['username'].iloc[k]

                        #Get the option_id chosen by userk
                        option_chosen_k = dataset_qi['option_id'].iloc[k]

                        #if both students chose the same option
                        if option_chosen_j == option_chosen_k:

                            #if the edge already exists, update its weight
                            if connected_students.has_edge(userj, userk):
                                if p_factor:
                                    connected_students[userj][userk]['weight'] += 1/num_questions * (1 - percent_options[option_chosen_j])
                                else:
                                    connected_students[userj][userk]['weight'] += 1/num_questions
                            #if the edge does not exist, add it
                            else:
                                if p_factor:
                                    connected_students.add_weighted_edges_from([(userj, userk, 1/num_questions * (1 - percent_options[option_chosen_j])  )])
                                else:
                                    connected_students.add_weighted_edges_from([(userj, userk, 1/num_questions)])

            #Sort the dictionary for each question by student username
            # FIX: A Username may not be transformable into a float. Re 
            # correct_question_per_student[question] = dict(sorted(correct_question_per_student[question].items(), key=lambda item: float(item[0])))

            if realmodelQ:
                #Compute the solution frequency for each question
                sol_freq_per_question[question] = (1/len(correct_question_per_student[question])) * sum([value for value in correct_question_per_student[question].values()])

                #Compute the solution frequency penalized for each question
                if sol_freq_per_question[question] != 1:
                    sol_freq_per_question_penalized[question] = math.log(sol_freq_per_question[question] / (1 - sol_freq_per_question[question] ))
        if realmodelQ:
            #Sort questions by difficulty (easier - solution frequency higher - first)
            sol_freq_per_question = dict(sorted(sol_freq_per_question.items(), key=lambda item: item[1], reverse=True))

        #Increment the value of the rank
        rank += 1

    #Compute the cheating indicators statistics for each student
    score_U1 = {}
    score_U3 = {}
    score_CS = {}
    if realmodelQ:
        for alumn in students:
            #U1 Statistic
            numerator = 0
            #get the sum score for this student
            sum_score = 0
            num_ques = len(sol_freq_per_question)
            ordered_questions = [key for key in sol_freq_per_question.keys()]
            for q in ordered_questions:
                if alumn in correct_question_per_student[q].keys():
                    sum_score += correct_question_per_student[q][alumn]

            for qu in range(num_ques-1):
                for que in range(qu+1,num_ques):
                    if alumn in correct_question_per_student[ordered_questions[qu]].keys() and alumn in correct_question_per_student[ordered_questions[que]].keys():
                        if correct_question_per_student[ordered_questions[qu]][alumn] < correct_question_per_student[ordered_questions[que]][alumn]:
                            numerator += 1
            if sum_score > 0 and sum_score < num_ques:
                score_U1[alumn] = numerator / (sum_score * (num_ques - sum_score))
            else:
                score_U1[alumn] = 0

            #Sort dictionary
            score_U1 = dict(sorted(score_U1.items(), key=lambda item: item[1], reverse=True))


            #U3 Statistic & CS Statistic

            first_term = 0
            first_term_CS = 0
            for w in range(sum_score):
                if ordered_questions[w] in sol_freq_per_question_penalized.keys():
                    first_term += sol_freq_per_question_penalized[ordered_questions[w]]
                first_term_CS += sol_freq_per_question[ordered_questions[w]]

            second_term = 0
            second_term_CS = 0
            third_term_CS = 0
            for y in range(num_ques):
                if alumn in correct_question_per_student[ordered_questions[y]].keys():
                    if ordered_questions[y] in sol_freq_per_question_penalized.keys():
                        second_term += correct_question_per_student[ordered_questions[y]][alumn] * sol_freq_per_question_penalized[ordered_questions[y]]
                    second_term_CS += correct_question_per_student[ordered_questions[y]][alumn] * sol_freq_per_question[ordered_questions[y]]
                    third_term_CS += sol_freq_per_question[ordered_questions[y]]

            third_term = 0
            for x in range(num_ques - sum_score + 1 - 1, num_ques):
                if ordered_questions[x] in sol_freq_per_question_penalized.keys():
                    third_term += sol_freq_per_question_penalized[ordered_questions[x]]

            if sum_score > 0 and sum_score < num_ques:
                score_U3[alumn] = (first_term - second_term) / (first_term - third_term)
            else:
                score_U3[alumn] = 0

            #Sort dictionary
            score_U3 = dict(sorted(score_U3.items(), key=lambda item: item[1], reverse=True))


            if sum_score > 0 and sum_score < num_ques:
                score_CS[alumn] = (num_ques * (first_term_CS - second_term_CS)) / (num_ques * first_term_CS - sum_score * third_term_CS)
            else:
                score_CS[alumn] = 0

            #Sort dictionary
            score_CS = dict(sorted(score_CS.items(), key=lambda item: item[1], reverse=True))

    num_questions_total = np.max([value for value in num_question_participations.values()])
    #Get classification of correct answers (0-20) in the dictionary
    students_performance = {k: round(v/num_questions_total*20,2) for k, v in students_performance.items()}
    #Define node color based on the performance
    color_map = {}
    #Assign color to each node
    for key in students_performance:
        if students_performance[key] >= 19:
            color_map[key] = 'DarkGreen'
        elif students_performance[key] >= 17:
            color_map[key] = 'Green'
        elif students_performance[key] >= 15:
            color_map[key] = 'OliveDrab'
        elif students_performance[key] >= 13:
            color_map[key] = 'ForrestGreen'
        elif students_performance[key] >= 10:
            color_map[key] = 'YellowGreen'
        elif students_performance[key] >= 7:
            color_map[key] = 'GreenYellow'
        else:
            color_map[key] = 'PaleGreen'


    #Get list of graph's edges
    edges_data = list(connected_students.edges.data())


    #Compute students' scores

    #Create dictionary with scores per student (in and out)
    students_score_in = {}
    students_score_out = {}
    for node in connected_students.nodes():
        #List of ingoing weights for this node
        ingoing_edges_weights = [e[2]['weight'] for e in edges_data if e[1] == str(node)]

        #Sort list of weights
        ingoing_edges_weights = sorted(ingoing_edges_weights, reverse=True)

        #Ingoing score (consumption)
        #Get the three highest values of weight
        n_highest_in = ingoing_edges_weights[:n_edges_score]

        #If there are no ingoing edges the score is 0
        if n_highest_in != []:
            students_score_in[node] = sum(n_highest_in)
        else:
            students_score_in[node] = 0


        #List of ingoing weights for this node
        outgoing_edges_weights = [e[2]['weight'] for e in edges_data if e[0] == str(node)]

        #Sort list of weights
        outgoing_edges_weights = sorted(outgoing_edges_weights, reverse=True)

        #Outgoing score (sharing)
        #Get the three highest values of weight
        n_highest_out = outgoing_edges_weights[:n_edges_score]

        #If there are no ingoing edges the score is 0
        if n_highest_out != []:
            students_score_out[node] = sum(n_highest_out)
        else:
            students_score_out[node] = 0

    #Sort the dictionaries by values
    students_score_in = dict(sorted(students_score_in.items(), key=lambda item: item[1], reverse=True))
    students_score_out = dict(sorted(students_score_out.items(), key=lambda item: item[1], reverse=True))


    return [students_score_in, students_score_out]

