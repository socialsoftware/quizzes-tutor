package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import java.util.List;

/**
 * What a teacher is told about their own API keys: which providers have one and how it ends. The
 * key itself is never sent back.
 */
public record TeacherApiKeysDto(boolean available, List<Key> keys) {
    public record Key(String provider, boolean configured, String lastFour, String updatedAt) {
    }
}
