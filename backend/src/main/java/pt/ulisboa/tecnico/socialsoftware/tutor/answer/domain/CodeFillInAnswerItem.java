package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.Answerable;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeFillInOptionStatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeFillInStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CODE_FILL_IN_QUESTION)
public class CodeFillInAnswerItem extends QuestionAnswerItem {

    @ElementCollection
    private List<Integer> optionIds;

    public CodeFillInAnswerItem() {
    }

    public CodeFillInAnswerItem(String username, int quizId, StatementAnswerDto answer, CodeFillInStatementAnswerDetailsDto detailsDto) {
        super(username, quizId, answer);
        this.optionIds = detailsDto.getSelectedOptions()
                .stream()
                .map(CodeFillInOptionStatementAnswerDto::getOptionId)
                .collect(Collectors.toList());
    }

    @Override
    public String getAnswerRepresentation(Map<Integer, Answerable> options) {
        return optionIds.stream()
                .map(x -> options.get(x).getAnswerRepresentation())
                .collect(Collectors.joining("|"));
    }
}
