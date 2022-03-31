package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class SameDifficulty implements DomainEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private DifficultQuestion difficultQuestion;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "same_difficulty_id")
    private Set<DifficultQuestion> difficultQuestions = new HashSet<>();

    public SameDifficulty() {}

    public SameDifficulty(DifficultQuestion difficultQuestion) {
        this.difficultQuestion = difficultQuestion;
    }

    public void remove() {
        difficultQuestions.clear();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DifficultQuestion getDifficultQuestion() {
        return difficultQuestion;
    }

    public void setDifficultQuestion(DifficultQuestion difficultQuestion) {
        this.difficultQuestion = difficultQuestion;
    }

    public Set<DifficultQuestion> getDifficultQuestions() {
        return difficultQuestions;
    }

    public void setDifficultQuestions(Set<DifficultQuestion> difficultQuestions) {
        this.difficultQuestions = difficultQuestions;
    }

    @Override
    public void accept(Visitor visitor) {

    }
}
