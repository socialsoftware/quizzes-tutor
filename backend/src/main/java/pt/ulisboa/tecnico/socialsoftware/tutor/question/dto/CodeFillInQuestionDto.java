package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CodeFillInQuestionDto extends QuestionDto {
    private List<FillInSpotDto> fillInSpots = new ArrayList<>();

    public CodeFillInQuestionDto() {
    }

    public CodeFillInQuestionDto(CodeFillInQuestion question) {
        super(question);
        this.fillInSpots = question.getFillInSpots()
                .stream().map(FillInSpotDto::new)
                .collect(Collectors.toList());
    }

    public List<FillInSpotDto> getFillInSpots() {
        return fillInSpots;
    }

    public void setFillInSpots(List<FillInSpotDto> fillInSpots) {
        this.fillInSpots = fillInSpots;
    }

    @Override
    public String toString() {
        return "CodeFillInQuestionDto{" +
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
                ", fillInSpots=" + getFillInSpots() +
                ", image=" + getImage() +
                ", topics=" + getTopics() +
                ", sequence=" + getSequence() +
                '}';
    }
}
