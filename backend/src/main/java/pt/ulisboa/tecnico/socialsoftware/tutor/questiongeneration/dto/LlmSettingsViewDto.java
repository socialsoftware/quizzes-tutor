package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/** The settings with what the page needs around them: whether each provider's key is set (never the key). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LlmSettingsViewDto(
        LlmSettingsDto settings,
        Map<String, Boolean> keys,
        List<String> providers,
        Map<String, String> defaultModels) implements Serializable {
}
