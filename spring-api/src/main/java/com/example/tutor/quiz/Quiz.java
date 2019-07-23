package com.example.tutor.quiz;

import com.example.tutor.ResourceNotFoundException;
import com.example.tutor.question.Question;
import com.example.tutor.question.QuestionRepository;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "quizzes")
public class Quiz implements Serializable {

    public enum QuizType {
        GENERATED, EXAM, TEST
    }

    @Autowired
    QuestionRepository questionRepository;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question")
    @JoinColumn(name = "question_id")
    Set<QuizQuestion> questions;

    public Quiz() {}

    public Quiz(QuizDTO quiz) {
        this.title = quiz.getTitle();
        this.date = quiz.getDate();
        this.year = quiz.getYear();
        this.type = quiz.getType();
        this.series = quiz.getSeries();
        this.version = quiz.getVersion();

        this.questions = new HashSet<>();
        for (int i = 0; i < quiz.getQuestions().size(); i ++) {
            this.questions.add(new QuizQuestion(this, new Question(quiz.getQuestions().get(i)), i));
        }
    }

    // TODO verify question is active
    public void generate(Integer userId, Integer quizSize, String[] topics, String questionType) {
        Random rand = new Random();
        Map<Integer, Question> questions = new HashMap<>();
        int numberOfQuestions = questionRepository.getTotalUniqueQuestions();

        while (questions.size() < quizSize) {
            int random_number = rand.nextInt(numberOfQuestions);
            try {
                if (!questions.keySet().contains(random_number)) {
                    questionRepository.findById(random_number)
                            .ifPresent(question -> {
                                questions.put(questions.size(), question);
                            });
                }
            } catch (ResourceNotFoundException e) {

            }
        }

        this.setDate(LocalDateTime.now());
        this.setQuestions(questions);
        this.setType(QuizType.GENERATED.name());

        //TODO create answer
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

    public void setQuestions(Map<Integer, Question> questions) {
        this.questions = questions.entrySet().stream().map(entry -> new QuizQuestion(this, entry.getValue(), entry.getKey())).collect(Collectors.toSet());
    }

    public Map<Integer, Question> getQuestions() {
        return this.questions.stream().collect(Collectors.toMap(QuizQuestion::getSequence, QuizQuestion::getQuestion));
    }

    public Set<QuizQuestion> getQuizQuestion() {
        return this.questions;
    }
}