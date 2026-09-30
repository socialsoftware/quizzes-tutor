package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AqgMaterialDto(
        String id,
        @JsonProperty("course_id") Integer courseId,
        String filename,
        String status,
        @JsonProperty("chunk_count") Integer chunkCount,
        String parser,
        @JsonProperty("parse_seconds") Double parseSeconds,
        String error) implements Serializable {
}
