package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

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
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class AnswersXmlImport {
	private AnswerService answerService;
	private QuestionRepository questionRepository;
	private QuizRepository quizRepository;
	private UserRepository userRepository;

	private Map<Integer, Map<Integer,Integer>> questionMap;

	public void importAnswers(InputStream inputStream, AnswerService answerService, QuestionRepository questionRepository,
							  QuizRepository quizRepository, UserRepository userRepository) {
		this.answerService = answerService;
		this.questionRepository = questionRepository;
		this.quizRepository = quizRepository;
		this.userRepository = userRepository;

		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);

		Document doc;
		try {
			Reader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
			doc = builder.build(reader);
		} catch (FileNotFoundException e) {
			throw new TutorException(TutorException.ExceptionError.ANSWERS_IMPORT_ERROR, "File not found");
		} catch (JDOMException e) {
			throw new TutorException(TutorException.ExceptionError.ANSWERS_IMPORT_ERROR, "Coding problem");
		} catch (IOException e) {
			throw new TutorException(TutorException.ExceptionError.ANSWERS_IMPORT_ERROR, "File type or format");
		}

		if (doc == null) {
			throw new TutorException(TutorException.ExceptionError.ANSWERS_IMPORT_ERROR, "File not found ot format error");
		}

		loadQuestionMap();

		importQuizAnswers(doc);
	}

	private void loadQuestionMap() {
		questionMap = questionRepository.findAll().stream()
				.collect(Collectors.toMap(Question::getNumber,
						question -> question.getOptions().stream()
								.collect(Collectors.toMap(Option::getNumber,Option::getId))));
	}

	public void importAnswers(String answersXml, AnswerService answerService, QuestionRepository questionRepository,
							  QuizRepository quizRepository, UserRepository userRepository) {
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);

		InputStream stream = new ByteArrayInputStream(answersXml.getBytes());

		importAnswers(stream, answerService, questionRepository, quizRepository, userRepository);
	}

	private void importQuizAnswers(Document doc) {
		XPathFactory xpfac = XPathFactory.instance();
		XPathExpression<Element> xp = xpfac.compile("//quizAnswers/quizAnswer", Filters.element());
		for (Element element : xp.evaluate(doc)) {
			importQuizAnswer(element);
		}
	}

	private void importQuizAnswer(Element answerElement) {
		LocalDateTime answerDate = null;
		if (answerElement.getAttributeValue("answerDate") != null) {
			answerDate = LocalDateTime.parse(answerElement.getAttributeValue("answerDate"));
		}

		Boolean completed = false;
		if (answerElement.getAttributeValue("completed") != null) {
			completed = Boolean.valueOf(answerElement.getAttributeValue("completed"));
		}

		Integer quizNumber = Integer.valueOf(answerElement.getChild("quiz").getAttributeValue("quizNumber"));
		Quiz quiz = quizRepository.findByNumber(quizNumber)
				.orElseThrow(() -> new TutorException(TutorException.ExceptionError.ANSWERS_IMPORT_ERROR,
						"quiz number does not exist " + quizNumber));

		Integer number = Integer.valueOf(answerElement.getChild("user").getAttributeValue("number"));
		User user = userRepository.findByNumber(number);

		QuizAnswer quizAnswer = answerService.createQuizAnswer(user.getId(), quiz.getId());
		quizAnswer.setAnswerDate(answerDate);
		quizAnswer.setCompleted(completed);

		Map<Integer,Integer> mapQuizQuestionId = quiz.getQuizQuestions().stream()
				.collect(Collectors.toMap(QuizQuestion::getSequence, QuizQuestion::getId));

		importQuestionAnswers(answerElement.getChild("questionAnswers"), user, mapQuizQuestionId, quizAnswer);
	}

	private void importQuestionAnswers(Element questionAnswersElement, User user, Map<Integer,Integer> mapQuizQuestionId, QuizAnswer quizAnswer) {
		ResultAnswersDto resultAnswersDto = new ResultAnswersDto();
		resultAnswersDto.setQuizAnswerId(quizAnswer.getId());
		resultAnswersDto.setAnswerDate(quizAnswer.getAnswerDate());
		resultAnswersDto.setAnswers(new ArrayList<>());

		for (Element questionAnswerElement: questionAnswersElement.getChildren("questionAnswer")) {
			LocalDateTime timeTaken = null;
			if (questionAnswerElement.getAttributeValue("timeTaken") != null) {
				timeTaken = LocalDateTime.parse(questionAnswerElement.getAttributeValue("timeTaken"));
			}

			Integer sequence = Integer.valueOf(questionAnswerElement.getChild("quizQuestion").getAttributeValue("sequence"));
			Integer quizQuestionId = mapQuizQuestionId.get(sequence);

			Integer optionId = null;
			if (questionAnswerElement.getChild("option") != null) {
				Integer questionNumber = Integer.valueOf(questionAnswerElement.getChild("option").getAttributeValue("questionNumber"));
				Integer optionNumber = Integer.valueOf(questionAnswerElement.getChild("option").getAttributeValue("number"));
				optionId = questionMap.get(questionNumber).get(optionNumber);
			}

			ResultAnswerDto resultAnswerDto = new ResultAnswerDto();
			resultAnswerDto.setTimeTaken(timeTaken);
			resultAnswerDto.setQuizQuestionId(quizQuestionId);
			resultAnswerDto.setOptionId(optionId);
			resultAnswersDto.getAnswers().add(resultAnswerDto);
		}

		answerService.submitQuestionsAnswers(user, resultAnswersDto);
	}

}
