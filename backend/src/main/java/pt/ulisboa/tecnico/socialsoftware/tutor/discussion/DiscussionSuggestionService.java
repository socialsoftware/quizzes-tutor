package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.DiscussionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicSourceDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.AqgClient;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgDiscussionRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgDiscussionSuggestionDto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DISCUSSION_NOT_FOUND;

/**
 * A draft reply to a student's doubt, for a teacher. The draft is only returned: it is never
 * saved or sent, and the student's name never leaves the Tutor.
 */
@Service
public class DiscussionSuggestionService {
    // what the generation service accepts
    private static final int MAX_TURNS = 20;
    // The service looks up the few most relevant pieces among these; more would only slow it down
    private static final int MAX_CHUNKS = 2000;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private AqgClient aqgClient;

    @Autowired
    private TopicService topicService;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AqgDiscussionSuggestionDto suggestReply(Integer discussionId) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new TutorException(DISCUSSION_NOT_FOUND, discussionId));
        return aqgClient.suggestReply(buildRequest(discussion));
    }

    AqgDiscussionRequest buildRequest(Discussion discussion) {
        Question question = discussion.getQuestion();

        // The doubt is the student's last message; what came before it is the conversation so far
        List<AqgDiscussionRequest.Turn> turns = new ArrayList<>();
        turns.add(new AqgDiscussionRequest.Turn("student", discussion.getMessage()));
        discussion.getReplies().stream()
                .sorted(Comparator.comparing(Reply::getDate, Comparator.nullsFirst(Comparator.naturalOrder())))
                .forEach(reply -> turns.add(new AqgDiscussionRequest.Turn(
                        reply.getUser().isTeacher() ? "teacher" : "student", reply.getMessage())));

        int doubt = turns.size() - 1;
        while (doubt > 0 && !"student".equals(turns.get(doubt).role()))
            doubt--;
        String doubtMessage = turns.get(doubt).message();
        List<AqgDiscussionRequest.Turn> before = new ArrayList<>(turns.subList(0, doubt));
        if (before.size() > MAX_TURNS)
            before = new ArrayList<>(before.subList(before.size() - MAX_TURNS, before.size()));

        List<AqgDiscussionRequest.Option> options = new ArrayList<>();
        QuestionDetails details = question.getQuestionDetails();
        if (details instanceof MultipleChoiceQuestion multipleChoice) {
            multipleChoice.getOptions().stream()
                    .sorted(Comparator.comparing(option -> option.getSequence() == null ? 0 : option.getSequence()))
                    .forEach(option -> options.add(new AqgDiscussionRequest.Option(option.getContent(), option.isCorrect())));
        }

        // The question's course, which owns the topics the material sections come from
        return new AqgDiscussionRequest(
                question.getCourse().getId(),
                question.getContent(),
                options,
                "",
                chosenOption(discussion),
                doubtMessage,
                before,
                chunkIds(question));
    }

    private String chosenOption(Discussion discussion) {
        if (discussion.getQuestionAnswer() == null)
            return null;
        AnswerDetails answer = discussion.getQuestionAnswer().getAnswerDetails();
        if (answer instanceof MultipleChoiceAnswer multipleChoice && multipleChoice.getOption() != null)
            return multipleChoice.getOption().getContent();
        return null;
    }

    /** The pieces of documents under the question's topics and their subtopics. */
    private List<String> chunkIds(Question question) {
        Set<String> chunkIds = new LinkedHashSet<>();
        for (Topic topic : question.getTopics()) {
            for (TopicSourceDto source : topicService.findSourcesOfSubtree(topic.getId())) {
                if (chunkIds.size() >= MAX_CHUNKS)
                    return new ArrayList<>(chunkIds);
                chunkIds.add(source.getChunkId());
            }
        }
        return new ArrayList<>(chunkIds);
    }
}
