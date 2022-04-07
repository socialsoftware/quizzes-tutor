package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DifficultQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UpdatedDifficultQuestionsDto {
    private String lastCheckDifficultQuestions;
    private List<DifficultQuestionDto> difficultQuestions;

    public UpdatedDifficultQuestionsDto(Dashboard dashboard) {
        this.lastCheckDifficultQuestions = DateHandler.toISOString(dashboard.getLastCheckDifficultQuestions());
        this.difficultQuestions = dashboard.getDifficultQuestions().stream()
                .filter(Predicate.not(DifficultQuestion::isRemoved))
                .map(DifficultQuestionDto::new)
                .collect(Collectors.toList());
    }

    public String getLastCheckDifficultQuestions() {
        return lastCheckDifficultQuestions;
    }

    public void setLastCheckDifficultQuestions(String lastCheckDifficultQuestions) {
        this.lastCheckDifficultQuestions = lastCheckDifficultQuestions;
    }

    public List<DifficultQuestionDto> getDifficultQuestions() {
        return difficultQuestions;
    }

    public void setDifficultQuestions(List<DifficultQuestionDto> difficultQuestions) {
        this.difficultQuestions = difficultQuestions;
    }

    @Override
    public String toString() {
        return "UpdatedDifficultQuestionsDto{" +
                "lastCheckDifficultQuestions='" + lastCheckDifficultQuestions + '\'' +
                ", difficultQuestions=" + difficultQuestions +
                '}';
    }
}
