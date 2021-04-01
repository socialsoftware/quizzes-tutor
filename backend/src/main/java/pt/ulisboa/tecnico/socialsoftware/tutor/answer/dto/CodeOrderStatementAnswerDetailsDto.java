package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeOrderAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeOrderAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswerItem;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CodeOrderStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private List<CodeOrderSlotStatementAnswerDetailsDto> orderedSlots = new ArrayList<>();

    public CodeOrderStatementAnswerDetailsDto() {
    }

    public CodeOrderStatementAnswerDetailsDto(CodeOrderAnswer questionAnswer) {
        if (questionAnswer.getOrderedSlots() != null) {
            this.orderedSlots = questionAnswer.getOrderedSlots()
                    .stream()
                    .map(CodeOrderSlotStatementAnswerDetailsDto::new)
                    .collect(Collectors.toList());
            this.orderedSlots.sort(Comparator.comparing(CodeOrderSlotStatementAnswerDetailsDto::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));
        }
    }

    public List<CodeOrderSlotStatementAnswerDetailsDto> getOrderedSlots() {
        return orderedSlots;
    }

    public void setOrderedSlots(List<CodeOrderSlotStatementAnswerDetailsDto> orderedSlots) {
        this.orderedSlots = orderedSlots;
    }

    @Transient
    private CodeOrderAnswer codeOrderAnswer;

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
        codeOrderAnswer = new CodeOrderAnswer(questionAnswer);
        questionAnswer.getQuestion().getQuestionDetails().update(this);
        return codeOrderAnswer;
    }

    @Override
    public void update(CodeOrderQuestion question) {
        codeOrderAnswer.setOrderedSlots(question, this);
    }

    @Override
    public boolean emptyAnswer() {
        return orderedSlots == null || orderedSlots.isEmpty();
    }

    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
        return new CodeOrderAnswerItem(username, quizId, statementAnswerDto, this);
    }

    @Override
    public String toString() {
        return "CodeOrderStatementAnswerDetailsDto{" +
                "orderedSlots=" + orderedSlots +
                '}';
    }
}
