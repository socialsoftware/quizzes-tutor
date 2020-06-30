package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MultipleChoiceCorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MultipleChoiceQuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.MultipleChoiceStatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.MultipleChoiceStatementQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuestionDto;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_OPTION_MISMATCH;

@Entity
@DiscriminatorValue(Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION)
public class MultipleChoiceQuestionAnswer extends QuestionAnswer {

    @ManyToOne
    @JoinColumn(name = "option_id")
    private Option option;

    public MultipleChoiceQuestionAnswer() {

    }

    public MultipleChoiceQuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, Integer timeTaken, Option option, int sequence) {
        super(quizAnswer, quizQuestion, timeTaken, sequence);
        setOption(option);
    }

    public MultipleChoiceQuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, int sequence) {
        super(quizAnswer, quizQuestion, sequence);
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;

        if (option != null)
            option.addQuestionAnswer(this);
    }

    @Override
    public MultipleChoiceQuestion getQuestion() {
        return (MultipleChoiceQuestion) getQuizQuestion().getQuestion();
    }

    @Override
    public boolean isCorrect() {
        return getOption() != null && getOption().getCorrect();
    }

    @Override
    public void setResponse(StatementAnswerDto statementAnswerDto) {
        if (statementAnswerDto instanceof MultipleChoiceStatementAnswerDto) {
            MultipleChoiceStatementAnswerDto multipleChoiceStatementAnswerDto = (MultipleChoiceStatementAnswerDto) statementAnswerDto;
            if (multipleChoiceStatementAnswerDto.getOptionId() != null) {

                Option option = this.getQuestion().getOptions().stream()
                        .filter(option1 -> option1.getId().equals(multipleChoiceStatementAnswerDto.getOptionId()))
                        .findAny()
                        .orElseThrow(() -> new TutorException(QUESTION_OPTION_MISMATCH, multipleChoiceStatementAnswerDto.getOptionId()));

                if (this.getOption() != null) {
                    this.getOption().getQuestionAnswers().remove(this);
                }

                this.setOption(option);
            } else {
                this.setOption(null);
            }
        }
    }

    @Override
    public QuestionAnswerDto getQuestionAnswerDto() {
        return new MultipleChoiceQuestionAnswerDto(this);
    }

    @Override
    public CorrectAnswerDto getCorrectAnswerDto() {
        return new MultipleChoiceCorrectAnswerDto(this);
    }

    @Override
    public StatementAnswerDto getStatementAnswerDto() {
        return new MultipleChoiceStatementAnswerDto(this);
    }

    @Override
    public StatementQuestionDto getStatementQuestionDto() {
        return new MultipleChoiceStatementQuestionDto(this);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionAnswer(this);
    }


    @Override
    public void remove() {
        super.remove();
        if (option != null) {
            option.getQuestionAnswers().remove(this);
            option = null;
        }
    }
}
