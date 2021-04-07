package pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament;

import java.util.List;

public class FindTopicsDto {

    private List<TopicWithCourseDto> topicWithCourseDtoList;

    public FindTopicsDto(List<TopicWithCourseDto> topicWithCourseDtoList) {
        this.topicWithCourseDtoList = topicWithCourseDtoList;
    }

    public List<TopicWithCourseDto> getTopicWithCourseDtoList() {
        return topicWithCourseDtoList;
    }
}
