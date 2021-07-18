package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.CodeFillInOptionStatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.CodeFillInStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.QuestionTypes;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(QuestionTypes.CODE_FILL_IN_QUESTION)
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
    public String getAnswerRepresentation(QuestionDetails questionDetails) {
        return questionDetails.getAnswerRepresentation(optionIds);
    }
}
