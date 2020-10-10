package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ReplyDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.DiscussionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ReplyRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.api.TopicController;
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
import java.util.stream.Stream;

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

    @Autowired
    public QuizAnswerRepository quizAnswerRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DiscussionDto createDiscussion(int quizAnswerId, int questionId, DiscussionDto discussionDto) {
        checkDiscussionDto(discussionDto);
        User user = userRepository.findById(discussionDto.getUserId()).orElseThrow(() -> new TutorException(USER_NOT_FOUND, discussionDto.getUserId()));
        Question question = questionRepository.findById(discussionDto.getQuestion().getId()).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, discussionDto.getQuestion().getId()));
        QuizAnswer quizAnswer = quizAnswerRepository.findById(quizAnswerId).orElse(null);

        checkUserAndQuestion(user, question);
        checkMessage(discussionDto.getMessage());
        checkQuizAnswer(quizAnswer);


        List<QuestionAnswer> questionAnswerList = quizAnswer.getQuestionAnswers().stream().filter(qAnswer -> {
            if(qAnswer.getQuizQuestion().getQuestion().getId() == questionId) {
                return true;
            }
            else{
                return false;
            }
        }).collect(Collectors.toList());

        if(questionAnswerList.size() != 1){
            throw new TutorException(MORE_THAN_ONE_QUESTION_ANSWER,questionId);
        }

        QuestionAnswer questionAnswer = questionAnswerList.get(0);
        checkExistingDiscussion(questionAnswer);

        Discussion discussion = new Discussion(user, questionAnswer, discussionDto);
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

    private void checkExistingDiscussion(QuestionAnswer questionAnswer) {
        if (questionAnswer.getDiscussion() != null) {
            throw new TutorException(DUPLICATE_DISCUSSION);
        }
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public DiscussionDto changeStatus(int discussionId) {
        Discussion discussion = discussionRepository
                .findById(discussionId)
                .orElseThrow(() -> new TutorException(DISCUSSION_NOT_FOUND, discussionId));

        if((discussion.getReplies().isEmpty() || discussion.getReplies() == null) && !discussion.isClosed()){
            throw new TutorException(CLOSE_NOT_POSSIBLE);
        }
        discussion.changeStatus();
        return new DiscussionDto(discussion);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public DiscussionDto changeReplyAvailability(int replyId) {
        Reply reply = replyRepository
                .findById(replyId)
                .orElseThrow(() -> new TutorException(REPLY_NOT_FOUND, replyId));

        reply.changeAvailability();
        return new DiscussionDto(reply.getDiscussion());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ReplyDto createReply(DiscussionDto discussionDto, ReplyDto replyDto) {
        checkDiscussionDto(discussionDto);

        User user = userRepository.findById(replyDto.getUserId()).orElseThrow(()->new TutorException(USER_NOT_FOUND, replyDto.getUserId()));

        checkUserAndDiscussion(user, discussionDto);

        Discussion discussion = discussionRepository
                .findById(discussionDto.getId())
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

    private void checkQuizAnswer(QuizAnswer quizAnswer) {
        if(quizAnswer == null || quizAnswer.getQuestionAnswers() == null) {
            throw new TutorException(QUIZ_ANSWER_NOT_FOUND);
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
    public List<DiscussionDto> findDiscussionsByCourseExecutionId(int courseExecutionId)  {
        return discussionRepository.findByCourseExecutionId(courseExecutionId).stream().map(DiscussionDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DiscussionDto> findOpenDiscussionsByCourseExecutionId(int courseExecutionId)  {
        return discussionRepository.findByCourseExecutionId(courseExecutionId).stream().filter(discussion -> !discussion.isClosed())
                .map(DiscussionDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DiscussionDto> getAnsweredDiscussions(int courseExecutionId, Integer userId) {
        return discussionRepository.findByCourseExecutionIdAndUserId(courseExecutionId, userId).stream()
                .filter(discussion -> (!discussion.getReplies().isEmpty()))
                .map(DiscussionDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DiscussionDto> findByCourseExecutionIdAndUserId(Integer courseExecutionId, Integer userId) {
        return discussionRepository.findByCourseExecutionIdAndUserId(courseExecutionId, userId).stream().map(DiscussionDto::new)
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

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ReplyDto> findClarificationsByQuestionId(Integer questionId) {
        List<ReplyDto> newList = new ArrayList<>();

        discussionRepository.findByQuestionId(questionId).stream().filter(Discussion::isAvailable
        ).forEach(discussion -> {
            newList.addAll(discussion.getClarifications().stream().map(ReplyDto::new).collect(Collectors.toList()));
        });

        return newList;
    }
}
