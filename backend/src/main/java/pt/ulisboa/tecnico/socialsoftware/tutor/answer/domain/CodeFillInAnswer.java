package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeFillInAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInSpot;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.CodeFillInOptionStatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.CodeFillInStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDetailsDto;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_OPTION_MISMATCH;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CODE_FILL_IN_QUESTION)
public class CodeFillInAnswer extends AnswerDetails {

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "questionAnswers")
    private Set<FillInOption> fillInOptions = new HashSet<>();

    public CodeFillInAnswer() {
        super();
    }

    public CodeFillInAnswer(QuestionAnswer questionAnswer) {
        super(questionAnswer);

    }

    public Set<FillInOption> getFillInOptions() {
        return fillInOptions;
    }

    public void setFillInOptions(Set<FillInOption> fillInOptions) {
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

    @Override
    public void remove() {
        super.remove();
        if (fillInOptions != null) {
            fillInOptions.forEach(x -> x.getQuestionAnswers().remove(this));
            fillInOptions.clear();
        }
    }

    public void setFillInOptions(CodeFillInQuestion question, CodeFillInStatementAnswerDetailsDto codeFillInStatementAnswerDetailsDto) {
        this.fillInOptions.clear();
        if (codeFillInStatementAnswerDetailsDto.emptyAnswer()) {
            for (CodeFillInOptionStatementAnswerDto option : codeFillInStatementAnswerDetailsDto.getSelectedOptions()) {

                FillInOption fillInOption = question.getFillInSpots().stream()
                        .map(FillInSpot::getOptions)
                        .flatMap(Collection::stream)
                        .filter(option1 -> option1.getId().equals(option.getOptionId()))
                        .findAny()
                        .orElseThrow(() -> new TutorException(QUESTION_OPTION_MISMATCH, option.getOptionId()));

                getFillInOptions().add(fillInOption);
            }
        }
    }
}
