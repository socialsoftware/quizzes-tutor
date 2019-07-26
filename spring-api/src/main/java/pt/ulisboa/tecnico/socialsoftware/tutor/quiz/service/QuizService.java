package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.ResourceNotFoundException;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException.ExceptionError.QUESTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException.ExceptionError.QUIZ_NOT_FOUND;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @PersistenceContext
    EntityManager entityManager;


    public List<Quiz> findAll(int pageIndex, int pageSize) {
        return quizRepository.findAll(PageRequest.of(pageIndex, pageSize)).getContent();
    }


    public Quiz findById(Integer quizId) {
        return this.quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + quizId));
    }

    @Transactional
    public Quiz create(QuizDto quizDto) {
        Quiz quiz = new Quiz(quizDto);
        entityManager.persist(quiz);
        return quiz;
    }

    @Transactional
    public void addQuestionToQuiz(int questionId, int quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() ->new TutorException(QUIZ_NOT_FOUND, Integer.toString(quizId)));
        Question question = questionRepository.findById(questionId).orElseThrow(() ->new TutorException(QUESTION_NOT_FOUND, Integer.toString(questionId)));

        QuizQuestion quizQuestion = new QuizQuestion(quiz, question, quiz.getQuizQuestionsMap().size());

        entityManager.persist(quizQuestion);
    }

    @Transactional
    public void delete(Integer quizId) {
        entityManager.remove(findById(quizId));
    }

    @Transactional
    public QuizAnswer generateStudentQuiz(User user, Integer quizSize) {
        Quiz quiz = new Quiz();

        // TODO: to include knowhow about the student in the future
        quiz.generate(quizSize, questionRepository.getActiveQuestions());


        QuizAnswer quizAnswer = new QuizAnswer(user, quiz, LocalDateTime.now());

        entityManager.persist(quizAnswer);

        return quizAnswer;
    }
}
