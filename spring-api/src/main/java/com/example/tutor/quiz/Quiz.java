package com.example.tutor.quiz;

import com.example.tutor.question.Question;
import org.springframework.web.client.HttpServerErrorException;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

@Entity
@Table(name = "Quizzes")
public class Quiz implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(columnDefinition = "title")
    private String title;

    @Column(columnDefinition = "date")
    private LocalDateTime date;

    private Question[] questions;

    public Quiz() {
        int maxID = 1698;
        List<Question> questionList = new ArrayList<>();
        Random rand = new Random();
        HashSet<Integer> idList = new HashSet<>();
        Question q;

        while (questionList.size() < 30) {
            int n = rand.nextInt(maxID);
            try {
                if (!idList.contains(n)) {
                    questionList.add(new Question());
                    idList.add(n);
                }
            } catch (HttpServerErrorException e) {
                idList.add(n);
            }
        }

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