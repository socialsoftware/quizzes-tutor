package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ReplyDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.DiscussionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
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
                this.entityManager.persist(discussion);
            }
        }

        return new DiscussionDto(discussion);
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

        if (!discussionRepository.findByUserIdQuestionId(user.getId(), question.getId()).isEmpty()) {
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
}
