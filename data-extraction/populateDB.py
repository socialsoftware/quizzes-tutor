import glob, os, re, json, psycopg2, shutil
from pathlib import Path

dataPath = os.getcwd() + '/../data/**/*.tex'
imagesPath = os.getcwd() + '/../node-api/public/images/questions/'
DBNAME = 'tutordb'
DBUSER = 'pedro'
DBPASS = 'foobar123'





# get tex files list
def getTexFiles():
    result = glob.glob(dataPath, recursive=True)
    result = sorted(result)
    return result

# separates the question tex files from the questions tex files
def separateQuestionsFromQuizes(texFiles):
    quizes = []
    questions = []

    for texFile in texFiles:
        if "questions.tex" in texFile:
            questions.append(texFile)

        elif "macros.tex" not in texFile:
            quizes.append(texFile)

    result = {"questions": questions, "quizes": quizes}

    return result

# parse quiz files
# return list of quizes
def parseQuizFiles(quizFiles):
    result = []

    for quizFile in quizFiles:
        result.append(parseQuizFile(quizFile))

    return result

# parse a quiz file
# returns a list of question commands (in regular expression form) and a question file path
def parseQuizFile(quizFile):
    result = {"quizFile": quizFile, "quizTitle": "", "questionsFile": "", "questions": []}

    relativeFilePath = os.path.relpath(quizFile)
    quizTitle = relativeFilePath.replace('/', '-')
    quizTitle = quizTitle.replace('.tex', '')
    result["quizTitle"] = quizTitle

    # open file
    with open(quizFile, encoding="iso-8859-15") as f:
        file_contents = f.read()

        # get questions file
        filepath = os.path.dirname(os.path.abspath(quizFile))

        matches = re.findall('{.*?questions}', file_contents)
        questionsFileRelative = matches[0].replace('{', '').replace('}', '') + ".tex"

        questionsFile = os.path.abspath(os.path.join(filepath, questionsFileRelative))

        result["questionsFile"] = questionsFile

        # get question commands
        # it is weird that I need to match with \\\\ (apparently it is required due to the re library)
        # see https://docs.python.org/3/library/re.html
        questions = re.findall('\\\\q(.*)', file_contents)
        # TODO: modify it so that it extracts only the command name (probably requires an iteration over the findall questions)
        # see extract questionID
        result["questions"] = questions

    return result

def parseQuestionFiles(questionFiles):
    result = []

    for questionFile in questionFiles:
        result.append(parseQuestionFile(questionFile))

    return  result

def parseQuestionFile(questionFile):
    result = {"questionsFile": questionFile, "questions": []}

    with open(questionFile, encoding="iso-8859-15") as f:
        file_contents = f.read()

        questionsTex = re.findall('\\\\newcommand{\\\\q[^\{]*?\}\{[\s]*?\\\\begin\{ClosedQuestion\}[\s\S]*?\\\\end\{ClosedQuestion\}', file_contents)

        result['questions'] = extractQuestions(questionsTex)
        # returns questions

    return result

def extractQuestions(questionsTex):
    result = []

    for questionTex in questionsTex:
        result.append(extractQuestion(questionTex))

    return result

def extractQuestion(questionTex):
    result = {"questionId": "", "content": "", "image": [], "options": []}


    # get question id
    # should append the question file somehow?
    result["questionId"] = extractQuestionId(questionTex)

    result["content"] = extractContent(questionTex)

    result["image"] = extractImage(questionTex)

    result["options"] = extractOptions(questionTex)

    return result;

def extractQuestionId(questionTex):
    result = ''

    pattern = '\\\\newcommand{\\\\q([^\{]*?)\}'
    match = re.search(pattern, questionTex)

    if match:
        result = match.group(1)

    return result.strip()

def extractContent(questionTex):
    result = ''

    # no idea why this pattern is not working
    # \\newcommand{\\q([^\{]*?)\}\{[\s]*?\\begin\{ClosedQuestion\}([\s\S]*?)\\begin\{options\}([\s\S]*?)\\end\{options\}[\s]\\end\{ClosedQuestion\}[\s]*?\}|\\newcommand{\\q([^\{]*?)\}\{[\s]*?\\begin\{ClosedQuestion\}([\s\S]*?)(\\option[\S]\{[\s\S]*?)\\putOptions[\s\S]*?\\end\{ClosedQuestion\}[\s]*?\}
    # pattern = '\\\\begin\{ClosedQuestion\}([\s\S]*?)\\\\begin\{options\}([\s\S]*?)\\\\end\{options\}[\s]*?\\\\end\{ClosedQuestion\}[\s]*?\}'

    pattern = '\\\\begin\{ClosedQuestion\}([\s\S]*?)\\\\begin\{options\}[\s\S]*?\\\\end\{options\}[\s]\\\\end\{ClosedQuestion\}|\\\\begin\{ClosedQuestion\}([\s\S]*?)\\\\option[\S]\{[\s\S]*?\\\\putOptions[\s\S]*?\\\\end\{ClosedQuestion\}'
    match = re.search(pattern, questionTex)

    if match:
        match1 = match.group(1)
        match2 = match.group(2)

        if match1:
            result = match1
        elif match2:
            result = match2

    return result.strip()

def extractImage(questionTex):
    result = []

    # match includegraphics
    pattern = '\\\\includegraphics\[width=(.*)mm\]\{([\S]*?)\}'
    match = re.search(pattern, questionTex)

    if match:
        result.append({'image': match.group(2), 'width': match.group(1)})

    return result

def extractOptions(questionTex):
    result = []

    result += re.findall('\\\\optionA\{([\s\S]*?)\}\s*\\\\optionB', questionTex)
    result += re.findall('\\\\optionB\{([\s\S]*?)\}\s*\\\\optionC', questionTex)
    result += re.findall('\\\\optionC\{([\s\S]*?)\}\s*\\\\optionD', questionTex)
    result += re.findall('\\\\optionD\{([\s\S]*?)\}\s*\\\\putOptions', questionTex)
   
    return result

def getQuizes(quizes, questionsDBs):
    result = []

    for quiz in quizes:
        result.append(getQuiz(quiz, questionsDBs))

    return result

def getQuiz(quiz, questionsDBs):
    result = {"quizFile": quiz["quizFile"], "quizTitle": quiz["quizTitle"], "questions": []}

    # quiz = {"quizFile": quizFile, "questionsFile": "", "questions": []}
    # questions = [{"questionsFile": questionFile, "questions": []}]
    for questionsDB in questionsDBs:
        if os.path.normpath(questionsDB["questionsFile"]) == os.path.normpath(quiz["questionsFile"]):
            result["questions"] = getQuestions(quiz, questionsDB)

    return result

def getQuestions(quiz, questionsDB):
    result = []
    for question in quiz["questions"]:
        result.append(getQuestion(quiz["quizFile"], question, questionsDB))

    return result

def getQuestion(quizFile, questionId, questionsDB):
    result = {}

    for question in questionsDB["questions"]:
        if(question["questionId"].strip() == questionId.strip()):
            result = question
            # get the actual path for the image file
            result["image"] = getImagePath(quizFile, question["image"])
            break

    return result

def getImagePath(quizFile, imageList):
    result = []

    if len(imageList) > 0:
        dirname = os.path.dirname(quizFile)
        filename = imageList[0]['image']
        suffix = '.png'

        imagePath  = Path(dirname).joinpath(filename).with_suffix(suffix)

        # TODO .resolve() to eliminate /../

        # some errors in finding some files (makes resolve not work)
        # - basically the files don't exist (not even as pdf in these directories)
        # error: /home/jbgrocha/Projects/questions/2015/test-3/pipes-and-filters.png
        # error: /home/jbgrocha/Projects/questions/2015/test-3/pipes-and-filters.png
        # error: /home/jbgrocha/Projects/questions/2017/20180112-Exam/1-proxy-server.png
        # error: /home/jbgrocha/Projects/questions/2017/20180112-Exam/1-proxy-server.png

        try:
            imagePath = imagePath.resolve()
        except FileNotFoundError:
            print('error: ' + str(imagePath))

        result.append({'image':str(imagePath), 'width': int(imageList[0]['width'])})

    return result

def populateDB(quizes):

    try:
        conn = psycopg2.connect("dbname='" + DBNAME + "' user='" + DBUSER + "' host='localhost' password='" + DBPASS + "'")
        cur = conn.cursor()

        # quiz = {"quizFile": quiz["quizFile"], "quizTitle": quiz["quizTitle"], "questions": []}
        for quiz in quizes:
            insertQuiz(cur, quiz)
        # Make the changes to the database persistent
        conn.commit()

        # Close communication with the database
        cur.close()
        conn.close()

    except Exception as inst:
        print("I am unable to connect to the database - ", inst)

def insertQuiz(cur, quiz):
    quizTitle = quiz["quizTitle"]
    # insert quiz

    cur.execute("INSERT INTO quizzes (title) VALUES (%s) RETURNING *", [quizTitle])
    quizId = cur.fetchone()[0]

    for question in quiz["questions"]:
        questionId = insertQuestion(cur, question, quizId)
        insertOptions(cur, question["options"], questionId)
      

def insertQuestion(cur, question, quizId):
    # question = {"questionId": "", "content": "", "image": [], "options": []}
    # insert question
    cur.execute("INSERT INTO questions (content, name) VALUES (%s, %s) RETURNING *", [question["content"], question["questionId"]])
    questionId = cur.fetchone()[0]
    # insert quizhasquestion
    cur.execute("INSERT INTO quiz_has_question (quiz_id, question_id) VALUES (%s, %s)", [int(quizId), int(questionId)])

    # image insertion
    if len(question["image"]) > 0:
        imageAbsolutePath = question["image"][0]['image']
        width = question["image"][0]['width']
        serverImageName = getServerImageName(questionId)

        # insert QuestionHasImage
        # "INSERT INTO QuestionHasImage (QuestionID, Url, Width) VALUES (1, '/images/placeholder.svg', 100);"
        cur.execute("INSERT INTO images (question_id, url, width) VALUES (%s, %s, %s)", [int(questionId), serverImageName, width])

        # copy image from imageAbsolutePath to imagesServerPath + imageRelativePath
        copyImageToServer(questionId, imageAbsolutePath, serverImageName)

    return questionId

def getServerImageName(questionId):
    result = str(questionId) + '.png'
    return result

def copyImageToServer(questionId, imagePath, serverImageName):
    
    if not os.path.exists(imagesPath):
        os.makedirs(imagesPath)
        
    destination = imagesPath + serverImageName

    shutil.copy(imagePath, destination)

def insertOptions(cur, options, questionId):
    for i in range(len(options)):
      # insert option
      cur.execute("INSERT INTO options (question_id, option, content) VALUES (%s, %s, %s) RETURNING *", (int(questionId), i, options[i]))
      # not really needed
      # optionId = cur.fetchone()[0]

def main():

    texFiles = getTexFiles()

    texFiles = separateQuestionsFromQuizes(texFiles)
    quizFiles = texFiles["quizes"]
    questionFiles = texFiles["questions"]

    quizes = parseQuizFiles(quizFiles)

    questionsDBs = parseQuestionFiles(questionFiles)

    outputQuizes = getQuizes(quizes, questionsDBs)
    #outputing the data as a json
    #encoding not working
    #should also validate the encoding for the reads
    with open('data.json', 'w', encoding="iso-8859-15") as outfile:
        json.dump(outputQuizes, outfile, indent=4)

    #print(outputQuizes)

    populateDB(outputQuizes)

main()
