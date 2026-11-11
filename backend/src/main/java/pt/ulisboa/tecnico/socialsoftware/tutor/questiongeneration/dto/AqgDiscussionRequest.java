package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
        // the pieces of documents under the question's topics; never null
        @JsonProperty("chunk_ids") List<String> chunkIds,
        // the asking teacher's own API keys by provider; empty when they have none
        @JsonProperty("api_keys") @JsonInclude(JsonInclude.Include.NON_EMPTY) Map<String, String> apiKeys) {

    public AqgDiscussionRequest(int courseId, String questionStem, List<Option> options, String explanation,
                                String studentChoice, String studentMessage, List<Turn> replies, List<String> chunkIds) {
        this(courseId, questionStem, options, explanation, studentChoice, studentMessage, replies, chunkIds, Map.of());
    }

    public AqgDiscussionRequest withApiKeys(Map<String, String> keys) {
        return new AqgDiscussionRequest(courseId, questionStem, options, explanation, studentChoice, studentMessage,
                replies, chunkIds, keys);
    }

    // neither the keys nor what the student wrote are printed
    @Override
    public String toString() {
        return "AqgDiscussionRequest{courseId=" + courseId + "}";
    }

    public record Option(String content, boolean correct) {
    }

    /** role is "student" or "teacher" */
    public record Turn(String role, String message) {
    }
}
