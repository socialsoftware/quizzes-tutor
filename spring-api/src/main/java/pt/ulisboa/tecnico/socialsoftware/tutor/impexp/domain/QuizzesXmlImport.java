package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service.QuizService;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

public class QuizzesXmlImport {
	private QuizService quizService;
	private QuestionRepository questionRepository;

	public void importQuizzes(InputStream inputStream, QuizService quizService, QuestionRepository questionRepository) {
		this.quizService = quizService;
		this.questionRepository = questionRepository;

		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);

		Document doc;
		try {
			Reader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
			doc = builder.build(reader);
		} catch (FileNotFoundException e) {
			throw new TutorException(TutorException.ExceptionError.QUIZZES_IMPORT_ERROR, "File not found");
		} catch (JDOMException e) {
			throw new TutorException(TutorException.ExceptionError.QUIZZES_IMPORT_ERROR, "Coding problem");
		} catch (IOException e) {
			throw new TutorException(TutorException.ExceptionError.QUIZZES_IMPORT_ERROR, "File type or format");
		}

		if (doc == null) {
			throw new TutorException(TutorException.ExceptionError.QUIZZES_IMPORT_ERROR, "File not found ot format error");
		}

		importQuizzes(doc);
	}

	public void importQuizzes(String quizzesXml, QuizService quizService, QuestionRepository questionRepository) {
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);

		InputStream stream = new ByteArrayInputStream(quizzesXml.getBytes());

		importQuizzes(stream, quizService, questionRepository);
	}

	private void importQuizzes(Document doc) {
		XPathFactory xpfac = XPathFactory.instance();
		XPathExpression<Element> xp = xpfac.compile("//quizzes/quiz", Filters.element());
		for (Element element : xp.evaluate(doc)) {
			importQuiz(element);
		}
	}

	private void importQuiz(Element quizElement) {
		Integer number = Integer.valueOf(quizElement.getAttributeValue("number"));
		String title = quizElement.getAttributeValue("title");
		LocalDateTime date = null;
		if (quizElement.getAttributeValue("date") != null) {
			date = LocalDateTime.parse(quizElement.getAttributeValue("date"));
		}
		Integer year = Integer.valueOf(quizElement.getAttributeValue("year"));
		String type = quizElement.getAttributeValue("type");
		Integer series = Integer.valueOf(quizElement.getAttributeValue("series"));
		String version = quizElement.getAttributeValue("version");


		QuizDto quizDto = new QuizDto();
		quizDto.setNumber(number);
		quizDto.setTitle(title);
		quizDto.setDate(date);
		quizDto.setYear(year);
		quizDto.setType(type);
		quizDto.setSeries(series);
		quizDto.setVersion(version);

		Quiz quiz = quizService.createQuiz(quizDto);

		importQuizQuestions(quizElement.getChild("quizQuestions"), quiz);
	}

	private void importQuizQuestions(Element quizQuestionsElement, Quiz quiz) {
		for (Element quizQuestionElement: quizQuestionsElement.getChildren("quizQuestion")) {
			Integer sequence = Integer.valueOf(quizQuestionElement.getAttributeValue("sequence"));
			Integer questionNumber = Integer.valueOf(quizQuestionElement.getAttributeValue("questionNumber"));

			Question question = questionRepository.findByNumber(questionNumber)
					.orElseThrow(() -> new TutorException(TutorException.ExceptionError.QUESTION_NOT_FOUND, questionNumber.toString()));

			QuizQuestion quizQuestion = quizService.addQuestionToQuiz(question.getId(), quiz.getId());

			quizQuestion.setSequence(sequence);
		}
	}

}
