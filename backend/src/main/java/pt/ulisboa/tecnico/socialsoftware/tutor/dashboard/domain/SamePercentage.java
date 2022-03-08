package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import javax.persistence.*;

@Entity
public class SamePercentage implements DomainEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private WeeklyScore weeklyScore;

    public SamePercentage(WeeklyScore weeklyScore) {
        this.weeklyScore = weeklyScore;
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

    @Override
    public void accept(Visitor visitor) {

    }
}
