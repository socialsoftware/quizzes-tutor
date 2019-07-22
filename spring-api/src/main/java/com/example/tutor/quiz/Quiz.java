package com.example.tutor.quiz;

import com.example.tutor.ResourceNotFoundException;
import com.example.tutor.question.Question;
import com.example.tutor.question.QuestionRepository;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question")
    Set<QuizHasQuestion> questions;

    public Quiz() {}

    public Quiz(QuizDTO quiz) {
        this.id = quiz.getId();
        this.title = quiz.getTitle();
        this.date = quiz.getDate();
        this.year = quiz.getYear();
        this.type = quiz.getType();
        this.series = quiz.getSeries();
        this.version = quiz.getVersion();
        this.generated_by = quiz.getGeneratedBy();
        this.completed = quiz.getCompleted();

        this.questions = new HashSet<>();
        for (int i = 0; i < quiz.getQuestions().size(); i ++) {
            this.questions.add(new QuizHasQuestion(this, new Question(quiz.getQuestions().get(i)), i));
        }
    }

    public void generate(QuestionRepository questionRepository, Integer userId, Integer numberOfQuestions, String[] topics, String questionType) {
        Random rand = new Random();
        Map<Integer, Question> questions = new HashMap<>();

        while (questions.size() < numberOfQuestions) {
            int question_id = rand.nextInt(questionRepository.getTotalUniqueQuestions());
            try {
                if (!questions.keySet().contains(question_id)) {
                    questionRepository.findById(question_id)
                            .ifPresent(question -> {
                                questions.put(questions.size(), question);
                            });
                }
            } catch (ResourceNotFoundException e) {

            }
        }

        this.setDate(LocalDateTime.now());
        this.setQuestions(questions);
        this.setType("Generated");
        this.setGeneratedBy(userId);
        this.setCompleted(false);
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

    public Integer getGeneratedBy() {
        return generated_by;
    }

    public void setGeneratedBy(Integer generatedBy) {
        this.generated_by = generatedBy;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public void setQuestions(Map<Integer, Question> questions) {
        this.questions = questions.entrySet().stream().map(entry -> new QuizHasQuestion(this, entry.getValue(), entry.getKey())).collect(Collectors.toSet());
    }

    public Map<Integer, Question> getQuestions() {
        return this.questions.stream().collect(Collectors.toMap(QuizHasQuestion::getSequence, QuizHasQuestion::getQuestion));
    }

    public Set<QuizHasQuestion> getQuizHasQuestion() {
        return this.questions;
    }
}