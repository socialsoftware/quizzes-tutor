package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;

import java.io.Serializable;

public class QuestionAnswerDto implements Serializable {
    private QuestionDto question;

    private AnswerDetailsDto answer;

    public QuestionAnswerDto() {
    }

    public QuestionAnswerDto(QuestionAnswer questionAnswer) {
        this.question = new QuestionDto(questionAnswer.getQuizQuestion().getQuestion());
        if (questionAnswer.getAnswerDetails() == null) {
            this.answer = questionAnswer.getQuizQuestion().getQuestion().getEmptyAnswerTypeDto();
        } else {
            this.answer = questionAnswer.getAnswerDetails().getAnswerTypeDto();
        }
    }

    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }

    public AnswerDetailsDto getAnswer() {
        return answer;
    }

    public void setAnswer(AnswerDetailsDto answer) {
        this.answer = answer;
    }
}
