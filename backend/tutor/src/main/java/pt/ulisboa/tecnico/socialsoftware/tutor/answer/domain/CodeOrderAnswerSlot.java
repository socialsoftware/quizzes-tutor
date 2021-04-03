package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderSlot;

import javax.persistence.*;

@Entity
public class CodeOrderAnswerSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private CodeOrderAnswer codeOrderAnswer;

    @ManyToOne(optional = false)
    private CodeOrderSlot codeOrderSlot;

    private Integer assignedOrder;

    public CodeOrderAnswerSlot() {
    }

    public CodeOrderAnswerSlot(CodeOrderSlot orderSlot, CodeOrderAnswer codeOrderAnswer, Integer assignedOrder) {
        setCodeOrderSlot(orderSlot);
        setCodeOrderAnswer(codeOrderAnswer);
        setAssignedOrder(assignedOrder);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CodeOrderAnswer getCodeOrderAnswer() {
        return codeOrderAnswer;
    }

    public void setCodeOrderAnswer(CodeOrderAnswer codeOrderAnswer) {
        this.codeOrderAnswer = codeOrderAnswer;
    }

    public CodeOrderSlot getCodeOrderSlot() {
        return codeOrderSlot;
    }

    public void setCodeOrderSlot(CodeOrderSlot codeOrderSlot) {
        this.codeOrderSlot = codeOrderSlot;
    }

    public Integer getAssignedOrder() {
        return assignedOrder;
    }

    public void setAssignedOrder(Integer assignedOrder) {
        this.assignedOrder = assignedOrder;
    }

    public void remove() {
        this.codeOrderSlot.getOrderedSlots().remove(this);
        this.codeOrderSlot = null;
    }

    public boolean isCorrect() {
        return this.assignedOrder.equals(this.codeOrderSlot.getOrder());
    }
}
