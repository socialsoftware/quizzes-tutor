package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeOrderStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.Comparator;
import java.util.List;
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
    public String getAnswerRepresentation(QuestionDetails questionDetails) {
        return questionDetails.getAnswerRepresentation(
                orderedSlots.stream()
                        .sorted(Comparator.comparing(CodeOrderSlotAnswerItem::getAssignedOrder))
                        .map(CodeOrderSlotAnswerItem::getSlotId)
                        .collect(Collectors.toList()));
    }
}
