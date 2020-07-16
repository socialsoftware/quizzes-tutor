package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;

import java.io.Serializable;

public class QuestionAnswerDto implements Serializable {
    private QuestionDto question;

    private AnswerTypeDto answer;

    public QuestionAnswerDto() {
    }

    public QuestionAnswerDto(QuestionAnswer questionAnswer) {
        this.question = new QuestionDto(questionAnswer.getQuizQuestion().getQuestion());
        if (questionAnswer.getAnswer() == null) {
            this.answer = questionAnswer.getQuizQuestion().getQuestion().getEmptyAnswerTypeDto();
        } else {
            this.answer = questionAnswer.getAnswer().getAnswerTypeDto();
        }
    }

    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }

    public AnswerTypeDto getAnswer() {
        return answer;
    }

    public void setAnswer(AnswerTypeDto answer) {
        this.answer = answer;
    }
}
