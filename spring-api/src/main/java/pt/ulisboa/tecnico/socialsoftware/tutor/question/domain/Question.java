package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException.ExceptionError.OPTION_NOT_FOUND;

@Entity
@Table(
        name = "questions",
        indexes = {
                @Index(name = "question_indx_0", columnList = "number")
        })
public class Question implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String title;

    @Column(name = "number_of_answers", columnDefinition = "integer default 0")
    private Integer numberOfAnswers = 0;

    @Column(name = "number_of_correct", columnDefinition = "integer default 0")
    private Integer numberOfCorrect = 0;

    @Column(columnDefinition = "boolean default true")
    private Boolean active = true;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "question")
    private Image image;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question", fetch = FetchType.EAGER)
    private List<Option> options = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question")
    private Set<QuizQuestion> quizQuestions;

    @ManyToMany(mappedBy = "questions")
    private Set<Topic> topics = new HashSet<>();

    public Question() {
    }

    public Question(QuestionDto questionDto) {
        checkConsistentQuestion(questionDto);

        this.title = questionDto.getTitle();
        this.number = questionDto.getNumber();
        this.content = questionDto.getContent();
        this.active = questionDto.getActive();

        if (questionDto.getImage() != null) {
            setImage(new Image(questionDto.getImage()));
        }

        int index = 0;
        for (OptionDto optionDto : questionDto.getOptions()) {
            optionDto.setNumber(index++);
            Option option = new Option(optionDto);
            this.options.add(option);
            option.setQuestion(this);
        }
    }

    public void remove() {
        canRemove();

        getTopics().stream().forEach(topic -> {
            topic.getQuestions().remove(this);
        });

        getTopics().clear();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
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

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
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

        double result = numberOfAnswers != 0 ? 1.0 - numberOfCorrect / (double) numberOfAnswers : 0.0;
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

    public void update(QuestionDto questionDto) {
        checkConsistentQuestion(questionDto);

        setTitle(questionDto.getTitle());
        setContent(questionDto.getContent());

        questionDto.getOptions().stream().forEach(optionDto -> {
            Option option = getOptionById(optionDto.getId());
            if (option == null) {
                throw new TutorException(OPTION_NOT_FOUND, optionDto.getId().toString());
            }
            option.setContent(optionDto.getContent());
            option.setCorrect(optionDto.getCorrect());
        });

        // TODO: not yet implemented
        //new Image(questionDto.getImage());
    }

    public void switchActive() {
        this.active = !this.active;
    }

    public void addImage(Image image) {
        this.image = image;
        image.setQuestion(this);
    }

    private void checkConsistentQuestion(QuestionDto questionDto) {
        if (questionDto.getTitle().trim().length() == 0 ||
                questionDto.getContent().trim().length() == 0 ||
                questionDto.getOptions().stream().anyMatch(optionDto -> optionDto.getContent().trim().length() == 0)) {
            throw new TutorException(TutorException.ExceptionError.QUESTION_MISSING_DATA, "");
        }

        if (questionDto.getOptions().stream().filter(option -> option.getCorrect()).count() != 1) {
            throw new TutorException(TutorException.ExceptionError.QUESTION_MULTIPLE_CORRECT_OPTIONS, "");
        }

        if (!questionDto.getOptions().stream().filter(OptionDto::getCorrect).findAny()
                .equals(getOptions().stream().filter(Option::getCorrect).findAny())
                && getQuizQuestions().stream().flatMap(quizQuestion -> quizQuestion.getQuestionAnswers().stream())
                .findAny().isPresent()) {
            throw new TutorException(TutorException.ExceptionError.QUESTION_CHANGE_CORRECT_OPTION_HAS_ANSWERS, "");
        }

    }

    public void updateTopics(Set<Topic> newTopics) {
        Set<Topic> toRemove = this.topics.stream().filter(topic -> !newTopics.contains(topic)).collect(Collectors.toSet());

        toRemove.stream().forEach(topic -> {
            this.topics.remove(topic);
            topic.getQuestions().remove(this);
        });

        newTopics.stream().filter(topic -> !this.topics.contains(topic)).forEach(topic -> {
            this.topics.add(topic);
            topic.getQuestions().add(this);
        });
    }

    private Option getOptionById(Integer id) {
        return getOptions().stream().filter(option -> option.getId().equals(id)).findAny().orElse(null);
    }

    private void canRemove() {
        if (getQuizQuestions().size() != 0) {
            throw new TutorException(TutorException.ExceptionError.QUESTION_IS_USED_IN_QUIZ, getTitle());
        }
    }

    public void setOptionsNumber() {
        int index = 0;
        for (Option option: getOptions()) {
            option.setNumber(index++);
        }
    }
}