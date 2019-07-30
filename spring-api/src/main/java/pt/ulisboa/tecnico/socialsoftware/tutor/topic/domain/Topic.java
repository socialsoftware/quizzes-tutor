package pt.ulisboa.tecnico.socialsoftware.tutor.topic.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "Topic")
@Table(name = "topics")
public class Topic implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Topic parent;

    @Column(columnDefinition = "name")
    private String name;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Topic getParent() {
        return parent;
    }

    public void setParent(Topic parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}