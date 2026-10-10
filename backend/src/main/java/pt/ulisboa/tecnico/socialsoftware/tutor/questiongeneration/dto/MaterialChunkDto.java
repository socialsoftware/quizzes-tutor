package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import java.io.Serializable;

/** A piece of a course material with the topic it is under (null: under none), to distribute the material over topics. */
public record MaterialChunkDto(String id, Integer position, String heading, String text, Integer topicId) implements Serializable {

    public MaterialChunkDto(AqgChunkDto chunk, Integer topicId) {
        this(chunk.id(), chunk.position(), chunk.heading(), chunk.text(), topicId);
    }
}
