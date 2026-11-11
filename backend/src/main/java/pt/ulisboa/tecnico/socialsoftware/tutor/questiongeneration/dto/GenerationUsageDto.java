package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import java.util.List;

/**
 * How much question generation each course asked for over the last days: a row per course
 * execution, a count per day (oldest first, the same order as `days`).
 */
public record GenerationUsageDto(List<String> days, List<Row> courses) {
    public record Row(Integer courseExecutionId, String course, String academicTerm,
                      List<Integer> requests, List<Integer> questions, int totalRequests, int totalQuestions) {
    }
}
