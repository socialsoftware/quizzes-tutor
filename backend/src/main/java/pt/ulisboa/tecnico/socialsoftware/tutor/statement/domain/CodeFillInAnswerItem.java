package pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.CodeFillInOptionStatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.CodeFillInStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.MultipleChoiceStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CODE_FILL_IN_QUESTION)
public class CodeFillInAnswerItem extends QuestionAnswerItem {

    // TODO: MIGHT NOT BE THE SMARTEST APPROACH HERE
    @Lob
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
    public String getAnswerRepresentation(Map<Integer, Option> options) {
        // TODO: Need to create a string representation for export
        return null;
    }
}
