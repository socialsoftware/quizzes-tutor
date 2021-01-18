package pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.CodeOrderStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CODE_ORDER_QUESTION)
public class CodeOrderAnswerItem extends QuestionAnswerItem {

    @ElementCollection
    private List<CodeOrderSlotAnswerItem> orderedSlots;

    public CodeOrderAnswerItem() {
    }

    public CodeOrderAnswerItem(String username, int quizId, StatementAnswerDto answer, CodeOrderStatementAnswerDetailsDto detailsDto) {
        super(username, quizId, answer);
        this.orderedSlots = detailsDto.getOrderedSlots()
                .stream()
                .map(CodeOrderSlotAnswerItem::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getAnswerRepresentation(Map<Integer, Option> options) {
        // TODO: Need to create a string representation for export
        return orderedSlots.stream().sorted(Comparator.comparing(CodeOrderSlotAnswerItem::getAssignedOrder)).map(c -> String.format("%s(%s)", c.getSlotId(), c.getAssignedOrder())).collect(Collectors.joining("|"));
    }
}
