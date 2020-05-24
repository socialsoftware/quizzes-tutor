package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDtoFactory;

import java.io.Serializable;

public abstract class QuestionAnswerDto implements Serializable {
    private QuestionDto question;


    public QuestionAnswerDto() {}

    public QuestionAnswerDto(QuestionAnswer questionAnswer) {
        this.question = QuestionDtoFactory.getQuestionDto(questionAnswer.getQuizQuestion().getQuestion());
    }

    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }
}
