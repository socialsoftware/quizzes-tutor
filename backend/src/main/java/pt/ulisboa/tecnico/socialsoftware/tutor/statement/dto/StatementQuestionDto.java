package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;

import java.io.Serializable;

import static pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question.QuestionTypes.CODE_FILL_IN;
import static pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = MultipleChoiceStatementQuestionDto.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceStatementQuestionDto.class, name = MULTIPLE_CHOICE_QUESTION),
        @JsonSubTypes.Type(value = CodeFillInStatementQuestionDto.class, name = CODE_FILL_IN)
})
public abstract class StatementQuestionDto implements Serializable {
    private String content;
    private ImageDto image;
    private Integer sequence;

    public StatementQuestionDto() {
    }

    public StatementQuestionDto(QuestionAnswer questionAnswer) {
        this.content = questionAnswer.getQuizQuestion().getQuestion().getContent();
        if (questionAnswer.getQuizQuestion().getQuestion().getImage() != null) {
            this.image = new ImageDto(questionAnswer.getQuizQuestion().getQuestion().getImage());
        }
        this.sequence = questionAnswer.getSequence();
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
}