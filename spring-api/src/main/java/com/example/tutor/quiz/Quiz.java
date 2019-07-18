package com.example.tutor.quiz;

import com.example.tutor.ResourceNotFoundException;
import com.example.tutor.question.Question;
import com.example.tutor.question.QuestionRepository;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Entity
@Table(name = "quizzes")
public class Quiz implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "title")
    private String title;

    @Column(columnDefinition = "date")
    private LocalDateTime date;

    @Column(columnDefinition = "year")
    private Integer year;

    @Column(columnDefinition = "type")
    private String type;

    @Column(columnDefinition = "series")
    private Integer series;

    @Column(columnDefinition = "version")
    private String version;

    @Column(columnDefinition = "generated_by")
    private Integer generated_by;

    @Column(columnDefinition = "completed")
    private Boolean completed;

    @ManyToMany(fetch=FetchType.EAGER)
    @Fetch (FetchMode.SELECT)
    @JoinTable(
            name="quiz_has_question",
            joinColumns=@JoinColumn(name="quiz_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="question_id", referencedColumnName="id"))
    private List<Question> questions;


    public Quiz() {}

    public Quiz(QuizDTO quiz) {
        this.id = quiz.getId();
        this.title = quiz.getTitle();
        this.date = quiz.getDate();
        this.year = quiz.getYear();
        this.type = quiz.getType();
        this.series = quiz.getSeries();
        this.version = quiz.getVersion();
        this.generated_by = quiz.getGenerated_by();
        this.completed = quiz.getCompleted();
        this.questions = quiz.getQuestions().stream().map(Question::new).collect(Collectors.toList());
    }

    public void generate(QuestionRepository questionRepository, Integer userId, Integer numberOfQuestions, String[] topics, String questionType) {
        int maxID = 1698;
        List<Question> questionList = new ArrayList<>();
        Random rand = new Random();
        HashSet<Integer> idList = new HashSet<>();
        Question q;

        while (questionList.size() < numberOfQuestions) {
            int question_id = rand.nextInt(maxID);
            try {
                if (!idList.contains(question_id)) {
                    questionRepository.findById(question_id)
                            .ifPresent(question -> {
                                questionList.add(question);
                                idList.add(question_id);
                            });
                }
            } catch (ResourceNotFoundException e) {
                idList.add(question_id);
            }
        }

        this.date = LocalDateTime.now();
        this.questions = questionList;
        this.type = "Generated";
        this.generated_by = userId;
        this.completed = false;
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

    public Integer getGenerated_by() {
        return generated_by;
    }

    public void setGenerated_by(Integer generatedBy) {
        this.generated_by = generatedBy;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}