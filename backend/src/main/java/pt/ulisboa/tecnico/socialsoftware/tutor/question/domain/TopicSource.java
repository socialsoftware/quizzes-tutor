package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import java.util.Objects;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_TOPIC_SOURCE;

/**
 * A part of a course material a topic is taught from: a section (heading path) of one document.
 * The material lives in the question generation service, so only its id and the section path
 * are kept here, with no link between the two databases.
 */
@Entity
@Table(name = "topic_sources")
public class TopicSource {
    public static final int MAX_MATERIAL_ID_LENGTH = 64;
    public static final int MAX_SECTION_PATH_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(name = "material_id", nullable = false, length = MAX_MATERIAL_ID_LENGTH)
    private String materialId;

    @Column(name = "section_path", nullable = false, length = MAX_SECTION_PATH_LENGTH)
    private String sectionPath;

    public TopicSource() {
    }

    public TopicSource(Topic topic, String materialId, String sectionPath) {
        if (materialId == null || materialId.isBlank() || materialId.length() > MAX_MATERIAL_ID_LENGTH
                || sectionPath == null || sectionPath.isBlank() || sectionPath.length() > MAX_SECTION_PATH_LENGTH)
            throw new TutorException(INVALID_TOPIC_SOURCE);

        this.topic = topic;
        this.materialId = materialId;
        this.sectionPath = sectionPath;
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

    public String getSectionPath() {
        return sectionPath;
    }

    public boolean is(String materialId, String sectionPath) {
        return Objects.equals(this.materialId, materialId) && Objects.equals(this.sectionPath, sectionPath);
    }
}
