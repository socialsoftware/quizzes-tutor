package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.FillInSpotDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "fill_in_spot",
uniqueConstraints = @UniqueConstraint(columnNames = {"question_id","sequence"}))
public class FillInSpot implements DomainEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer sequence;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private CodeFillInQuestion question;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fillInSpot",fetch = FetchType.EAGER, orphanRemoval=true)
    private final List<FillInOption> options = new ArrayList<>();

    public FillInSpot() {}

    public FillInSpot(FillInSpotDto fillInSpotDto) {
        setOptions(fillInSpotDto.getOptions());
        setSequence(fillInSpotDto.getSequence());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CodeFillInQuestion getQuestion() {
        return question;
    }

    public void setQuestion(CodeFillInQuestion question) {
        this.question = question;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public List<FillInOption> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        if (options.stream().filter(OptionDto::getCorrect).count() == 0) {
            throw new TutorException(NO_CORRECT_OPTION);
        }

        int index = 0;
        for (OptionDto optionDto : options) {
            if (optionDto.getId() == null) {
                optionDto.setSequence(index++);
                FillInOption fillInOption = new FillInOption(optionDto);
                fillInOption.setFillInSpot(this);
                this.options.add(fillInOption);
            } else {
                FillInOption option = getOptions()
                        .stream()
                        .filter(op -> op.getId().equals(optionDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new TutorException(OPTION_NOT_FOUND, optionDto.getId()));

                option.setContent(optionDto.getContent());
                option.setCorrect(optionDto.getCorrect());
            }
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitFillInSpot(this);
    }
}
