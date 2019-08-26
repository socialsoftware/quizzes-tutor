package pt.ulisboa.tecnico.socialsoftware.tutor.impexp;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.service.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class AnswersXmlImport {
	private AnswerService answerService;
	private QuestionRepository questionRepository;
	private QuizRepository quizRepository;
	private UserRepository userRepository;

	public void importAnswers(InputStream inputStream) {
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);

		Document doc;
		try {
			Reader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
			doc = builder.build(reader);
		} catch (FileNotFoundException e) {
			throw new TutorException(TutorException.ExceptionError.ASWERS_IMPORT_ERROR, "File not found");
		} catch (JDOMException e) {
			throw new TutorException(TutorException.ExceptionError.ASWERS_IMPORT_ERROR, "Coding problem");
		} catch (IOException e) {
			throw new TutorException(TutorException.ExceptionError.ASWERS_IMPORT_ERROR, "File type or format");
		}

		if (doc == null) {
			throw new TutorException(TutorException.ExceptionError.ASWERS_IMPORT_ERROR, "File not found ot format error");
		}

		importQuizAnswers(doc);
	}

	public void importAnswers(String answersXml, AnswerService answerService, QuestionRepository questionRepository,
							  QuizRepository quizRepository, UserRepository userRepository) {
		this.answerService = answerService;
		this.questionRepository = questionRepository;
		this.quizRepository = quizRepository;
		this.userRepository = userRepository;

		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);

		InputStream stream = new ByteArrayInputStream(answersXml.getBytes());

		importAnswers(stream);
	}

	private void importQuizAnswers(Document doc) {
		XPathFactory xpfac = XPathFactory.instance();
		XPathExpression<Element> xp = xpfac.compile("//quizAnswers/quizAnswer", Filters.element());
		for (Element element : xp.evaluate(doc)) {
			importQuizAnswer(element);
		}
	}

	private void importQuizAnswer(Element answerElement) {
		LocalDateTime assignedDate = LocalDateTime.parse(answerElement.getAttributeValue("assignedDate"));
		LocalDateTime availableDate = null;
		if (answerElement.getAttributeValue("availableDate") != null) {
			availableDate = LocalDateTime.parse(answerElement.getAttributeValue("availableDate"));
		}
		LocalDateTime answerDate = null;
		if (answerElement.getAttributeValue("answerDate") != null) {
			answerDate = LocalDateTime.parse(answerElement.getAttributeValue("answerDate"));
		}
		Boolean completed = Boolean.valueOf(answerElement.getAttributeValue("completed"));

		Integer quizNumber = Integer.valueOf(answerElement.getChild("quiz").getAttributeValue("quizNumber"));
		Quiz quiz = quizRepository.findByNumber(quizNumber)
				.orElseThrow(() -> new TutorException(TutorException.ExceptionError.ASWERS_IMPORT_ERROR,
						"quiz number does not exist " + quizNumber));

		String username = answerElement.getChild("user").getAttributeValue("username");
		User user = userRepository.findByUsername(username);

		QuizAnswer quizAnswer = answerService.createQuizAnswer(user.getId(), quiz.getId(), availableDate);
		quizAnswer.setAssignedDate(assignedDate);
		quizAnswer.setAnswerDate(answerDate);
		quizAnswer.setCompleted(completed);


		importQuestionAnswers(answerElement.getChild("questionAnswers"), user, quizAnswer);
	}

	private void importQuestionAnswers(Element questionAnswersElement, User user, QuizAnswer quizAnswer) {
		ResultAnswersDto resultAnswersDto = new ResultAnswersDto();
		resultAnswersDto.setQuizAnswerId(quizAnswer.getId());
		resultAnswersDto.setAnswerDate(quizAnswer.getAnswerDate());
		resultAnswersDto.setAnswers(new ArrayList<>());

		for (Element questionAnswerElement: questionAnswersElement.getChildren("questionAnswer")) {
			LocalDateTime timeTaken = LocalDateTime.parse(questionAnswerElement.getAttributeValue("timeTaken"));

			Integer quizNumber = Integer.valueOf(questionAnswerElement.getChild("quizQuestion").getAttributeValue("quizNumber"));
			Integer sequence = Integer.valueOf(questionAnswerElement.getChild("quizQuestion").getAttributeValue("sequence"));
			Quiz quiz = quizRepository.findByNumber(quizNumber).orElse(null);
			QuizQuestion quizQuestion = quiz.getQuizQuestions().stream()
					.filter(quizQuestion1 -> quizQuestion1.getSequence().equals(sequence)).findAny().get();

			Integer questionNumber = Integer.valueOf(questionAnswerElement.getChild("option").getAttributeValue("questionNumber"));
			Integer optionNumber = Integer.valueOf(questionAnswerElement.getChild("option").getAttributeValue("number"));
			Question question = questionRepository.findByNumber(questionNumber).orElse(null);
			Option option = question.getOptions().stream().filter(option1 -> option1.getNumber().equals(optionNumber)).findAny().get();

			ResultAnswerDto resultAnswerDto = new ResultAnswerDto();
			resultAnswerDto.setTimeTaken(timeTaken);
			resultAnswerDto.setQuizQuestionId(quizQuestion.getId());
			resultAnswerDto.setOptionId(option.getId());
			resultAnswersDto.getAnswers().add(resultAnswerDto);
		}

		answerService.submitQuestionsAnswers(user, resultAnswersDto);
	}

}
