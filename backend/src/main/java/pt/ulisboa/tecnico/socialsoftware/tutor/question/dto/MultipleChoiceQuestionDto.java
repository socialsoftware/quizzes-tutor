package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleChoiceQuestionDto extends QuestionTypeDto {
    private List<OptionDto> options = new ArrayList<>();

    public MultipleChoiceQuestionDto() {
    }

    public MultipleChoiceQuestionDto(MultipleChoiceQuestion question) {
        this.options = question.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

    @Override
    public QuestionType getQuestionType() {
        return new MultipleChoiceQuestion(this);
    }

    //TODO[is->has]: to string
    /*
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
    */

}
