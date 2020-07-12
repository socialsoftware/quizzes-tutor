package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import org.springframework.boot.autoconfigure.condition.ConditionalOnJava;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeFillInCorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeFillInQuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.OPTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_OPTION_MISMATCH;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CODE_FILL_IN)
public class CodeFillInQuestionAnswer extends QuestionAnswer {

    @ManyToMany
    @JoinColumn(name = "options_in_spots_id")
    private List<FillInOption> fillInOptions = new ArrayList<>();

    public CodeFillInQuestionAnswer(){

    }

    public CodeFillInQuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, int sequence) {
        super(quizAnswer,quizQuestion, sequence);
    }

    public List<FillInOption> getFillInOptions() {
        return fillInOptions;
    }

    public void setFillInOptions(List<FillInOption> fillInOptions) {
        this.fillInOptions = fillInOptions;
    }

    /*
    public FillInOption getFillInOption() {
        return fillInOption;
    }

    public void setFillInOption(FillInOption fillInOption) {
        this.fillInOption = fillInOption;

        if (fillInOption != null) {
            //TODO CHECK IT -> fillInOption.addQuestionAnswer(this);
        }
    }*/

    @Override
    public boolean isCorrect() {
        return getFillInOptions() != null &&
                !getFillInOptions().isEmpty() &&
                getFillInOptions().stream().allMatch(FillInOption::isCorrect);
    }

    @Override
    public void setResponse(StatementAnswerDto statementAnswerDto) {
        if (statementAnswerDto instanceof CodeFillInStatementAnswerDto) {
            CodeFillInStatementAnswerDto answer = (CodeFillInStatementAnswerDto) statementAnswerDto;
            if (answer.getSelectedOptions() != null) {
                for (CodeFillInOptionStatementAnswerDto option : answer.getSelectedOptions()) {

                    FillInOption fillInOption = this.getQuestion().getFillInSpots().stream()
                            .map(FillInSpot::getOptions)
                            .flatMap(Collection::stream)
                            .filter(option1 -> option1.getId().equals(option.getOptionId()))
                            .findAny()
                            .orElseThrow(() -> new TutorException(QUESTION_OPTION_MISMATCH, option.getOptionId()));

                    // todo validate answer.
                /*if (isNotQuestionOption(questionAnswer.getQuizQuestion(), option)) {
                    throw new TutorException(QUESTION_OPTION_MISMATCH, questionAnswer.getQuizQuestion().getQuestion().getId(), option.getId());
                }*/

                    // todo remove old answer
                /*if (questionAnswer.getOption() != null) {
                    questionAnswer.getOption().getQuestionAnswers().remove(questionAnswer);
                }*/

                    getFillInOptions().add(fillInOption);
                }

            } else {
                setFillInOptions(null);
            }
        }
    }

    @Override
    public QuestionAnswerDto getQuestionAnswerDto() {
        return new CodeFillInQuestionAnswerDto(this);
    }

    @Override
    public CorrectAnswerDto getCorrectAnswerDto() {
        return new CodeFillInCorrectAnswerDto(this);
    }

    @Override
    public StatementAnswerDto getStatementAnswerDto() {
        return new CodeFillInStatementAnswerDto(this);
    }

    @Override
    public StatementQuestionDto getStatementQuestionDto() {
        return new CodeFillInStatementQuestionDto(this);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionAnswer(this);
    }

    @Override
    public CodeFillInQuestion getQuestion() {
        return (CodeFillInQuestion) getQuizQuestion().getQuestion();
    }


    @Override
    public void remove() {
        super.remove();
        if (getFillInOptions() != null) {
            //TODO CHECK IT fillInOption.getQuestionAnswers().remove(this);
            setFillInOptions(null);
        }
    }
}
