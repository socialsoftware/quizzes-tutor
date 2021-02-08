package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.util.Comparator;
import java.util.List;

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
			exportQuiz(element, quiz, quiz.getQuizQuestions());
		}
	}

	private void exportQuiz(Element element, Quiz quiz, List<QuizQuestion> quizQuestions) {
		Element quizElement = new Element("quiz");
		quizElement.setAttribute("courseName",quiz.getCourseExecution().getCourse().getName());
		quizElement.setAttribute("courseType",quiz.getCourseExecution().getCourse().getType().name());
		quizElement.setAttribute("courseExecutionType",quiz.getCourseExecution().getType().name());
		quizElement.setAttribute("acronym",quiz.getCourseExecution().getAcronym());
        quizElement.setAttribute("academicTerm",quiz.getCourseExecution().getAcademicTerm());
        quizElement.setAttribute("key", String.valueOf(quiz.getNonNullKey()));
		quizElement.setAttribute("scramble", String.valueOf(quiz.getScramble()));
		quizElement.setAttribute("qrCodeOnly", String.valueOf(quiz.isQrCodeOnly()));
		quizElement.setAttribute("oneWay", String.valueOf(quiz.isOneWay()));
		quizElement.setAttribute("type", quiz.getType().name());
		quizElement.setAttribute("title", quiz.getTitle());

		if (quiz.getCreationDate() != null)
			quizElement.setAttribute("creationDate", DateHandler.toISOString(quiz.getCreationDate()));
		if (quiz.getAvailableDate() != null)
			quizElement.setAttribute("availableDate", DateHandler.toISOString(quiz.getAvailableDate()));
        if (quiz.getConclusionDate() != null)
            quizElement.setAttribute("conclusionDate", DateHandler.toISOString(quiz.getConclusionDate()));
		if (quiz.getSeries() != null)
			quizElement.setAttribute("series", String.valueOf(quiz.getSeries()));
		if (quiz.getVersion() != null)
			quizElement.setAttribute("version", quiz.getVersion());

		exportQuizQuestions(quizElement, quizQuestions);

		element.addContent(quizElement);
	}

	private void exportQuizQuestions(Element questionElement, List<QuizQuestion> quizQuestions) {
		Element quizQuestionsElement = new Element("quizQuestions");

		quizQuestions.stream()
			.sorted(Comparator.comparing(QuizQuestion::getSequence))
			.forEach(quizQuestion -> exportQuizQuestion(quizQuestionsElement, quizQuestion));

		questionElement.addContent(quizQuestionsElement);
	}

	private void exportQuizQuestion(Element optionsElement, QuizQuestion quizQuestion) {
		Element optionElement = new Element("quizQuestion");

		optionElement.setAttribute("sequence", String.valueOf(quizQuestion.getSequence()));
		optionElement.setAttribute("questionKey", String.valueOf(quizQuestion.getQuestion().getKey()));

		optionsElement.addContent(optionElement);
	}
}
