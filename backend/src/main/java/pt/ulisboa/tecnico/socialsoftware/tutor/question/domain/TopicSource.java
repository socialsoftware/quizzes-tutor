package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import java.util.Objects;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_TOPIC_SOURCE;

/**
 * A piece of a course material (a chunk of one document) a topic is taught from. The material
 * lives in the question generation service, so only the ids are kept here, with no link between
 * the two databases. A piece belongs to one topic at most.
 */
@Entity
@Table(name = "topic_chunks", uniqueConstraints = @UniqueConstraint(columnNames = "chunk_id"))
public class TopicSource {
    public static final int MAX_MATERIAL_ID_LENGTH = 64;
    public static final int MAX_CHUNK_ID_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(name = "material_id", nullable = false, length = MAX_MATERIAL_ID_LENGTH)
    private String materialId;

    @Column(name = "chunk_id", nullable = false, length = MAX_CHUNK_ID_LENGTH)
    private String chunkId;

    public TopicSource() {
    }

    public TopicSource(Topic topic, String materialId, String chunkId) {
        if (materialId == null || materialId.isBlank() || materialId.length() > MAX_MATERIAL_ID_LENGTH
                || chunkId == null || chunkId.isBlank() || chunkId.length() > MAX_CHUNK_ID_LENGTH)
            throw new TutorException(INVALID_TOPIC_SOURCE);

        this.topic = topic;
        this.materialId = materialId;
        this.chunkId = chunkId;
    }

    public Integer getId() {
        return id;
    }

    public Topic getTopic() {
        return topic;
    }

    public String getMaterialId() {
        return materialId;
    }

    public String getChunkId() {
        return chunkId;
    }

    public boolean is(String chunkId) {
        return Objects.equals(this.chunkId, chunkId);
    }
}
