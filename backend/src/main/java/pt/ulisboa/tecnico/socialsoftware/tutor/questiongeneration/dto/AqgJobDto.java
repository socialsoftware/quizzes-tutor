package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** A generation job as the question generation service reports it. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AqgJobDto(
        String id,
        String status,
        @JsonProperty("prompt_version") String promptVersion,
        @JsonProperty("model_id") String modelId,
        @JsonProperty("grounding_mode") String groundingMode,
        List<Outcome> outcomes,
        String error) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Outcome(
            String status,
            Question question,
            Integer retries,
            List<String> failures,
            @JsonProperty("source_chunk_ids") List<String> sourceChunkIds) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Question(String stem, List<Option> options, String explanation) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Option(String content, boolean correct) {
    }
}
