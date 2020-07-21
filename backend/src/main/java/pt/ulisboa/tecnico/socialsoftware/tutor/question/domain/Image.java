package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;

import javax.persistence.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_URL_FOR_IMAGE;

@Entity
@Table(name = "images")
public class Image implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String url;

    private Integer width;

    @OneToOne
    @JoinColumn(name="question_id")
    private Question question;

    public Image() {}

    public Image(ImageDto imageDto) {
        setUrl(imageDto.getUrl());
        setWidth(imageDto.getWidth());
    }

    public Image(String url) {
        setUrl(url);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitImage(this);
    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if (url == null || url.isBlank())
            throw new TutorException(INVALID_URL_FOR_IMAGE);

        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", width=" + width +
                '}';
    }
}