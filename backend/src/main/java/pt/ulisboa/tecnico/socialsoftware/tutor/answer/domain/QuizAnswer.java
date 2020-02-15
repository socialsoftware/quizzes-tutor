package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "quiz_answers")
public class QuizAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "answer_date")
    private LocalDateTime answerDate;

    private boolean completed;

    @Column(columnDefinition = "boolean default false")
    private boolean usedInStatistics;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quizAnswer", fetch=FetchType.LAZY, orphanRemoval=true)
    private List<QuestionAnswer> questionAnswers = new ArrayList<>();

    public QuizAnswer() {
    }

    public QuizAnswer(User user, Quiz quiz) {
        this.completed = false;
        this.usedInStatistics = false;
        this.user = user;
        user.addQuizAnswer(this);
        this.quiz = quiz;
        quiz.addQuizAnswer(this);

        List<QuizQuestion> quizQuestions = new ArrayList<>(quiz.getQuizQuestions());
        if (quiz.getScramble()) {
            Collections.shuffle(quizQuestions);
        }

        quizQuestions.forEach(quizQuestion -> new QuestionAnswer(this, quizQuestion, quizQuestion.getSequence()));
    }

    public void remove() {
        user.getQuizAnswers().remove(this);
        user = null;

        quiz.getQuizAnswers().remove(this);
        quiz = null;

        for (QuestionAnswer questionAnswer: getQuestionAnswers()) {
            questionAnswer.remove();
        }

        questionAnswers.clear();
    }

    public boolean canResultsBePublic(CourseExecution courseExecution) {
        return getCompleted() &&
                getQuiz().getCourseExecution() == courseExecution &&
                !(getQuiz().getType().equals(Quiz.QuizType.IN_CLASS) && getQuiz().getConclusionDate().isAfter(LocalDateTime.now()));
    }

    public void calculateStatistics() {
        if (!this.usedInStatistics) {
            user.increaseNumberOfQuizzes(getQuiz().getType());

            getQuestionAnswers().forEach(questionAnswer -> {
                user.increaseNumberOfAnswers(getQuiz().getType());
                if (questionAnswer.getOption() != null && questionAnswer.getOption().getCorrect()) {
                    user.increaseNumberOfCorrectAnswers(getQuiz().getType());
                }

            });

            getQuestionAnswers().forEach(questionAnswer ->
                    questionAnswer.getQuizQuestion().getQuestion().addAnswerStatistics(questionAnswer));

            this.usedInStatistics = true;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isUsedInStatistics() {
        return usedInStatistics;
    }

    public void setUsedInStatistics(boolean usedInStatistics) {
        this.usedInStatistics = usedInStatistics;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<QuestionAnswer> getQuestionAnswers() {
        if (questionAnswers == null) {
            questionAnswers = new ArrayList<>();
        }
        return questionAnswers;
    }

    public void addQuestionAnswer(QuestionAnswer questionAnswer) {
        if (questionAnswers == null) {
            questionAnswers = new ArrayList<>();
        }
        questionAnswers.add(questionAnswer);
    }


}
