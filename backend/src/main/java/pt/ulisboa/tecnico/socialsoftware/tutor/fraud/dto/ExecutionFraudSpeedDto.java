package pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * The students whose answer times make them worth a look over all the quizzes of a course
 * execution, the ones the most quizzes flagged first. A sign for the teacher, never proof.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ExecutionFraudSpeedDto(Integer quizzesAnalysed, List<Student> users) implements Serializable {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Student(UserFraudInfoDto userInfo, Integer quizzesAnalysed, Integer flaggedQuizzes, Integer totalPoints,
                          Integer blockedOpenings, List<QuizResult> quizzes) implements Serializable {
    }

    /** What one quiz said about the student: the level is NONE, LOW, MEDIUM or HIGH, and the reasons say why. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record QuizResult(Integer quizId, String title, Integer answered, Integer tooFast, Double meanSeconds,
                             Integer points, String level, List<String> reasons) implements Serializable {
    }
}
