package pt.ulisboa.tecnico.socialsoftware.tutor.answer.command;

import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.common.commands.answer.GenerateQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;
import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.NOT_ENOUGH_QUESTIONS_TOURNAMENT;

public class AnswerServiceCommandHandlers {

    private Logger logger = LoggerFactory.getLogger(AnswerServiceCommandHandlers.class);

    @Autowired
    private AnswerService answerService;

    /**
     * Create command handlers.
     *
     * <p>Map each command to different functions to handle.
     *
     * @return The {code CommandHandlers} object.
     */
    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel(ServiceChannels.ANSWER_SERVICE_COMMAND_CHANNEL)
                .onMessage(GenerateQuizCommand.class, this::generateQuiz)
                .build();
    }

    public Message generateQuiz(CommandMessage<GenerateQuizCommand> cm) {
        logger.info("Received GenerateQuizCommand");

        Integer courseExecutionId = cm.getCommand().getCourseExecutionId();
        Integer userId = cm.getCommand().getCreatorId();
        ExternalStatementCreationDto quizForm = cm.getCommand().getQuizForm();

        try {
            Quiz quiz = answerService.generateExternalQuiz(userId, courseExecutionId, quizForm);
            return withSuccess(quiz.getId());
        } catch (Exception e) {
            return withFailure();
        }
    }
}
