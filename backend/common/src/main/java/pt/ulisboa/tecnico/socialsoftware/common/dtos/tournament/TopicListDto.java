package pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament;

import java.util.Set;

public class TopicListDto {

    private Set<Integer> topicList;

    public TopicListDto(Set<Integer> topicList) {
        this.topicList = topicList;
    }

    public Set<Integer> getTopicList() {
        return topicList;
    }
}
