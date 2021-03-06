package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.services.remote;

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentTopic;

import java.util.Set;

public interface TopicInterface {
    Set<TournamentTopic> getTournamentTopics(Set<Integer> topicsList);
}
