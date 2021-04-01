package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CodeFillInSpotDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NO_CORRECT_OPTION;

@Entity
@Table(name = "code_fill_in_spot",
        uniqueConstraints = @UniqueConstraint(columnNames = {"question_details_id", "sequence"}))
public class CodeFillInSpot implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer sequence;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "question_details_id")
    private CodeFillInQuestion questionDetails;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codeFillInSpot", fetch = FetchType.EAGER, orphanRemoval = true)
    private final List<CodeFillInOption> options = new ArrayList<>();

    public CodeFillInSpot() {
    }

    public CodeFillInSpot(CodeFillInSpotDto codeFillInSpotDto) {
        setOptions(codeFillInSpotDto.getOptions());
        setSequence(codeFillInSpotDto.getSequence());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CodeFillInQuestion getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(CodeFillInQuestion question) {
        this.questionDetails = question;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public List<CodeFillInOption> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        if (options.stream().filter(OptionDto::isCorrect).count() == 0) {
            throw new TutorException(NO_CORRECT_OPTION);
        }

        // Ensures some randomization when creating the options ids.
        Collections.shuffle(options);

        this.options.clear();

        int index = 0;
        for (OptionDto optionDto : options) {
            int seq = optionDto.getSequence() != null ? optionDto.getSequence() : index++;
            optionDto.setSequence(seq);
            CodeFillInOption codeFillInOption = new CodeFillInOption(optionDto);
            codeFillInOption.setFillInSpot(this);
            this.options.add(codeFillInOption);
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitFillInSpot(this);
    }

    public void visitOptions(Visitor visitor) {
        for (CodeFillInOption option : this.getOptions()) {
            option.accept(visitor);
        }
    }

    public void delete() {
        this.questionDetails = null;
        for (CodeFillInOption option : this.options) {
            option.delete();
        }
        this.options.clear();
    }
}