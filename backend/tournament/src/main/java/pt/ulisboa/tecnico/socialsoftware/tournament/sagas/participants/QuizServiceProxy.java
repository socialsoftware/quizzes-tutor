package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import pt.ulisboa.tecnico.socialsoftware.common.commands.quiz.DeleteQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.quiz.UpdateQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;

public class QuizServiceProxy {

    public final CommandEndpoint<DeleteQuizCommand> deleteQuiz = CommandEndpointBuilder
            .forCommand(DeleteQuizCommand.class)
            .withChannel(ServiceChannels.QUIZ_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<UpdateQuizCommand> updateQuiz = CommandEndpointBuilder
            .forCommand(UpdateQuizCommand.class)
            .withChannel(ServiceChannels.QUIZ_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

}
