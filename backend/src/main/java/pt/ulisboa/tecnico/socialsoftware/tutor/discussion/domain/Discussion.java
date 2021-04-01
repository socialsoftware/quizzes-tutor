package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Entity
@Table(name = "discussions")
public class Discussion implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(optional=false, fetch = FetchType.EAGER)
    @JoinColumn(name="questionAnswer_id")
    private QuestionAnswer questionAnswer;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="user_id")
    private User user;

    @NotNull
    @Column(name="message", columnDefinition="text")
    private String message;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="question_id")
    private Question question;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="course_execution_id")
    private CourseExecution courseExecution;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "discussion", orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    private LocalDateTime date;

    @Column(columnDefinition = "boolean default false")
    private boolean closed;

    public Discussion(){}

    public Discussion(QuestionAnswer questionAnswer, DiscussionDto discussionDto) {
        checkConsistentDiscussion(discussionDto);
        checkExistingDiscussion(questionAnswer);
        setUser(questionAnswer.getQuizAnswer().getUser());
        setMessage(discussionDto.getMessage());
        setDate(DateHandler.toLocalDateTime(discussionDto.getDate()));
        setQuestion(questionAnswer.getQuizQuestion().getQuestion());
        setCourseExecution(questionAnswer.getQuizAnswer().getQuiz().getCourseExecution());
        setClosed(discussionDto.isClosed());
        setQuestionAnswer(questionAnswer);
    }

    public Integer getId() {
        return id;
    }

    public QuestionAnswer getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(QuestionAnswer questionAnswer) {
        this.questionAnswer = questionAnswer;
        if (questionAnswer != null) {
            this.questionAnswer.setDiscussion(this);
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.user.addDiscussion(this);
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
        this.question.addDiscussion(this);
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
        this.courseExecution.addDiscussion(this);
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isClosed() {
        return closed;
    }

    public void addReply(Reply reply){
        this.replies.add(reply);
    }

    public void removeReply(Reply reply){
        this.replies.remove(reply);
    }

    private void checkConsistentDiscussion(DiscussionDto discussionDto) {
        if (discussionDto.getMessage() == null || discussionDto.getMessage().trim().length() == 0){
            throw new TutorException(DISCUSSION_MISSING_MESSAGE);
        }
        if (discussionDto.getDate() == null || discussionDto.getDate().trim().length() == 0){
            throw new TutorException(DISCUSSION_DATE_MISSING);
        }
    }

    public void changeStatus() {
        if ((getReplies().isEmpty() || getReplies() == null) && !isClosed()){
            throw new TutorException(CLOSE_NOT_POSSIBLE);
        }

        this.closed = !this.closed;
    }

    public boolean teacherAnswered() {
        return this.getReplies().stream().anyMatch(reply -> reply.getUser().isTeacher());
    }

    public void remove() {
        user.getDiscussions().remove(this);
        user = null;

        courseExecution.getDiscussions().remove(this);
        courseExecution = null;

        question.getDiscussions().remove(this);
        question = null;

        questionAnswer.setDiscussion(null);
        questionAnswer = null;

        replies.forEach(Reply::remove);
    }

    private void checkExistingDiscussion(QuestionAnswer questionAnswer) {
        if (questionAnswer.getDiscussion() != null) {
            throw new TutorException(DUPLICATE_DISCUSSION);
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitDiscussion(this);
    }
}
