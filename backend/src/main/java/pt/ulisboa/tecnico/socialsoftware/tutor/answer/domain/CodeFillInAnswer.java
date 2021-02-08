package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeFillInAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInSpot;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeFillInOptionStatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeFillInStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDetailsDto;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_OPTION_MISMATCH;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CODE_FILL_IN_QUESTION)
public class CodeFillInAnswer extends AnswerDetails {

    @ManyToMany(mappedBy = "questionAnswers", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<CodeFillInOption> codeFillInOptions = new HashSet<>();

    public CodeFillInAnswer() {
        super();
    }

    public CodeFillInAnswer(QuestionAnswer questionAnswer) {
        super(questionAnswer);

    }

    public Set<CodeFillInOption> getFillInOptions() {
        return codeFillInOptions;
    }

    public void setFillInOptions(Set<CodeFillInOption> codeFillInOptions) {
        this.codeFillInOptions = codeFillInOptions;
    }

    @Override
    public boolean isCorrect() {
        return this.getFillInOptions().stream().allMatch(CodeFillInOption::isCorrect);
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
        return codeFillInOptions != null && !codeFillInOptions.isEmpty();
    }

    public String getAnswerRepresentation() {
        var correctAnswers = this.getFillInOptions().stream().filter(CodeFillInOption::isCorrect).count();
        // TODO: Might be relevant for answer details to know about the respective question answers.
        var questionOptions = ((CodeFillInQuestion) this.getQuestionAnswer().getQuestion().getQuestionDetails()).getFillInSpots().size();
        return String.format("%d/%d", correctAnswers, questionOptions);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitAnswerDetails(this);
    }

    @Override
    public void remove() {
        if (this.codeFillInOptions != null) {
            this.codeFillInOptions.forEach(x -> x.getQuestionAnswers().remove(this));
            this.codeFillInOptions.clear();
        }
    }

    public void setFillInOptions(CodeFillInQuestion question, CodeFillInStatementAnswerDetailsDto codeFillInStatementAnswerDetailsDto) {
        this.codeFillInOptions.clear();
        if (!codeFillInStatementAnswerDetailsDto.emptyAnswer()) {
            for (CodeFillInOptionStatementAnswerDto option : codeFillInStatementAnswerDetailsDto.getSelectedOptions()) {

                CodeFillInOption codeFillInOption = question.getFillInSpots().stream()
                        .map(CodeFillInSpot::getOptions)
                        .flatMap(Collection::stream)
                        .filter(option1 -> option1.getId().equals(option.getOptionId()))
                        .findAny()
                        .orElseThrow(() -> new TutorException(QUESTION_OPTION_MISMATCH, option.getOptionId()));

                getFillInOptions().add(codeFillInOption);
                codeFillInOption.getQuestionAnswers().add(this);
            }
        }
    }
}
