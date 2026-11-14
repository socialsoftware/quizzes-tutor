package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import java.util.List;

/**
 * What is told about the API keys of someone (a teacher, or the site for the administrator): the
 * ones given and how each ends, never the key, and the providers a key can be given for.
 */
public record TeacherApiKeysDto(boolean available, List<Key> keys, List<Provider> providers) {
    public record Key(String provider, String name, String lastFour, String updatedAt) {
    }

    public record Provider(String id, String name) {
    }
}
