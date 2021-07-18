package pt.ulisboa.tecnico.socialsoftware.tutor.question.command;

import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.common.commands.question.GetTopicsCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

public class QuestionServiceCommandHandlers {
    private Logger logger = LoggerFactory.getLogger(QuestionServiceCommandHandlers.class);

    @Autowired
    private TopicService topicService;

    /**
     * Create command handlers.
     *
     * <p>Map each command to different functions to handle.
     *
     * @return The {code CommandHandlers} object.
     */
    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel(ServiceChannels.QUESTION_SERVICE_COMMAND_CHANNEL)
                .onMessage(GetTopicsCommand.class, this::getTopics)
                .build();
    }

    private Message getTopics(CommandMessage<GetTopicsCommand> cm) {
        logger.info("Received GetTopicsCommand");

        TopicListDto topicsList = cm.getCommand().getTopicListDto();
        try {
            FindTopicsDto findTopicsDto = topicService.findTopicById(topicsList.getTopicList());
            return withSuccess(findTopicsDto);
        } catch (Exception e) {
            return withFailure();
        }
    }
}
