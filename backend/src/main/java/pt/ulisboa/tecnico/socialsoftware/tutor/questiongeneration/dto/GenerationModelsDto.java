package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import java.util.List;

/**
 * The models a teacher can pick for a request: the one used when they pick none, the ones the
 * administrator lists, and the providers they have a key for (any model of those can be typed).
 * `ownKey` says the request would be charged to the teacher's own key.
 */
public record GenerationModelsDto(Choice defaultModel, List<Choice> models, List<String> ownKeyProviders) {
    public record Choice(String provider, String model, boolean ownKey) {
    }
}
