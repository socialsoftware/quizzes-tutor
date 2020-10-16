package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeFillInAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MultipleChoiceAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.CodeFillInStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.MultipleChoiceStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDetailsDto;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_OPTION_MISMATCH;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CODE_FILL_IN_QUESTION)
public class CodeFillInAnswer extends AnswerDetails {

    @ManyToMany
    private List<FillInOption> fillInOptions = new ArrayList<>();

    public CodeFillInAnswer() {
        super();
    }

    public CodeFillInAnswer(QuestionAnswer questionAnswer){
        super(questionAnswer);
    }

    public List<FillInOption> getFillInOptions() {
        return fillInOptions;
    }

    public void setFillInOptions(List<FillInOption> fillInOptions) {
        this.fillInOptions = fillInOptions;
    }

    @Override
    public boolean isCorrect() {
        return this.getFillInOptions().stream().allMatch(FillInOption::isCorrect);
    }

    @Override
    public AnswerDetailsDto getAnswerDetailsDto() {
        return new CodeFillInAnswerDto(this);
    }

    @Override
    public StatementAnswerDetailsDto getStatementAnswerDetailsDto() {
        return new CodeFillInStatementAnswerDetailsDto(this);
    }

    @Override
    public boolean isAnswered() {
        return fillInOptions != null && !fillInOptions.isEmpty();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitAnswerDetails(this);
    }
}
