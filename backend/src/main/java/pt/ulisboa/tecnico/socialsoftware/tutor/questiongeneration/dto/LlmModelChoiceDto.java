package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import java.io.Serializable;

/** A model of a provider (ollama, openai, anthropic, nvidia_nim), as the generation service names it. */
public record LlmModelChoiceDto(String provider, String model) implements Serializable {
}
