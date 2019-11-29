package pt.ulisboa.tecnico.socialsoftware.tutor.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.QuizzesXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.QuizzesXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.QUESTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.QUIZ_NOT_FOUND;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @PersistenceContext
    EntityManager entityManager;


    public List<QuizDto> findAll(int pageIndex, int pageSize) {
        return quizRepository.findAll(PageRequest.of(pageIndex, pageSize)).getContent()
                .stream().map(quiz -> new QuizDto(quiz, true))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public QuizDto findById(Integer quizId) {
        return this.quizRepository.findById(quizId).map(quiz -> new QuizDto(quiz, true))
                .orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<QuizDto> findAllNonGenerated() {
        Comparator<Quiz> comparator = Comparator.comparing(Quiz::getYear, Comparator.nullsFirst(Comparator.reverseOrder()))
                .thenComparing(Quiz::getSeries, Comparator.nullsFirst(Comparator.reverseOrder()))
                .thenComparing(Quiz::getVersion, Comparator.nullsFirst(Comparator.reverseOrder()));
        return quizRepository.findAllNonGenerated().stream()
                .sorted(comparator)
                .map(quiz -> new QuizDto(quiz, false))
                .collect(Collectors.toList());
    }


    public Integer getMaxQuizNumber() {
        Integer maxQuizNumber = quizRepository.getMaxQuizNumber();
        return maxQuizNumber != null ? maxQuizNumber : 0;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public QuizDto createQuiz(QuizDto quizDto) {
        if (quizDto.getNumber() == null) {
            quizDto.setNumber(getMaxQuizNumber() + 1);
        }
        Quiz quiz = new Quiz(quizDto);

        if (quizDto.getQuestions() != null) {
            for (QuestionDto questionDto : quizDto.getQuestions()) {
                Question question = questionRepository.findById(questionDto.getId())
                        .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionDto.getId()));
                new QuizQuestion(quiz, question, quiz.getQuizQuestions().size());
            }
        }
        quiz.setCreationDate(LocalDateTime.now());
        entityManager.persist(quiz);

        return new QuizDto(quiz, true);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public QuizDto updateQuiz(Integer quizId, QuizDto quizDto) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() ->new TutorException(QUIZ_NOT_FOUND, quizId));

        quiz.setTitle(quizDto.getTitle());
        quiz.setAvailableDate(quizDto.getAvailableDateDate());
        quiz.setConclusionDate(quizDto.getConclusionDateDate());
        quiz.setScramble(quizDto.getScramble());

        Set<QuizQuestion> quizQuestions = new HashSet<>(quiz.getQuizQuestions());

        quizQuestions.forEach(QuizQuestion::remove);
        quizQuestions.forEach(quizQuestion -> entityManager.remove(quizQuestion));

        if (quizDto.getQuestions() != null) {
            for (QuestionDto questionDto : quizDto.getQuestions()) {
                Question question = questionRepository.findById(questionDto.getId())
                        .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionDto.getId()));
                QuizQuestion quizQuestion = new QuizQuestion(quiz, question, quiz.getQuizQuestions().size());
                entityManager.persist(quizQuestion);
            }
        }

        return new QuizDto(quiz, true);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public QuizQuestionDto addQuestionToQuiz(int questionId, int quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() ->new TutorException(QUIZ_NOT_FOUND, quizId));
        Question question = questionRepository.findById(questionId).orElseThrow(() ->new TutorException(QUESTION_NOT_FOUND, questionId));

        QuizQuestion quizQuestion = new QuizQuestion(quiz, question, quiz.getQuizQuestions().size());

        entityManager.persist(quizQuestion);

        return new QuizQuestionDto(quizQuestion);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void removeQuiz(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() ->new TutorException(QUIZ_NOT_FOUND, quizId));

        quiz.checkCanRemove();

        Set<QuizQuestion> quizQuestions = new HashSet<>(quiz.getQuizQuestions());

        quizQuestions.forEach(QuizQuestion::remove);
        quizQuestions.forEach(quizQuestion -> entityManager.remove(quizQuestion));

        entityManager.remove(quiz);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String exportQuizzes() {
        QuizzesXmlExport xmlExport = new QuizzesXmlExport();

        return xmlExport.export(quizRepository.findAll());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void importQuizzes(String quizzesXml) {
        QuizzesXmlImport xmlImport = new QuizzesXmlImport();

        xmlImport.importQuizzes(quizzesXml, this, questionRepository, quizQuestionRepository);
    }

}
