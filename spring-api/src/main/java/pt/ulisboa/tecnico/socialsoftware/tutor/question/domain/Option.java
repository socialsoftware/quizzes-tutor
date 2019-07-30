package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "options")
public class Option implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer option;
    private Boolean correct = false;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quizAnswer")
    private Set<QuestionAnswer> questionAnswers;

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

    public Set<QuestionAnswer> getQuestionAnswers() {
        return questionAnswers;
    }

    public void setQuestionAnswers(Set<QuestionAnswer> questionAnswers) {
        this.questionAnswers = questionAnswers;
    }

    public void addQuestionAnswer(QuestionAnswer questionAnswer) {
        if (questionAnswers == null) {
            questionAnswers = new HashSet<>();
        }
        questionAnswers.add(questionAnswer);
    }

}