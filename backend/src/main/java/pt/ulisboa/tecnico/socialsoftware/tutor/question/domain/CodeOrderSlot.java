package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeOrderAnswerOrderedSlot;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CodeOrderSlotDto;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "code_order_slot",
        uniqueConstraints = @UniqueConstraint(columnNames = {"question_details_id", "correct_order"}))
public class CodeOrderSlot implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "correct_order")
    private Integer order;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "question_details_id")
    private CodeOrderQuestion questionDetails;

    @OneToMany(mappedBy = "codeOrderSlot", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final Set<CodeOrderAnswerOrderedSlot> orderedSlots = new HashSet<>();

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

    public Set<CodeOrderAnswerOrderedSlot> getOrderedSlots() {
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
