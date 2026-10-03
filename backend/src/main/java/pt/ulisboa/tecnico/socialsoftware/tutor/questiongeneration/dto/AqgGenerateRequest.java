package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** Body of POST /generate on the question generation service. */
public record AqgGenerateRequest(
        @JsonProperty("course_id") int courseId,
        String topic,
        String difficulty,
        int count,
        @JsonProperty("grounding_mode") String groundingMode,
        @JsonProperty("material_ids") List<String> materialIds,
        // null keeps the language of the materials
        String language,
        // set when rewriting a question after a teacher's review
        Revision revision,
        // questions already in the course, so the service retries drafts that repeat one
        @JsonProperty("existing_stems") List<String> existingStems,
        // heading paths to draw from (empty: the whole materials)
        List<String> sections,
        // what to ask about inside the topic
        String focus) {

    public record Revision(AqgJobDto.Question previous, String review) {
    }
}
