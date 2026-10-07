package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Body of POST /discussion/suggest on the question generation service: a student's doubt about a
 * question. It carries no names or usernames, only the roles of who wrote each message.
 */
public record AqgDiscussionRequest(
        @JsonProperty("course_id") int courseId,
        @JsonProperty("question_stem") String questionStem,
        List<Option> options,
        String explanation,
        @JsonProperty("student_choice") String studentChoice,
        @JsonProperty("student_message") String studentMessage,
        List<Turn> replies,
        // where the question's topics are taught (document -> sections); never null
        @JsonProperty("material_sections") Map<String, List<String>> materialSections) {

    public record Option(String content, boolean correct) {
    }

    /** role is "student" or "teacher" */
    public record Turn(String role, String message) {
    }
}
