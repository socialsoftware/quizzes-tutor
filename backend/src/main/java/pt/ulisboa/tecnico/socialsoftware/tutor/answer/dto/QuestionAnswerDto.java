package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;

import java.io.Serializable;

public class QuestionAnswerDto implements Serializable {
    private QuestionDto question;
    private OptionDto option;


    public QuestionAnswerDto() {}

    public QuestionAnswerDto(QuestionAnswer questionAnswer) {
        this.question = new QuestionDto(questionAnswer.getQuizQuestion().getQuestion());

        if (questionAnswer.getOption() != null)
            this.option = new OptionDto(questionAnswer.getOption());
    }

    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }

    public OptionDto getOption() {
        return option;
    }

    public void setOption(OptionDto option) {
        this.option = option;
    }
}
