package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "questions")
public class Question implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String title;

    @Column(name= "number_of_answers")
    private Integer numberOfAnswers = 0;

    @Column(name= "number_of_correct")
    private Integer numberOfCorrect = 0;

    private Boolean active = true;

    @OneToOne(cascade = { CascadeType.ALL }, mappedBy = "question")
    private Image image;

    @OneToMany(cascade = { CascadeType.ALL }, mappedBy = "question", fetch=FetchType.EAGER)
    private List<Option> options = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question")
    private Set<QuizQuestion> quizQuestions;

    public Question(){}

    public Question(QuestionDto question) {
        this.title = question.getTitle();
        this.content = question.getContent();
        this.numberOfAnswers = question.getNumberOfAnswers();
        this.numberOfCorrect = question.getNumberOfCorrect();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<QuizQuestion> getQuizQuestions() {
        if (quizQuestions == null) {
            quizQuestions = new HashSet<>();
        }
        return quizQuestions;
    }

    public Integer getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(Integer numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    public Integer getNumberOfCorrect() {
        return numberOfCorrect;
    }

    public void setNumberOfCorrect(Integer numberOfCorrect) {
        this.numberOfCorrect = numberOfCorrect;
    }

    public void setQuizQuestions(Set<QuizQuestion> quizQuestions) {
        this.quizQuestions = quizQuestions;
    }

    public void addOption(Option option) {
        options.add(option);
    }

    public Integer getCorrectOptionId() {
        return this.getOptions().stream()
                .filter(Option::getCorrect)
                .findAny()
                .map(Option::getId)
                .orElse(null);
    }

    public void addQuizQuestion(QuizQuestion quizQuestion) {
        if (quizQuestions == null) {
            quizQuestions = new HashSet<>();
        }
        quizQuestions.add(quizQuestion);
    }

    public double getDifficulty() {
        // required because the import is done directely in the database
        if (numberOfAnswers == null || numberOfAnswers == 0) {
            numberOfAnswers = getQuizQuestions().stream()
                    .flatMap(quizQuestion -> quizQuestion.getQuestionAnswers().stream())
                    .collect(Collectors.reducing(0, e -> 1, Integer::sum));
            numberOfCorrect = getQuizQuestions().stream()
                    .flatMap(quizQuestion -> quizQuestion.getQuestionAnswers().stream())
                    .filter(questionAnswer -> questionAnswer.getOption() != null && questionAnswer.getOption().getCorrect() != null && questionAnswer.getOption().getCorrect())
                    .collect(Collectors.reducing(0, e -> 1, Integer::sum));
        }

        double result = numberOfAnswers != 0 ? 1.0 - numberOfCorrect/ (double) numberOfAnswers : 0.0;
        result = result * 100;
        result = Math.round(result);
        return result / 100;
    }

    public void addAnswer(QuestionAnswer questionAnswer) {
        numberOfAnswers++;
        if (questionAnswer.getOption() != null && questionAnswer.getOption().getCorrect() != null && questionAnswer.getOption().getCorrect()) {
           numberOfCorrect++;
       }
    }
}