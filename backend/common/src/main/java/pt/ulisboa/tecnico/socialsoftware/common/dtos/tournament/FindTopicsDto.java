package pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament;

import java.util.List;

public class FindTopicsDto {

    private List<TopicWithCourseDto> topicWithCourseDtoList;

    public FindTopicsDto() {
    }

    public FindTopicsDto(List<TopicWithCourseDto> topicWithCourseDtoList) {
        this.topicWithCourseDtoList = topicWithCourseDtoList;
    }

    public List<TopicWithCourseDto> getTopicWithCourseDtoList() {
        return topicWithCourseDtoList;
    }

    public void setTopicWithCourseDtoList(List<TopicWithCourseDto> topicWithCourseDtoList) {
        this.topicWithCourseDtoList = topicWithCourseDtoList;
    }

    @Override
    public String toString() {
        return "FindTopicsDto{" +
                "topicWithCourseDtoList=" + topicWithCourseDtoList +
                '}';
    }
}
