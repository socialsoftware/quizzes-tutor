package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;

import java.util.List;

public class AnswersXmlExport {

	public static final String SEQUENCE = "sequence";

	public String export(List<QuizAnswer> quizAnswers) {
		Element element = createHeader();

		exportQuizAnswers(element, quizAnswers);

		XMLOutputter xml = new XMLOutputter();
		xml.setFormat(Format.getPrettyFormat());

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

		if (quizAnswer.getAnswerDate() != null) {
			quizAnswerElement.setAttribute("answerDate", String.valueOf(quizAnswer.getAnswerDate()));
		}

		quizAnswerElement.setAttribute("completed", String.valueOf(quizAnswer.isCompleted()));

		Element quizElement = new Element("quiz");
		quizElement.setAttribute("key", String.valueOf(quizAnswer.getQuiz().getKey()));
		quizAnswerElement.addContent(quizElement);

		Element userElement = new Element("user");
		userElement.setAttribute("key", String.valueOf(quizAnswer.getUser().getKey()));
		quizAnswerElement.addContent(userElement);

		exportQuestionAnswers(quizAnswerElement, quizAnswer.getQuestionAnswers());

		element.addContent(quizAnswerElement);
	}

	private void exportQuestionAnswers(Element quizAnswerElement, List<QuestionAnswer> questionAnswers) {
		Element questionAnswersElement = new Element("questionAnswers");

		for (QuestionAnswer questionAnswer: questionAnswers) {
			exportQuestionAnswer(questionAnswersElement, questionAnswer);
		}

		quizAnswerElement.addContent(questionAnswersElement);
	}

	private void exportQuestionAnswer(Element questionAnswersElement, QuestionAnswer questionAnswer) {
		Element questionAnswerElement = new Element("questionAnswer");

		if (questionAnswer.getTimeTaken() != null) {
			questionAnswerElement.setAttribute("timeTaken", String.valueOf(questionAnswer.getTimeTaken()));
		}

        questionAnswerElement.setAttribute(SEQUENCE, String.valueOf(questionAnswer.getSequence()));

		Element quizQuestionElement = new Element("quizQuestion");
		quizQuestionElement.setAttribute("key", String.valueOf(questionAnswer.getQuizQuestion().getQuiz().getKey()));
		quizQuestionElement.setAttribute(SEQUENCE, String.valueOf(questionAnswer.getQuizQuestion().getSequence()));
		questionAnswerElement.addContent(quizQuestionElement);

		if ( questionAnswer.getOption() != null) {
			Element optionElement = new Element("option");
			optionElement.setAttribute("questionKey", String.valueOf(questionAnswer.getOption().getQuestion().getKey()));
			optionElement.setAttribute(SEQUENCE, String.valueOf(questionAnswer.getOption().getSequence()));
			questionAnswerElement.addContent(optionElement);
		}

		questionAnswersElement.addContent(questionAnswerElement);
	}

}
