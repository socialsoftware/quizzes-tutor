package com.example.tutor.quiz;

import com.example.tutor.ResourceNotFoundException;
import com.example.tutor.option.Option;
import com.example.tutor.question.Question;
import com.example.tutor.question.QuestionRepository;
import org.springframework.web.client.HttpServerErrorException;

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

    @ManyToMany
    @JoinTable(
            name="quiz_has_question",
            joinColumns=@JoinColumn(name="quiz_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="question_id", referencedColumnName="id"))
    private List<Question> questions = new ArrayList<>();

    public Quiz(){

    }

    public Quiz(QuestionRepository questionRepository) {
        int maxID = 1698;
        List<Question> questionList = new ArrayList<>();
        Random rand = new Random();
        HashSet<Integer> idList = new HashSet<>();
        Question q;

        while (questionList.size() < 3) {
            int question_id = rand.nextInt(maxID);
            try {
                if (!idList.contains(question_id)) {
                    questionRepository.findById(question_id)
                        .ifPresent(question -> {
                            if(null != question.getImage()){
                                questionList.add(question);
                                idList.add(question_id);
                            }
                        });
                }
            } catch (ResourceNotFoundException e) {
                idList.add(question_id);
            }
        }

        this.date = LocalDateTime.now();

        this.questions = questionList;
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