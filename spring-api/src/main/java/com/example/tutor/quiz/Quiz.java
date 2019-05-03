package com.example.tutor.quiz;

import com.example.tutor.ResourceNotFoundException;
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

    private Question[] questions;


    public Quiz(QuestionRepository questionRepository) {
        int maxID = 1698;
        List<Question> questionList = new ArrayList<>();
        Random rand = new Random();
        HashSet<Integer> idList = new HashSet<>();
        Question q;

        while (questionList.size() < 30) {
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

        this.questions = questionList.toArray(new Question[30]);
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

    public Question[] getQuestions() {
        return questions;
    }

    public void setQuestions(Question[] questions) {
        this.questions = questions;
    }
}