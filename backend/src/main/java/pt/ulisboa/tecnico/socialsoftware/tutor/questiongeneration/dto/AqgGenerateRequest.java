package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body of POST /generate on the question generation service. */
public record AqgGenerateRequest(
        @JsonProperty("course_id") int courseId,
        String topic,
        String difficulty,
        int count,
        @JsonProperty("grounding_mode") String groundingMode,
        // null keeps the language of the materials
        String language,
        // set when rewriting a question after a teacher's review
        Revision revision,
        // questions already in the course, so the service retries drafts that repeat one
        @JsonProperty("existing_stems") List<String> existingStems,
        // the pieces of documents under the topic and its subtopics, in tree order
        @JsonProperty("chunk_ids") List<String> chunkIds,
        // what to ask about inside the topic
        String focus,
        // the asking teacher's own API keys by provider; empty when they have none
        @JsonProperty("api_keys") @JsonInclude(JsonInclude.Include.NON_EMPTY) Map<String, String> apiKeys) {

    public AqgGenerateRequest(int courseId, String topic, String difficulty, int count, String groundingMode,
                              String language, Revision revision, List<String> existingStems, List<String> chunkIds,
                              String focus) {
        this(courseId, topic, difficulty, count, groundingMode, language, revision, existingStems, chunkIds, focus, Map.of());
    }

    public AqgGenerateRequest withApiKeys(Map<String, String> keys) {
        return new AqgGenerateRequest(courseId, topic, difficulty, count, groundingMode, language, revision,
                existingStems, chunkIds, focus, keys);
    }

    // the keys are never printed
    @Override
    public String toString() {
        return "AqgGenerateRequest{courseId=" + courseId + ", topic=" + topic + ", count=" + count + "}";
    }

    public record Revision(AqgJobDto.Question previous, String review) {
    }
}
