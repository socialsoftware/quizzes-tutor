package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/** A piece of a material as the generation service stores it; `heading` is the path of headings above it. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AqgChunkDto(String id, Integer position, String heading, String text) implements Serializable {
}
