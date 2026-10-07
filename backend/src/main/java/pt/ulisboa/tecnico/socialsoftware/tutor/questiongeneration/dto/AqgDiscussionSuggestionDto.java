package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/** A draft reply for the teacher to edit, and the sections of the course material it was written with. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AqgDiscussionSuggestionDto(String reply, List<String> sources) implements Serializable {
}
