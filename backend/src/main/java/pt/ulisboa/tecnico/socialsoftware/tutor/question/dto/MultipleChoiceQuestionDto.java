package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleChoiceQuestionDto extends QuestionDto {
    private List<OptionDto> options = new ArrayList<>();

    public MultipleChoiceQuestionDto() {
    }

    public MultipleChoiceQuestionDto(MultipleChoiceQuestion question) {
        super(question);
        this.options = question.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "QuestionDto{" +
                "id=" + getId() +
                ", key=" + getKey() +
                ", title='" + getTitle() + '\'' +
                ", content='" + getContent() + '\'' +
                ", difficulty=" + getDifficulty() +
                ", numberOfAnswers=" + getNumberOfAnswers() +
                ", numberOfGeneratedQuizzes=" + getNumberOfGeneratedQuizzes() +
                ", numberOfNonGeneratedQuizzes=" + getNumberOfNonGeneratedQuizzes() +
                ", numberOfCorrect=" + getNumberOfCorrect() +
                ", creationDate='" + getCreationDate() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", options=" + getOptions() +
                ", image=" + getImage() +
                ", topics=" + getTopics() +
                ", sequence=" + getSequence() +
                '}';
    }
}
