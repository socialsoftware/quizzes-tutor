package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;

import java.io.Serializable;

public class QuestionAnswerDto implements Serializable {
    private QuestionDto question;

    private AnswerDetailsDto answerDetails;

    public QuestionAnswerDto() {
    }

    public QuestionAnswerDto(QuestionAnswer questionAnswer) {
        this.question = new QuestionDto(questionAnswer.getQuizQuestion().getQuestion());
        if (questionAnswer.getAnswerDetails() == null) {
            this.answerDetails = questionAnswer.getQuizQuestion().getQuestion().getEmptyAnswerDetailsDto();
        } else {
            this.answerDetails = questionAnswer.getAnswerDetails().getAnswerDetailsDto();
        }
    }

    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }

    public AnswerDetailsDto getAnswerDetails() {
        return answerDetails;
    }

    public void setAnswerDetails(AnswerDetailsDto answerDetails) {
        this.answerDetails = answerDetails;
    }
}
