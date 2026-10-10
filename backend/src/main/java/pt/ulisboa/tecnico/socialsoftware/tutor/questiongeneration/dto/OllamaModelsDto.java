package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/** The models an Ollama server has and how the downloads asked for went ("downloading", "done" or the error). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OllamaModelsDto(List<String> models, Map<String, String> pulls, String error) implements Serializable {
}
