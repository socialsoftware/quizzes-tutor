package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/** The outline after an edit, and where the text of each section that changed went. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AqgOutlineEditResultDto(
        @JsonProperty("path_map") Map<String, List<String>> pathMap,
        List<AqgOutlineNodeDto> outline)
        implements Serializable {
}
