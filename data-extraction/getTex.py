import glob, os, re

from tex2py import tex2py

texFiles = glob.glob(os.getcwd() + '/**/*.tex', recursive=True)
texFiles = sorted(texFiles)

# for texFile in texFiles:
    # from original parser
    # file = open(texFile, 'rU', encoding="latin-1")
    # sentencesList = []
    # for line in file:
    #        sentencesList.append(line)
    # file.close()
    # sentences = ''.join(sentencesList)
    # sentences = sentences.replace("\n", '')
    # #get only the question and answers
    # questions = re.findall('begin\{ClosedQuestion\}.*?end\{ClosedQuestion\}', sentences)
    # print(questions)

    # alternative parser
    # with open(texFile, encoding="latin-1") as f:
    #     file_contents = f.read()
    #
    #     questions = re.findall('begin\{ClosedQuestion\}.*?end\{ClosedQuestion\}', file_contents)
    #     print(questions)
        # \includegraphics[width=12cm]{dvd-autocomplete}

        # print(file_contents)
        # toc = tex2py(file_contents)
        # for branch in toc.branches:
        #     print(branch)
        # print(texFile + " - " + str(toc.branches))

    # with open(texFile, encoding="latin-1") as f:
    #     file_contents = f.read()
    #     toc = tex2py(file_contents)
    #
    #     for branch in toc.branches:
    #         print(branch)
    #
    #     print(texFile + " - " + str(toc.branches))

    # print()

# test file
testFile = '/home/jbgrocha/Projects/questions/2010/primeiro/exame-20110113-companion-pt-a.tex'
questionFile = '/home/jbgrocha/Projects/questions/2010/primeiro/perguntas-escolha-multipla-20110113.tex'

with open(testFile, encoding="latin-1") as f:
    file_contents = f.read()
    toc = tex2py(file_contents)
    print(toc)
