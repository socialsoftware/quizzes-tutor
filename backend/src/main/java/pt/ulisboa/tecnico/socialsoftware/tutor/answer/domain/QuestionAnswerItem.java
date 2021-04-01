package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "question_answer_items",
        indexes = {
                @Index(name = "question_answer_items_indx_0", columnList = "quiz_id"),
        })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_answer_type",
        columnDefinition = "varchar(32) not null default 'multiple_choice'",
        discriminatorType = DiscriminatorType.STRING)
public abstract class QuestionAnswerItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "quiz_id")
    private Integer quizId;
    private String username;
    private Integer quizQuestionId;
    private LocalDateTime answerDate;
    private Integer timeTaken;
    private Integer timeToSubmission;

    protected QuestionAnswerItem() {
    }

    protected QuestionAnswerItem(String username, int quizId, StatementAnswerDto answer) {
        this.username = username;
        this.quizId = quizId;
        this.quizQuestionId = answer.getQuizQuestionId();
        this.answerDate = DateHandler.now();
        this.timeTaken = answer.getTimeTaken();
        this.timeToSubmission = answer.getTimeToSubmission();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getQuizQuestionId() {
        return quizQuestionId;
    }

    public void setQuizQuestionId(Integer quizQuestionId) {
        this.quizQuestionId = quizQuestionId;
    }

    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    public Integer getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Integer timeTaken) {
        this.timeTaken = timeTaken;
    }

    public Integer getTimeToSubmission() {
        return timeToSubmission;
    }

    public void setTimeToSubmission(Integer timeToSubmission) {
        this.timeToSubmission = timeToSubmission;
    }

    public abstract String getAnswerRepresentation(QuestionDetails questionDetails);
}
