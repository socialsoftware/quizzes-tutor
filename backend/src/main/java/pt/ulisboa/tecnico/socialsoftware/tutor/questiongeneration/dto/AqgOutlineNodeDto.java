package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/** A heading of a material with the text under it, to edit the sections of the document by hand. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AqgOutlineNodeDto(
        String path,
        String title,
        Integer depth,
        @JsonProperty("has_text") Boolean hasText,
        @JsonProperty("paragraph_count") Integer paragraphCount,
        String preview)
        implements Serializable {
}
