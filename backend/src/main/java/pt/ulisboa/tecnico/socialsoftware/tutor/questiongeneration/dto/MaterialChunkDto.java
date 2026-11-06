package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import java.io.Serializable;
import java.util.List;

/**
 * A piece of a course material with the topics it is under (none: under no topic), to distribute the
 * material over topics.
 */
public record MaterialChunkDto(String id, Integer position, String heading, String text, List<Integer> topicIds) implements Serializable {
    public MaterialChunkDto(AqgChunkDto chunk, List<Integer> topicIds) {
        this(chunk.id(), chunk.position(), chunk.heading(), chunk.text(), topicIds == null ? List.of() : topicIds);
    }
}
