package com.example.tutor.question;

import com.example.tutor.image.Image;
import com.example.tutor.option.Option;
import com.example.tutor.quiz.QuizQuestion;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity(name = "Question")
@Table(name = "questions")
public class Question implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private String content;
    private Integer difficulty;
    private Boolean active;

    @OneToMany(cascade = { CascadeType.ALL }, mappedBy = "question")
    private List<Option> options = new ArrayList<>();

    @OneToOne(cascade = { CascadeType.ALL }, mappedBy = "question")
    @JoinColumn(nullable = true)
    private Image image;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question")
    Set<QuizQuestion> quizzes;

    public Question(){}

    public Question(QuestionDTO question) {
        this.content = question.getContent();
        this.difficulty = question.getDifficulty();
        this.active = question.getActive();
       if (question.getImage() != null) {
            setImage(new Image(question.getImage()));
        }
        question.getOptions().stream().map(Option::new).forEach(option -> this.options.add(option));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
   }

    public Integer getCorrectOption() {
        Optional<Option> correctOption = this.getOptions().stream().filter(Option::getCorrect).findAny();
        return correctOption.get().getOption();
    }
}