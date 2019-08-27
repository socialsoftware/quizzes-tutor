package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.util.List;
import java.util.Set;

public class AnswersXmlExport {
	public String export(List<QuizAnswer> quizAnswers) {
		Element element = createHeader();

		exportQuizAnswers(element, quizAnswers);

		XMLOutputter xml = new XMLOutputter();
		xml.setFormat(Format.getPrettyFormat());
		System.out.println(xml.outputString(element));

		return xml.outputString(element);
	}

	public Element createHeader() {
		Document jdomDoc = new Document();
		Element rootElement = new Element("quizAnswers");

		jdomDoc.setRootElement(rootElement);
		return rootElement;
	}

	private void exportQuizAnswers(Element element, List<QuizAnswer> quizAnswers) {
		for (QuizAnswer quizAnswer : quizAnswers) {
			exportQuizAnswer(element, quizAnswer);
		}
	}

	private void exportQuizAnswer(Element element, QuizAnswer quizAnswer) {
		Element quizAnswerElement = new Element("quizAnswer");
		quizAnswerElement.setAttribute("assignedDate", quizAnswer.getAssignedDate().toString());
		if (quizAnswer.getAvailableDate() != null) {
			quizAnswerElement.setAttribute("availableDate", quizAnswer.getAvailableDate().toString());
		}
		if (quizAnswer.getAnswerDate() != null) {
			quizAnswerElement.setAttribute("answerDate", quizAnswer.getAnswerDate().toString());
		}

		if (quizAnswer.getCompleted() != null) {
			quizAnswerElement.setAttribute("completed", quizAnswer.getCompleted().toString());
		}

		Element quizElement = new Element("quiz");
		quizElement.setAttribute("quizNumber", quizAnswer.getQuiz().getNumber().toString());
		quizAnswerElement.addContent(quizElement);

		Element userElement = new Element("user");
		userElement.setAttribute("number", quizAnswer.getUser().getNumber().toString());
		quizAnswerElement.addContent(userElement);

		exportQuestionAnswers(quizAnswerElement, quizAnswer.getQuestionAnswers());

		element.addContent(quizAnswerElement);
	}

	private void exportQuestionAnswers(Element quizAnswerElement, Set<QuestionAnswer> questionAnswers) {
		Element questionAnswersElement = new Element("questionAnswers");

		for (QuestionAnswer questionAnswer: questionAnswers) {
			exportQuestionAnswer(questionAnswersElement, questionAnswer);
		}

		quizAnswerElement.addContent(questionAnswersElement);
	}

	private void exportQuestionAnswer(Element questionAnswersElement, QuestionAnswer questionAnswer) {
		Element questionAnswerElement = new Element("questionAnswer");

		if (questionAnswer.getTimeTaken() != null) {
			questionAnswerElement.setAttribute("timeTaken", questionAnswer.getTimeTaken().toString());
		}

		Element quizQuestionElement = new Element("quizQuestion");
		quizQuestionElement.setAttribute("quizNumber", questionAnswer.getQuizQuestion().getQuiz().getNumber().toString());
		quizQuestionElement.setAttribute("sequence", questionAnswer.getQuizQuestion().getSequence().toString());
		questionAnswerElement.addContent(quizQuestionElement);

		if ( questionAnswer.getOption() != null) {
			Element optionElement = new Element("option");
			optionElement.setAttribute("questionNumber", questionAnswer.getOption().getQuestion().getNumber().toString());
			optionElement.setAttribute("number", questionAnswer.getOption().getNumber().toString());
			questionAnswerElement.addContent(optionElement);
		}

		questionAnswersElement.addContent(questionAnswerElement);
	}

}
