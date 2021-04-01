package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeOrderAnswerSlot;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CodeOrderSlotDto;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "code_order_slot")
public class CodeOrderSlot implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer sequence;

    @Column(name = "correct_order")
    private Integer order;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_details_id")
    private CodeOrderQuestion questionDetails;

    @OneToMany(mappedBy = "codeOrderSlot", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<CodeOrderAnswerSlot> orderedSlots = new HashSet<>();

    public CodeOrderSlot() {
    }

    public CodeOrderSlot(CodeOrderSlotDto codeOrderSlotDto) {
        this.order = codeOrderSlotDto.getOrder();
        this.content = codeOrderSlotDto.getContent();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CodeOrderQuestion getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(CodeOrderQuestion questionDetails) {
        this.questionDetails = questionDetails;
    }

    public Set<CodeOrderAnswerSlot> getOrderedSlots() {
        return orderedSlots;
    }

    public void delete() {
        this.questionDetails = null;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitCodeOrderSlot(this);
    }
}
