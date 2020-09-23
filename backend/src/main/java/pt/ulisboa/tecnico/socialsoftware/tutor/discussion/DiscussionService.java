package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ReplyDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.UnansweredDiscussionsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.DiscussionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ReplyRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

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

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DiscussionDto createDiscussion(DiscussionDto discussionDto) {
        checkDiscussionDto(discussionDto);
        User user = userRepository.findById(discussionDto.getUserId()).orElseThrow(() -> new TutorException(USER_NOT_FOUND, discussionDto.getUserId()));
        Question question = questionRepository.findById(discussionDto.getQuestionId()).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, discussionDto.getQuestionId()));

        checkUserAndQuestion(user, question);

        checkMessage(discussionDto.getMessage());

        Discussion discussion = new Discussion(user, question, discussionDto);
        this.entityManager.persist(discussion);
        List<ReplyDto> replies = discussionDto.getReplies();
        if (replies != null && !replies.isEmpty()) {
            for (ReplyDto reply : replies) {
                User student = userRepository.findById(reply.getUserId())
                        .orElseThrow(() -> new TutorException(USER_NOT_FOUND, reply.getUserId()));

                this.entityManager.persist(new Reply(student, reply, discussion));
            }
        }

        return new DiscussionDto(discussion);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public DiscussionDto changeAvailability(int discussionId) {
        Discussion discussion = discussionRepository
                .findById(discussionId)
                .orElseThrow(() -> new TutorException(DISCUSSION_NOT_FOUND, discussionId));
        discussion.changeAvailability();
        return new DiscussionDto(discussion);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ReplyDto createReply(DiscussionDto discussionDto, ReplyDto replyDto) {
        checkDiscussionDto(discussionDto);

        User user = userRepository.findById(replyDto.getUserId()).orElseThrow(()->new TutorException(USER_NOT_FOUND, replyDto.getUserId()));

        checkUserAndDiscussion(user, discussionDto);


        Discussion discussion = discussionRepository
                .findByUserIdQuestionId(discussionDto.getUserId(), discussionDto.getQuestionId())
                .orElseThrow(() -> new TutorException(DISCUSSION_NOT_FOUND, discussionDto.getUserId(),
                        discussionDto.getQuestionId()));


        checkMessage(discussionDto.getMessage());

        Reply reply = new Reply(user, replyDto, discussion);
        this.entityManager.persist(reply);

        return new ReplyDto(reply);
    }

    private void checkUserAndDiscussion(User user, DiscussionDto discussion) {
        if (user.getRole() != User.Role.TEACHER && !user.getId().equals(discussion.getUserId())) {
            throw new TutorException(REPLY_UNAUTHORIZED_USER);
        }
    }

    private void checkMessage(String message) {
        if (message == null || message.trim().length() == 0) {
            throw new TutorException(DISCUSSION_MISSING_MESSAGE);
        }
    }

    private void checkDiscussionDto(DiscussionDto discussion) {
        if (discussion.getQuestion() == null ) {
            throw new TutorException(DISCUSSION_MISSING_QUESTION);
        }
        if(discussion.getUserId() == null){
            throw new TutorException(DISCUSSION_MISSING_USER);
        }
        if(discussion.getMessage() == null || discussion.getMessage().trim().length() == 0){
            throw new TutorException(DISCUSSION_MISSING_MESSAGE);
        }
    }

    private void checkUserAndQuestion(User user, Question question) {
        if (user.getRole() == User.Role.TEACHER) {
            throw new TutorException(DISCUSSION_NOT_STUDENT_CREATOR);
        }

        if (discussionRepository.findByUserIdQuestionId(user.getId(), question.getId()).isPresent()) {
            throw new TutorException(DUPLICATE_DISCUSSION, user.getId(), question.getId());
        }

        checkUserAnswered(user, question);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DiscussionDto> findDiscussionsByQuestionId(Integer questionId) {
        return discussionRepository.findByQuestionId(questionId).stream().map(DiscussionDto::new)
                .collect(Collectors.toList());
    }

    private void checkUserAnswered(User user, Question question) {
        if (!user.checkQuestionAnswered(question)) {
            throw new TutorException(QUESTION_NOT_ANSWERED, question.getId());
        }
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DiscussionDto> findDiscussionsByUserId(Integer userId) {
        return discussionRepository.findByUserId(userId).stream().map(DiscussionDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DiscussionDto> findDiscussionByUserIdAndQuestionId(Integer userId, Integer questionId) {
        return discussionRepository.findByUserIdQuestionId(userId, questionId).stream().map(DiscussionDto::new)
                .collect(Collectors.toList());
    }


    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DiscussionDto> findDiscussionsByCourseExecutionId(int courseExecutionId)  {
        return discussionRepository.findByCourseExecutionId(courseExecutionId).stream().map(DiscussionDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UnansweredDiscussionsDto getUnansweredDiscussionsNumber(int courseExecutionId)  {
        List<DiscussionDto> list = discussionRepository.findByCourseExecutionId(courseExecutionId).stream()
                .filter(discussion -> (discussion.getReplies() == null || discussion.getReplies().isEmpty()))
                .map(DiscussionDto::new)
                .collect(Collectors.toList());

        return new UnansweredDiscussionsDto(list);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DiscussionDto> getAnsweredDiscussions(int courseExecutionId, Integer userId) {
        return discussionRepository.findByCourseExecutionIdAndUserId(courseExecutionId, userId).stream()
                .filter(discussion -> (!discussion.getReplies().isEmpty()))
                .map(DiscussionDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteDiscussion(Discussion discussion) {
        List<Reply> replies = new ArrayList<>(discussion.getReplies());
        replies.forEach(reply ->
        {
            reply.remove();
            replyRepository.delete(reply);
        });
        discussion.remove();
        discussionRepository.delete(discussion);
    }
}
