package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Entity
@Table(name = "discussions")
public class Discussion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="question_id", insertable = false, updatable = false)
    private Question question;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id", insertable = false, updatable = false)
    private User user;

    @NotNull
    @Column(name="message")
    private String message;

    @NotNull
    @Column(name="question_id")
    private Integer questionId;

    @NotNull
    @Column(name="user_id")
    private Integer userId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "discussion", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    private LocalDateTime date;

    @Column(columnDefinition = "boolean default false")
    private boolean available;

    public Integer getId() {
        return id;
    }

    public Discussion(){}

    public Discussion(User user, Question question, DiscussionDto discussionDto) {
        checkConsistentDiscussion(discussionDto);
        this.question = question;
        this.user = user;
        this.message = discussionDto.getMessage();
        this.setQuestionId(question.getId());
        this.setUserId(user.getId());
        this.setDate(DateHandler.toLocalDateTime(discussionDto.getDate()));
        this.available = discussionDto.isAvailable();
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    public void addReply(Reply reply){
        this.replies.add(reply);
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    private void checkConsistentDiscussion(DiscussionDto discussionDto) {
        if (discussionDto.getMessage().trim().length() == 0){
            throw new TutorException(DISCUSSION_MISSING_MESSAGE);
        }
        if (discussionDto.getDate() == null){
            throw new TutorException(DISCUSSION_DATE_MISSING);
        }
    }
}
