package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "questions")
public class Question implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private String content;
    private Integer difficulty;
    private Boolean active;

    @OneToOne(cascade = { CascadeType.ALL }, mappedBy = "question")
    private Image image;

    @OneToMany(cascade = { CascadeType.ALL }, mappedBy = "question")
    private List<Option> options = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question")
    Set<QuizQuestion> quizQuestions;

    public Question(){}

    public Question(QuestionDto question) {
        this.content = question.getContent();
        this.difficulty = question.getDifficulty();
        this.active = question.getActive();
       if (question.getImage() != null) {
            setImage(new Image(question.getImage()));
        }
        question.getOptions().stream().map(Option::new).forEach(option -> this.options.add(option));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
   }

    public Integer getCorrectOption() {
        Optional<Option> correctOption = this.getOptions().stream().filter(Option::getCorrect).findAny();
        return correctOption.get().getOption();
    }
}