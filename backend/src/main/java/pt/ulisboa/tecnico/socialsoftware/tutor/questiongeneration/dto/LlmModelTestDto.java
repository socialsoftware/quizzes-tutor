package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/** How a one-line call to a model went: whether it answered, how fast, and what went wrong if not. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LlmModelTestDto(Boolean ok, Double seconds, String reply, String error) implements Serializable {
}
