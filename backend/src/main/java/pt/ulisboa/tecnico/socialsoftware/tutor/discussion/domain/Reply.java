package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ReplyDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.REPLY_MISSING_DATA;

@Entity
@Table(name = "replies")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Discussion discussion;

    @NotNull
    @ManyToOne
    private User user;

    @NotNull
    @Column(name="message")
    private String message;

    @Column(name="date")
    private LocalDateTime date;

    public Reply(){}

    public Reply(User user, ReplyDto replyDto, Discussion discussion){
        checkEmptyMessage(replyDto);
        this.user = user;
        user.addReply(this);
        this.date = DateHandler.toLocalDateTime(replyDto.getDate());
        this.message = replyDto.getMessage();
        this.discussion = discussion;
        discussion.addReply(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Discussion getDiscussion() {
        return discussion;
    }

    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void checkEmptyMessage(ReplyDto reply) {
        if(reply.getMessage().trim().length() == 0){
            throw new TutorException(REPLY_MISSING_DATA);
        }
    }

    public void remove() {
        user.getReplies().remove(this);
        user = null;

        discussion.getReplies().remove(this);
        discussion = null;
    }
}
