package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "options")
public class Option implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer option;
    private Boolean correct;
    private String content;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;


    public Option(){}

    public Option(OptionDto option) {
        this.content = option.getContent();
        this.correct = option.getCorrect();
    }


    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOption() {
        return option;
    }

    public void setOption(Integer option) {
        this.option = option;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}