package pt.ulisboa.tecnico.socialsoftware.tutor.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.TopicConjunction;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;

import java.util.List;

@Service
public class DemoService {
    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void populateDemo() {
        Integer courseId = courseExecutionService.getDemoCourse().getCourseId();
        Integer courseExecutionId = courseExecutionService.getDemoCourse().getCourseExecutionId();

        Topic softwareArchitectureTopic = new Topic();
        softwareArchitectureTopic.setName("Software Architecture");
        softwareArchitectureTopic.setCourse(courseRepository.findById(courseId).get());
        topicRepository.save(softwareArchitectureTopic);

        Topic softwareEngineeringTopic = new Topic();
        softwareEngineeringTopic.setName("Software Engineering");
        softwareEngineeringTopic.setCourse(courseRepository.findById(courseId).get());
        topicRepository.save(softwareEngineeringTopic);

        List<Question> questions = questionRepository.findQuestions(courseId);
        questions.forEach(question -> {
            question.setStatus(Question.Status.AVAILABLE);
            question.addTopic(softwareEngineeringTopic);
        });

        Assessment assessment = new Assessment();
        assessment.setTitle("Software Engineering Questions");
        assessment.setStatus(Assessment.Status.AVAILABLE);
        assessment.setSequence(1);
        assessment.setCourseExecution(courseExecutionRepository.findById(courseExecutionId).get());
        TopicConjunction topicConjunction = new TopicConjunction();
        topicConjunction.addTopic(softwareEngineeringTopic);
        topicConjunction.setAssessment(assessment);
        assessmentRepository.save(assessment);

        Quiz inClassOneWayQuiz = new Quiz();
        inClassOneWayQuiz.setTitle("In Class Quiz One Way");
        inClassOneWayQuiz.setType(Quiz.QuizType.IN_CLASS.name());
        inClassOneWayQuiz.setCreationDate(DateHandler.now());
        inClassOneWayQuiz.setAvailableDate(DateHandler.now());
        inClassOneWayQuiz.setConclusionDate(DateHandler.now().plusHours(22));
        inClassOneWayQuiz.setCourseExecution(courseExecutionRepository.findById(courseExecutionId).get());
        inClassOneWayQuiz.setOneWay(true);
        inClassOneWayQuiz.setScramble(true);

        Quiz inClassQuiz = new Quiz();
        inClassQuiz.setTitle("In Class Quiz");
        inClassQuiz.setType(Quiz.QuizType.IN_CLASS.name());
        inClassQuiz.setCreationDate(DateHandler.now());
        inClassQuiz.setAvailableDate(DateHandler.now());
        inClassQuiz.setConclusionDate(DateHandler.now().plusHours(22));
        inClassQuiz.setCourseExecution(courseExecutionRepository.findById(courseExecutionId).get());
        inClassQuiz.setScramble(true);

        Quiz proposedQuiz = new Quiz();
        proposedQuiz.setTitle("Teacher Proposed");
        proposedQuiz.setType(Quiz.QuizType.PROPOSED.name());
        proposedQuiz.setCreationDate(DateHandler.now());
        proposedQuiz.setAvailableDate(DateHandler.now());
        proposedQuiz.setCourseExecution(courseExecutionRepository.findById(courseExecutionId).get());
        proposedQuiz.setScramble(true);

        Quiz scrambledQuiz = new Quiz();
        scrambledQuiz.setTitle("Non Scrambled");
        scrambledQuiz.setType(Quiz.QuizType.PROPOSED.name());
        scrambledQuiz.setCreationDate(DateHandler.now());
        scrambledQuiz.setAvailableDate(DateHandler.now());
        scrambledQuiz.setCourseExecution(courseExecutionRepository.findById(courseExecutionId).get());

        questions.forEach(question -> {
            new QuizQuestion(inClassOneWayQuiz, question, inClassOneWayQuiz.getQuizQuestionsNumber());
            new QuizQuestion(inClassQuiz, question, inClassQuiz.getQuizQuestionsNumber());
            new QuizQuestion(proposedQuiz, question, proposedQuiz.getQuizQuestionsNumber());
            new QuizQuestion(scrambledQuiz, question, scrambledQuiz.getQuizQuestionsNumber());
        });

        quizRepository.save(inClassOneWayQuiz);
        quizRepository.save(inClassQuiz);
        quizRepository.save(proposedQuiz);
        quizRepository.save(scrambledQuiz);
    }
}
