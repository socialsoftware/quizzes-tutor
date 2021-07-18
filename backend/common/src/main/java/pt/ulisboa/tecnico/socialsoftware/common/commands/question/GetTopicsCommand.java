package pt.ulisboa.tecnico.socialsoftware.common.commands.question;

import io.eventuate.tram.commands.common.Command;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;

public class GetTopicsCommand implements Command {

    private TopicListDto topicListDto;

    public GetTopicsCommand() {
    }

    public GetTopicsCommand(TopicListDto topicListDto) {
        this.topicListDto = topicListDto;
    }

    public TopicListDto getTopicListDto() {
        return topicListDto;
    }

    public void setTopicListDto(TopicListDto topicListDto) {
        this.topicListDto = topicListDto;
    }

    @Override
    public String toString() {
        return "GetTopicsCommand{" +
                "topicListDto=" + topicListDto +
                '}';
    }
}
