package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.discussion.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.discussion.ReplyDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.DiscussionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ReplyRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.*;

@Service
public class DiscussionService {

    @Autowired
    public DiscussionRepository discussionRepository;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public QuestionRepository questionRepository;

    @Autowired
    public ReplyRepository replyRepository;

    @Autowired
    public QuizAnswerRepository quizAnswerRepository;

    @Autowired
    public QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DiscussionDto createDiscussion(int questionAnswerId, DiscussionDto discussionDto) {
        QuestionAnswer questionAnswer = questionAnswerRepository.findById(questionAnswerId).orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));
        Discussion discussion = new Discussion(questionAnswer, discussionDto);

        discussionRepository.save(discussion);

        return discussion.getDto(false);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DiscussionDto changeStatus(int discussionId) {
        Discussion discussion = discussionRepository
                .findById(discussionId)
                .orElseThrow(() -> new TutorException(DISCUSSION_NOT_FOUND, discussionId));

        discussion.changeStatus();
        return discussion.getDto(false);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DiscussionDto changeReplyAvailability(int replyId) {
        Reply reply = replyRepository
                .findById(replyId)
                .orElseThrow(() -> new TutorException(REPLY_NOT_FOUND, replyId));

        reply.changeAvailability();
        return reply.getDiscussion().getDto(false);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReplyDto addReply(int userId, int discussionId, ReplyDto replyDto) {
        User user = userRepository.findById(userId).orElseThrow(()->new TutorException(USER_NOT_FOUND, userId));

        Discussion discussion = discussionRepository
                .findById(discussionId)
                .orElseThrow(() -> new TutorException(DISCUSSION_NOT_FOUND, discussionId));

        Reply reply = new Reply(user, replyDto, discussion);
        replyRepository.save(reply);

        return reply.getDto();
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<DiscussionDto> findDiscussionsByCourseExecutionId(int courseExecutionId)  {
        return discussionRepository.findDiscussionsByCourseExecution(courseExecutionId).stream().map(discussion -> discussion.getDto(true))
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<DiscussionDto> findOpenDiscussionsByCourseExecutionId(int courseExecutionId)  {
        return discussionRepository.
                findOpenDiscussionsByCourseExecutionId(courseExecutionId).
                stream().map(discussion -> discussion.getDto(true)).collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<DiscussionDto> findByCourseExecutionIdAndUserId(Integer courseExecutionId, Integer userId) {
        return discussionRepository.findDiscussionsByCourseExecutionIdAndUserId(courseExecutionId, userId).stream().map(discussion -> discussion.getDto(true))
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ReplyDto> findClarificationsByQuestionId(Integer questionId) {
        return replyRepository.findClarificationsByQuestionId(questionId).stream().map(Reply::getDto).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DiscussionDto findDiscussionById(Integer discussionId, boolean deep) {
         return discussionRepository.findById(discussionId)
                 .map(discussion -> discussion.getDto(deep))
                 .orElse(null);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoDiscussions() {
        List<Discussion> discussions = discussionRepository.findByExecutionCourseId(courseExecutionService.getDemoCourse().getCourseExecutionId());

        discussions.forEach(discussion -> {
            discussion.remove();
            discussionRepository.delete(discussion);
        });

    }
}
