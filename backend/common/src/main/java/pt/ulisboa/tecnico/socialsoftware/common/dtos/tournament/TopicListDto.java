package pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament;

import java.util.Set;

public class TopicListDto {

    private Set<Integer> topicList;

    public TopicListDto() {
    }

    public TopicListDto(Set<Integer> topicList) {
        this.topicList = topicList;
    }

    public Set<Integer> getTopicList() {
        return topicList;
    }

    public void setTopicList(Set<Integer> topicList) {
        this.topicList = topicList;
    }

    @Override
    public String toString() {
        return "TopicListDto{" +
                "topicList=" + topicList +
                '}';
    }
}
