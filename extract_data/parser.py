import os, re, codecs
import sys

timePerQuestion = "30"

def main():

	fileName = sys.argv[1]
	questions = extract(fileName)
	result = []
	#Head - prepared for Quizizz format 
	print("Question,Option 1,Option 2,Option 3,Option 4,Correct Answer,Time")
	processedQuestions = processQuestions(questions)
	for pQ in processedQuestions:
		result.append(pQ)
	for r in result:
		print(r)


def extract(nameFile):
	file = open(nameFile, 'rU')
	sentencesList = []
	for line in file:
		sentencesList.append(line)
	file.close()
	sentences = ''.join(sentencesList)
	sentences = sentences.replace("\n", '')
	#get only the question and answers
	questions = re.findall('begin\{ClosedQuestion\}.*?end\{ClosedQuestion\}', sentences)
	return questions

def processQuestions(text):
	lines = []
	for q in text:
		quotes = "\""
		#get question
		quest = re.match('begin\{ClosedQuestion\}(.*?)(\\\\option.*?){4}', q)
		if quest is not None:
			lines.append(breakQuestion(quest, q, 4))
		if quest is None:
			#some questions are formated differently
			quest = re.match('begin\{ClosedQuestion\}(.*?)(\\\\option.*?){3}.*?\\\\end\{ClosedQuestion', q)
			if quest is not None:
				lines.append(breakQuestion(quest, q, 3))
			if quest is None:
				print(q) #for debug
		

	return lines

def breakQuestion(quest, q, number):
	l = []
	question = quest.group(1)
	question = removeFormat(question)
	#put quotation marks so the commas of the text are not mistaken in the csv
	question = "\"" + question + "\""
	l.append(question)
	#get options
	optionA = re.search('\\\\optionA?(.*?)\\\\optionB?', q)
	optionA = getGroup(optionA, 1)
	if (number == 4):
		optionB = re.search('(\\\\option[ABCD]?.*?){1}\\\\option[ABCD]?(.*?)(\\\\option.*?){2}', q)
		optionC = re.search('(\\\\option[ABCD]?.*?){2}\\\\option[ABCD]?(.*?)(\\\\option.*?){1}', q)
		optionD = re.search("(\\\\option[ABCD]?.*?){3}\\\\option[ABCD]?(.*?)\\\\end", q)	
		optionD = getGroup(optionD, 2)
	if (number == 3):
		optionB = re.search('(\\\\option[ABCD]?.*?){1}\\\\option[ABCD]?(.*?)(\\\\option.*?){1}', q)
		optionC = re.search('(\\\\option[ABCD]?.*?){2}\\\\option[ABCD]?(.*?)(\\\\end)', q)
		optionD = "null"
	optionB = getGroup(optionB, 2)
	optionC = getGroup(optionC, 2)
	l.append(",")
	l.append(optionA)
	l.append(",")
	l.append(optionB)
	l.append(",")
	l.append(optionC)
	l.append(",")
	l.append(optionD)
	l.append(",")
	#get right answer
	answer = re.search('Resposta: ([abcdABCD])', q)
	if answer is None:
		answer = re.search('Resposta ([abcdABCD])', q)
	if answer is not None:
		answer = answer.group(1)
		if re.search(answer, "[aA]") is not None:
			l.append("1")
		elif re.search(answer, "[bB") is not None:
			l.append("2")
		elif re.search(answer, "[cC]") is not None:
			l.append("3")
		elif re.search(answer, "[dD]") is not None:
			l.append("4")
	else:
		l.append("NULL")
		#print(q) for debug
	l.append("," + timePerQuestion)
	line = ''.join(l)
	return line

def getGroup(option, group):
	option = option.group(group)
	option = removeFormat(option)
	option = "\"" + option + "\""
	return option

#remove text format (bold, italic, etc)
def removeFormat(line):
	line = re.sub("\\\\[a-zA-Z]+?\{", '', line)
	line = re.sub("\\\\begin\{[a-zA-Z]*?\}", '', line)
	line = re.sub("\\\\end\{[a-zA-Z]*?\}", '', line)
	line = re.sub("\\\\[a-zA-Z]*? ", '', line)
	line = re.sub("\\\\putOptions", '', line)
	line = re.sub("\{", '', line)
	line = re.sub("\}", '', line)
	line = re.sub("\\\\", '', line)
	line = re.sub(" +", ' ', line)
	line = re.sub("\"", "\'", line)
	return line

main()
