package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "quizzes")
public class Quiz implements Serializable {
   public enum QuizType {
        GENERATED, EXAM, TEST, SINGLE, TEACHER
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Column(name = "generation_date")
    private LocalDateTime date;

    private Integer year;
    private String type;
    private Integer series;
    private String version;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quiz")
    private Set<QuizQuestion> quizQuestions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quiz")
    private Set<QuizAnswer> quizAnswers;

    public Quiz() {}

    public Quiz(QuizDto quiz) {
        this.title = quiz.getTitle();
        this.date = quiz.getDate();
        this.year = quiz.getYear();
        this.type = quiz.getType();
        this.series = quiz.getSeries();
        this.version = quiz.getVersion();
    }

    public void generate(Integer quizSize, List<Question> activeQuestions) {
        Random rand = new Random();
        int numberOfActiveQuestions = activeQuestions.size();
        Set <Integer> usedQuestions = new HashSet<>();

        int next;
        for (int i = 0; i < quizSize; i++) {
            do {
                next = rand.nextInt(numberOfActiveQuestions);
            }
            while (!usedQuestions.contains(next));
            usedQuestions.add(next);

            new QuizQuestion(this, activeQuestions.get(next), i+1);
        }

        this.setDate(LocalDateTime.now());
        this.setType(QuizType.GENERATED.name());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSeries() {
        return series;
    }

    public void setSeries(Integer series) {
        this.series = series;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Set<QuizQuestion> getQuizQuestions() {
        return quizQuestions;
    }

    public void setQuizQuestions(Set<QuizQuestion> quizQuestions) {
        this.quizQuestions = quizQuestions;
    }

    public Set<QuizAnswer> getQuizAnswers() {
        return quizAnswers;
    }

    public void setQuizAnswers(Set<QuizAnswer> quizAnswers) {
        this.quizAnswers = quizAnswers;
    }

    public void addQuizQuestion(QuizQuestion quizQuestion) {
        if (quizQuestions == null) {
            quizQuestions = new HashSet<>();
        }
        this.quizQuestions.add(quizQuestion);
    }

    public void addQuizAnswer(QuizAnswer quizAnswer) {
        if (quizAnswers == null) {
            quizAnswers = new HashSet<>();
        }
        this.quizAnswers.add(quizAnswer);
    }



    public Map<Integer, QuizQuestion> getQuizQuestionsMap() {
        if (quizQuestions == null ) {
            return new HashMap<>();
        }

        return this.quizQuestions.stream().collect(Collectors.toMap(QuizQuestion::getSequence, quizQuestion -> quizQuestion));
    }


}