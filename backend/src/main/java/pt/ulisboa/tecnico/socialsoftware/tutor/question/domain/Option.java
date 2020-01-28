package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "options")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer number;

    @Column(columnDefinition = "boolean default false")
    private boolean correct;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quizAnswer")
    private Set<QuestionAnswer> questionAnswers = new HashSet<>();

    public Option(){}

    public Option(OptionDto option) {
        this.number = option.getNumber();
        this.content = option.getContent();
        this.correct = option.getCorrect();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        if (number == null) {
            getQuestion().setOptionsNumber();
        }
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public boolean getCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        questionAnswers.add(questionAnswer);
    }

    @Override
    public String toString() {
        return "Option{" +
                "id=" + id +
                ", number=" + number +
                ", correct=" + correct +
                ", content='" + content + '\'' +
                '}';
    }
}