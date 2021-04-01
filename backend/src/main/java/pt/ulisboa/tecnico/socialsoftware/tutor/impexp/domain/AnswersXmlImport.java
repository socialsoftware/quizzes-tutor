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
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.AnswerDetailsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionDetailsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Component
public class AnswersXmlImport {
    public static final String SEQUENCE = "sequence";
    public static final String OPTION = "option";
    public static final String QUESTION_KEY = "questionKey";

    @Autowired
    private AnswerService answerService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private QuestionDetailsRepository questionDetailsRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private AnswerDetailsRepository answerDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    private Map<Integer, Map<Integer, Integer>> multipleChoiceQuestionMap;

    private Map<Integer, CodeFillInQuestion> codeFillInQuestionMap;

    private Map<Integer, CodeOrderQuestion> codeOrderQuestionMap;

    public void importAnswers(InputStream inputStream) {
        SAXBuilder builder = new SAXBuilder();
        builder.setIgnoringElementContentWhitespace(true);

        Document doc;
        try {
            Reader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
            doc = builder.build(reader);
        } catch (FileNotFoundException e) {
            throw new TutorException(ANSWERS_IMPORT_ERROR, "File not found");
        } catch (JDOMException e) {
            throw new TutorException(ANSWERS_IMPORT_ERROR, "Coding problem");
        } catch (IOException e) {
            throw new TutorException(ANSWERS_IMPORT_ERROR, "File type or format");
        }

        if (doc == null) {
            throw new TutorException(ANSWERS_IMPORT_ERROR, "File not found ot format error");
        }

        loadQuestionMap();

        importQuizAnswers(doc);
    }

    private void loadQuestionMap() {
        multipleChoiceQuestionMap = questionDetailsRepository.findMultipleChoiceQuestionDetails().stream()
                .collect(Collectors.toMap(questionDetails -> questionDetails.getQuestion().getKey(),
                        questionDetails -> questionDetails.getOptions().stream()
                                .collect(Collectors.toMap(Option::getSequence, Option::getId))));
        codeFillInQuestionMap = questionDetailsRepository.findCodeFillInQuestionDetails().stream()
                .collect(Collectors.toMap(questionDetails -> questionDetails.getQuestion().getKey(),
                        questionDetails -> questionDetails));
        codeOrderQuestionMap = questionDetailsRepository.findCodeOrderQuestionDetails().stream()
                .collect(Collectors.toMap(questionDetails -> questionDetails.getQuestion().getKey(),
                        questionDetails -> questionDetails));
    }

    public void importAnswers(String answersXml) {
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
        LocalDateTime answerDate = null;
        if (answerElement.getAttributeValue("answerDate") != null) {
            answerDate = LocalDateTime.parse(answerElement.getAttributeValue("answerDate"));
        }

        boolean completed = false;
        if (answerElement.getAttributeValue("completed") != null) {
            completed = Boolean.parseBoolean(answerElement.getAttributeValue("completed"));
        }

        Element quizElement = answerElement.getChild("quiz");

        String courseName = quizElement.getAttributeValue("courseName");
        String courseType = quizElement.getAttributeValue("courseType");
        String courseExecutionType = quizElement.getAttributeValue("courseExecutionType");
        String acronym = quizElement.getAttributeValue("acronym");
        String academicTerm = quizElement.getAttributeValue("academicTerm");
        Course course = courseRepository.findByNameType(courseName, courseType)
                .orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseName + ":" + courseType));
        CourseExecution courseExecution = course.getCourseExecution(acronym, academicTerm, Course.Type.valueOf(courseExecutionType))
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, acronym));

        Integer quizKey = Integer.valueOf(quizElement.getAttributeValue("key"));
        Quiz quiz = courseExecution.getQuizzes().stream()
                .filter(quiz1 -> quiz1.getKey().equals(quizKey))
                .findAny()
                .orElseThrow(() -> new TutorException(ANSWERS_IMPORT_ERROR,
                        "quiz id does not exist " + quizKey));

        Integer key = Integer.valueOf(answerElement.getChild("user").getAttributeValue("key"));
        User user = userRepository.findByKey(key).orElse(null);

        QuizAnswerDto quizAnswerDto = answerService.createQuizAnswer(user.getId(), quiz.getId());
        QuizAnswer quizAnswer = quizAnswerRepository.findById(quizAnswerDto.getId())
                .orElseThrow(() -> new TutorException(QUIZ_ANSWER_NOT_FOUND, quizAnswerDto.getId()));
        quizAnswer.setAnswerDate(answerDate);
        quizAnswer.setCompleted(completed);

        importQuestionAnswers(answerElement.getChild("questionAnswers"), quizAnswer);
    }

    private void importQuestionAnswers(Element questionAnswersElement, QuizAnswer quizAnswer) {
        for (Element questionAnswerElement : questionAnswersElement.getChildren("questionAnswer")) {
            Integer timeTaken = null;
            if (questionAnswerElement.getAttributeValue("timeTaken") != null) {
                timeTaken = Integer.valueOf(questionAnswerElement.getAttributeValue("timeTaken"));
            }

            int answerSequence = Integer.parseInt(questionAnswerElement.getAttributeValue(SEQUENCE));

            QuestionAnswer questionAnswer = quizAnswer.getQuestionAnswers().stream()
                    .filter(qa -> qa.getSequence().equals(answerSequence)).findAny()
                    .orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, answerSequence));

            questionAnswer.setTimeTaken(timeTaken);


            String questionType = Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION;
            if (questionAnswerElement.getChild("quizQuestion") != null) {
                questionType = Optional.ofNullable(questionAnswerElement.getChild("quizQuestion").getAttributeValue("type")).orElse(questionType);
            }

            switch (questionType) {
                case Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION:
                    importMultipleChoiceXmlImport(questionAnswerElement, questionAnswer);
                    break;
                case Question.QuestionTypes.CODE_FILL_IN_QUESTION:
                    importCodeFillInXmlImport(questionAnswerElement, questionAnswer);
                    break;
                case Question.QuestionTypes.CODE_ORDER_QUESTION:
                    importCodeOrderXmlImport(questionAnswerElement, questionAnswer);
                    break;
                default:
                    throw new TutorException(QUESTION_TYPE_NOT_IMPLEMENTED, questionType);
            }

            questionAnswerRepository.save(questionAnswer);
        }
    }

    private void importCodeOrderXmlImport(Element questionAnswerElement, QuestionAnswer questionAnswer) {
        var slotsElement = questionAnswerElement.getChild("slots");
        if (slotsElement != null){
            Integer questionKey = Integer.valueOf(slotsElement.getAttributeValue(QUESTION_KEY));
            CodeOrderQuestion codeOrderQuestion = codeOrderQuestionMap.get(questionKey);

            CodeOrderStatementAnswerDetailsDto codeOrderStatementAnswerDetailsDto = new CodeOrderStatementAnswerDetailsDto();
            for (var slot: slotsElement.getChildren("slot")) {
                var sequence = Integer.valueOf(slot.getAttributeValue(SEQUENCE));
                var order = Integer.valueOf(slot.getAttributeValue("order"));

                var slotId = codeOrderQuestion.getCodeOrderSlots()
                        .stream()
                        .filter(x -> x.getSequence().equals(sequence))
                        .findAny()
                        .get().getId();
                codeOrderStatementAnswerDetailsDto.getOrderedSlots().add(new CodeOrderSlotStatementAnswerDetailsDto(slotId, order));
            }
            CodeOrderAnswer answer = new CodeOrderAnswer(questionAnswer);
            answer.setOrderedSlots(codeOrderQuestion, codeOrderStatementAnswerDetailsDto);
            questionAnswer.setAnswerDetails(answer);
            answerDetailsRepository.save(answer);
        }
        else{
            questionAnswer.setAnswerDetails((AnswerDetails) null);
        }
    }


    private void importCodeFillInXmlImport(Element questionAnswerElement, QuestionAnswer questionAnswer) {
        var slotsElement = questionAnswerElement.getChild("fillInSpots");
        if (slotsElement != null){
            Integer questionKey = Integer.valueOf(slotsElement.getAttributeValue(QUESTION_KEY));
            var codeFillInQuestion = codeFillInQuestionMap.get(questionKey);

            CodeFillInStatementAnswerDetailsDto codeFillInStatementAnswerDetailsDto = new CodeFillInStatementAnswerDetailsDto();
            for (var slot: slotsElement.getChildren("fillInSpot")) {
                var slotSequence = Integer.valueOf(slot.getAttributeValue("spotSequence"));
                var optionSequence = Integer.valueOf(slot.getAttributeValue("optionSequence"));


                var optionId = codeFillInQuestion.getFillInSpots().stream()
                        .filter(x -> x.getSequence().equals(slotSequence))
                        .flatMap(s -> s.getOptions().stream()
                                .filter(op -> op.getSequence().equals(optionSequence))
                                .map(CodeFillInOption::getId)
                        ).findAny().get();

                var selectOption = new CodeFillInOptionStatementAnswerDto();
                selectOption.setSequence(slotSequence);
                selectOption.setOptionSequence(optionSequence);
                selectOption.setOptionId(optionId);
                codeFillInStatementAnswerDetailsDto.getSelectedOptions().add(selectOption);
            }
            CodeFillInAnswer answer = new CodeFillInAnswer(questionAnswer);
            answer.setFillInOptions(codeFillInQuestion, codeFillInStatementAnswerDetailsDto);
            questionAnswer.setAnswerDetails(answer);
            answerDetailsRepository.save(answer);
        }
        else{
            questionAnswer.setAnswerDetails((AnswerDetails) null);
        }
    }

    private void importMultipleChoiceXmlImport(Element questionAnswerElement, QuestionAnswer questionAnswer) {
        Integer optionId = null;
        if (questionAnswerElement.getChild(OPTION) != null) {
            Integer questionKey = Integer.valueOf(questionAnswerElement.getChild(OPTION).getAttributeValue(QUESTION_KEY));
            Integer optionSequence = Integer.valueOf(questionAnswerElement.getChild(OPTION).getAttributeValue(SEQUENCE));
            optionId = multipleChoiceQuestionMap.get(questionKey).get(optionSequence);
        }

        if (optionId == null) {
            questionAnswer.setAnswerDetails((AnswerDetails) null);
        } else {
            MultipleChoiceAnswer answer
                    = new MultipleChoiceAnswer(questionAnswer, optionRepository.findById(optionId).orElse(null));
            questionAnswer.setAnswerDetails(answer);
            answerDetailsRepository.save(answer);
        }

    }
}
