package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.util.List;
import java.util.Set;

public class QuizzesXmlExport {
	public String export(List<Quiz> quizzes) {
		Element element = createHeader();

		exportQuizzes(element, quizzes);

		XMLOutputter xml = new XMLOutputter();
		xml.setFormat(Format.getPrettyFormat());

		return xml.outputString(element);
	}

	public Element createHeader() {
		Document jdomDoc = new Document();
		Element rootElement = new Element("quizzes");

		jdomDoc.setRootElement(rootElement);
		return rootElement;
	}

	private void exportQuizzes(Element element, List<Quiz> quizzes) {
		for (Quiz quiz : quizzes) {
			exportQuiz(element, quiz);
		}
	}

	private void exportQuiz(Element element, Quiz quiz) {
		Element quizElement = new Element("quiz");
		quizElement.setAttribute("number", quiz.getNumber().toString());
		quizElement.setAttribute("scramble", String.valueOf(quiz.getScramble()));

		quizElement.setAttribute("title", quiz.getTitle());
		if (quiz.getCreationDate() != null) {
			quizElement.setAttribute("creationDate", quiz.getCreationDate().toString());
		}
		if (quiz.getAvailableDate() != null) {
			quizElement.setAttribute("availableDate", quiz.getAvailableDate().toString());
		}
        if (quiz.getConclusionDate() != null) {
            quizElement.setAttribute("conclusionDate", quiz.getConclusionDate().toString());
        }
		quizElement.setAttribute("year", quiz.getYear().toString());
		quizElement.setAttribute("type", quiz.getType());
		if (quiz.getSeries() != null) {
			quizElement.setAttribute("series", quiz.getSeries().toString());
		}
		if (quiz.getVersion() != null) {
			quizElement.setAttribute("version", quiz.getVersion());
		}

		exportQuizQuestions(quizElement, quiz.getQuizQuestions());

		element.addContent(quizElement);
	}

	private void exportQuizQuestions(Element questionElement, Set<QuizQuestion> quizQuestions) {
		Element quizQuestionsElement = new Element("quizQuestions");

		for (QuizQuestion quizQuestion: quizQuestions) {
			exportQuizQuestion(quizQuestionsElement, quizQuestion);
		}

		questionElement.addContent(quizQuestionsElement);
	}

	private void exportQuizQuestion(Element optionsElement, QuizQuestion quizQuestion) {
		Element optionElement = new Element("quizQuestion");

		optionElement.setAttribute("sequence", quizQuestion.getSequence().toString());
		optionElement.setAttribute("questionNumber", quizQuestion.getQuestion().getNumber().toString());

		optionsElement.addContent(optionElement);
	}

}
