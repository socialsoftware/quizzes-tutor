package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CANNOT_CONCLUDE_QUIZ;

@Entity
public class QuizAnswerItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer quizId;

    private Integer quizAnswerId;

    private LocalDateTime answerDate;

    @Lob
    private String answers;

    public QuizAnswerItem() {
    }

    public QuizAnswerItem(StatementQuizDto statementQuizDto) {
        this.quizId = statementQuizDto.getId();
        this.quizAnswerId = statementQuizDto.getQuizAnswerId();
        ObjectMapper obj = new ObjectMapper();
        try {
            this.answers = obj.writeValueAsString(statementQuizDto.getAnswers());
        } catch (JsonProcessingException e) {
            throw new TutorException(CANNOT_CONCLUDE_QUIZ);
        }
        this.answerDate = DateHandler.now();
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

    public Integer getQuizAnswerId() {
        return quizAnswerId;
    }

    public void setQuizAnswerId(Integer quizAnswerId) {
        this.quizAnswerId = quizAnswerId;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    public List<StatementAnswerDto> getAnswersList() {
        ObjectMapper obj = new ObjectMapper();
        try {
            return obj.readValue(this.getAnswers(), new TypeReference<ArrayList<StatementAnswerDto>>() {
            });
        } catch (JsonProcessingException e) {
            throw new TutorException(CANNOT_CONCLUDE_QUIZ);
        }
    }
}
