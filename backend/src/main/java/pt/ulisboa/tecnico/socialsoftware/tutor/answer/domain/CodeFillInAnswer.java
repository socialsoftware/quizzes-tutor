package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MultipleChoiceAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
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
    @JoinColumn(name = "option_id")
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
        // TODO: IMPLEMENT
        return false;
    }

    @Override
    public AnswerDetailsDto getAnswerDetailsDto() {
        // TODO: IMPLEMENT
        return null;
    }

    @Override
    public StatementAnswerDetailsDto getStatementAnswerDetailsDto() {
        // TODO: IMPLEMENT
        return null;
    }

    @Override
    public boolean isAnswered() {
        // TODO: IMPLEMENT
        return false;
    }

    @Override
    public void accept(Visitor visitor) {
        // TODO: IMPLEMENT
    }
}
