package com.example.tutor.quiz;

import com.example.tutor.ResourceNotFoundException;
import com.example.tutor.question.Question;
import com.example.tutor.question.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

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

    @ManyToMany
    @JoinTable(
            name="quiz_has_question",
            joinColumns=@JoinColumn(name="quiz_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="question_id", referencedColumnName="id"))
    private List<Question> questions;


    public Quiz() {}

    public Quiz(QuestionRepository questionRepository, Integer userId, Integer numberOfQuestions, String topic, String questions) {
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
        this.series = userId;
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

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}