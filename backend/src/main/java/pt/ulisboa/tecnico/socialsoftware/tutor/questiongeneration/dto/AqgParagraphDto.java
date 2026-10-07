package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/** One paragraph of a section, to choose where to cut it. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AqgParagraphDto(Integer index, String text) implements Serializable {
}
