package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/** The models a provider offers for writing text; `error` says why its list could not be read. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProviderModelsDto(String provider, List<String> models, String error) implements Serializable {
}
