package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/** A heading path of a material, as the generation service reports it (and as the browser gets it). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AqgSectionDto(
        String path,
        String title,
        @JsonProperty("chunk_count") Integer chunkCount)
        implements Serializable {
}
