package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DifficultQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "difficult_question")
public class DifficultQuestion implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "percentage")
    private int percentage;

    @Column(name = "collected")
    private LocalDateTime collected;

    @Column(name = "removed", columnDefinition = "boolean default false")
    private boolean removed = false;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name = "question_id")
    private Question question;


    public DifficultQuestion(){
    }

    public DifficultQuestion(DifficultQuestionDto difficultQuestionDto){
        setPercentage(difficultQuestionDto.getPercentage());
        setCollected(difficultQuestionDto.getCollected());
        setRemoved(difficultQuestionDto.isRemoved());
        setQuestion(difficultQuestionDto.getQuestion());
    }

    public Integer getId() {
        return id;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public LocalDateTime getCollected() {
        return collected;
    }

    public void setCollected(LocalDateTime collected) {
        this.collected = collected;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void update(DifficultQuestionDto difficultQuestionDto) {

        difficultQuestionDto.setPercentage(difficultQuestionDto.getQuestion().getDifficulty());
        difficultQuestionDto.setCollected(LocalDateTime.now());
    }

    @Override
    public void accept(Visitor visitor) {
        // TODO Auto-generated method stub
    }

    @Override
    public String toString() {
        return "DifficultQuestion{" +
                "id=" + id +
                ", percentage=" + percentage +
                ", removed=" + removed +
                ", question=" + question +
                "}";
    }
}