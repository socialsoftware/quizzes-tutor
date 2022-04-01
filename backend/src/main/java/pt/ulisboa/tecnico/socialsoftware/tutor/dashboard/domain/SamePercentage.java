package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class SamePercentage implements DomainEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private WeeklyScore weeklyScore;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "same_percentage_id")
    private Set<WeeklyScore> weeklyScores = new HashSet<>();

    public SamePercentage() {}

    public SamePercentage(WeeklyScore weeklyScore) {
        this.weeklyScore = weeklyScore;
    }

    public void remove() {
        weeklyScores.clear();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public WeeklyScore getWeeklyScore() {
        return weeklyScore;
    }

    public void setWeeklyScore(WeeklyScore weeklyScore) {
        this.weeklyScore = weeklyScore;
    }

    public Set<WeeklyScore> getWeeklyScores() {
        return weeklyScores;
    }

    public void setWeeklyScores(Set<WeeklyScore> weeklyScores) {
        this.weeklyScores = weeklyScores;
    }

    @Override
    public void accept(Visitor visitor) {

    }
}
