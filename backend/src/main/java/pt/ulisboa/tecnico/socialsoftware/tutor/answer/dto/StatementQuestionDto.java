package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;

import java.io.Serializable;


public class StatementQuestionDto implements Serializable {
    private String content;
    private ImageDto image;
    private Integer sequence;
    private Integer questionId;

    private StatementQuestionDetailsDto questionDetails;

    public StatementQuestionDto(QuestionAnswer questionAnswer, boolean ghost) {
        Question question = questionAnswer.getQuizQuestion().getQuestion();
        if (!ghost) {
            this.content = question.getContent();
            if (question.getImage() != null) {
                this.image = new ImageDto(question.getImage());
            }

            this.questionDetails = question.getStatementQuestionDetailsDto();
        }

        this.sequence = questionAnswer.getSequence();
        this.questionId = question.getId();
    }

    public StatementQuestionDto(Question question) {
        this.content = question.getContent();
        if (question.getImage() != null)
            this.image = new ImageDto(question.getImage());

        this.questionDetails = question.getStatementQuestionDetailsDto();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public StatementQuestionDetailsDto getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(StatementQuestionDetailsDto questionDetails) {
        this.questionDetails = questionDetails;
    }

    @Override
    public String toString() {
        return "StatementQuestionDto{" +
                "content='" + content + '\'' +
                ", image=" + image +
                ", sequence=" + sequence +
                ", questionDetails=" + questionDetails +
                '}';
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }
}