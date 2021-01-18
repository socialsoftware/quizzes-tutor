package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderSlot;

import javax.persistence.*;

@Entity
public class CodeOrderAnswerOrderedSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne()
    private CodeOrderAnswer codeOrderAnswer;

    @ManyToOne()
    private CodeOrderSlot codeOrderSlot;

    private Integer assignedOrder;

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
        this.codeOrderAnswer.getOrderedSlots().remove(this);
        this.codeOrderAnswer = null;
        this.codeOrderSlot = null;
    }

    public boolean isCorrect() {
        return this.assignedOrder.equals(this.codeOrderSlot.getOrder());
    }
}
