package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TournamentTopic {

    @Column(name = "tournament_topic_id")
    private Integer topicId;

    @Column(name = "tournament_topic_name")
    private String topicName;

    public TournamentTopic() {
    }

    public TournamentTopic(Integer topicId, String topicName) {
        this.topicId = topicId;
        this.topicName = topicName;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public String getTopicName() {
        return topicName;
    }
}
