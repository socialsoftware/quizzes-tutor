package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Which models the generation service uses and how. API keys are not here: they stay in the
 * service's environment. The service checks the values; the Tutor only passes them on.
 */
public record LlmSettingsDto(
        LlmModelChoiceDto primary,
        // tried in order when a call to the model before fails
        List<LlmModelChoiceDto> fallbacks,
        String ollamaBaseUrl,
        // tokens an Ollama model reads at once (null: the server's default)
        Integer ollamaContextLength,
        // "default", "off", "low", "medium" or "high"
        String ollamaThinking,
        Double timeout,
        Boolean thinking,
        Integer maxRetries,
        Boolean discussionSuggestions,
        Integer contextChars) implements Serializable {
}
