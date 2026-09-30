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
        @JsonProperty("material_ids") List<String> materialIds) {
}
