package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizType;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.StudentDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.common.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.Review;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.*;

@Entity
@Table(name = "users")
public class User implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true)
    private Integer key;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String name;

    @Column(columnDefinition = "boolean default false")
    private Boolean active;

    private String username;

    private Integer numberOfTeacherQuizzes = 0;
    private Integer numberOfStudentQuizzes = 0;
    private Integer numberOfInClassQuizzes = 0;
    private Integer numberOfTeacherAnswers = 0;
    private Integer numberOfInClassAnswers = 0;
    private Integer numberOfStudentAnswers = 0;
    private Integer numberOfCorrectTeacherAnswers = 0;
    private Integer numberOfCorrectInClassAnswers = 0;
    private Integer numberOfCorrectStudentAnswers = 0;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Discussion> discussions = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Reply> replies = new HashSet<>();

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    private Set<QuizAnswer> quizAnswers = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "submitter", fetch = FetchType.LAZY)
    private Set<QuestionSubmission> questionSubmissions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Review> reviews = new HashSet<>();

    @ManyToMany
    private Set<CourseExecution> courseExecutions = new HashSet<>();

    public User() {
    }

    public User(String name, String username, Role role){
        setName(name);
        setRole(role);
        setCreationDate(DateHandler.now());
        setUsername(username);
    }

    public User(String name, Role role){
        setName(name);
        setRole(role);
        setCreationDate(DateHandler.now());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitUser(this);
    }

    public Integer getId() {
        return id;
    }

    public Integer getKey() {
        if (this.key == null) {
            this.key = this.id;
        }
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<Discussion> getDiscussions() {
        return discussions;
    }

    public void addDiscussion(Discussion discussion) {this.discussions.add(discussion);}

    public void setDiscussions(Set<Discussion> discussions) {
        this.discussions = discussions;
    }

    public Set<Reply> getReplies() {
        return replies;
    }

    public void setReplies(Set<Reply> replies) {
        this.replies = replies;
    }

    public void setQuizAnswers(Set<QuizAnswer> quizAnswers) {
        this.quizAnswers = quizAnswers;
    }

    public String getUsername() {
        if (username == null) {
            return String.format("%s-%s", getRole().toString().toLowerCase(), getId());
        }
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void addReply(Reply reply) {
        this.replies.add(reply);
    }

    public void removeReply(Reply reply) {this.replies.remove(reply);}

    public void setRole(Role role) {
        if (role == null)
            throw new TutorException(INVALID_ROLE);

        this.role = role;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Set<QuizAnswer> getQuizAnswers() {
        return quizAnswers;
    }

    public Set<CourseExecution> getCourseExecutions() {
        return courseExecutions;
    }

    public void setCourseExecutions(Set<CourseExecution> courseExecutions) {
        this.courseExecutions = courseExecutions;
    }

    public void setQuestionSubmissions(Set<QuestionSubmission> questionSubmissions) { 
        this.questionSubmissions = questionSubmissions; 
    }

    public Integer getNumberOfTeacherQuizzes() {
        if (this.numberOfTeacherQuizzes == null)
            this.numberOfTeacherQuizzes = (int) getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(QuizType.PROPOSED))
                    .count();

        return numberOfTeacherQuizzes;
    }

    public void setNumberOfTeacherQuizzes(Integer numberOfTeacherQuizzes) {
        this.numberOfTeacherQuizzes = numberOfTeacherQuizzes;
    }

    public Integer getNumberOfStudentQuizzes() {
        if (this.numberOfStudentQuizzes == null)
            this.numberOfStudentQuizzes = (int) getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(QuizType.GENERATED))
                    .count();

        return numberOfStudentQuizzes;
    }

    public void setNumberOfStudentQuizzes(Integer numberOfStudentQuizzes) {
        this.numberOfStudentQuizzes = numberOfStudentQuizzes;
    }

    public Integer getNumberOfInClassQuizzes() {
        if (this.numberOfInClassQuizzes == null)
            this.numberOfInClassQuizzes = (int) getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(QuizType.IN_CLASS))
                    .count();

        return numberOfInClassQuizzes;
    }

    public void setNumberOfInClassQuizzes(Integer numberOfInClassQuizzes) {
        this.numberOfInClassQuizzes = numberOfInClassQuizzes;
    }

    public Integer getNumberOfTeacherAnswers() {
        if (this.numberOfTeacherAnswers == null)
            this.numberOfTeacherAnswers = getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(QuizType.PROPOSED))
                    .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestionsNumber())
                    .sum();

        return numberOfTeacherAnswers;
    }

    public void setNumberOfTeacherAnswers(Integer numberOfTeacherAnswers) {
        this.numberOfTeacherAnswers = numberOfTeacherAnswers;
    }

    public Integer getNumberOfInClassAnswers() {
        if (this.numberOfInClassAnswers == null)
            this.numberOfInClassAnswers = getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(QuizType.IN_CLASS))
                    .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestionsNumber())
                    .sum();
            return numberOfInClassAnswers;
    }

    public void setNumberOfInClassAnswers(Integer numberOfInClassAnswers) {
        this.numberOfInClassAnswers = numberOfInClassAnswers;
    }

    public Integer getNumberOfStudentAnswers() {
        if (this.numberOfStudentAnswers == null) {
            this.numberOfStudentAnswers = getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(QuizType.GENERATED))
                    .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestionsNumber())
                    .sum();
        }

        return numberOfStudentAnswers;
    }

    public void setNumberOfStudentAnswers(Integer numberOfStudentAnswers) {
        this.numberOfStudentAnswers = numberOfStudentAnswers;
    }

    public Integer getNumberOfCorrectTeacherAnswers() {
        if (this.numberOfCorrectTeacherAnswers == null)
            this.numberOfCorrectTeacherAnswers = (int) this.getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(QuizType.PROPOSED))
                    .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                    .filter(QuestionAnswer::isCorrect)
                    .count();

            return numberOfCorrectTeacherAnswers;
    }

    public void setNumberOfCorrectTeacherAnswers(Integer numberOfCorrectTeacherAnswers) {
        this.numberOfCorrectTeacherAnswers = numberOfCorrectTeacherAnswers;
    }

    public Integer getNumberOfCorrectInClassAnswers() {
        if (this.numberOfCorrectInClassAnswers == null)
            this.numberOfCorrectInClassAnswers = (int) this.getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(QuizType.IN_CLASS))
                    .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                    .filter(QuestionAnswer::isCorrect)
                    .count();

        return numberOfCorrectInClassAnswers;
    }

    public void setNumberOfCorrectInClassAnswers(Integer numberOfCorrectInClassAnswers) {
        this.numberOfCorrectInClassAnswers = numberOfCorrectInClassAnswers;
    }


    public Integer getNumberOfCorrectStudentAnswers() {
        if (this.numberOfCorrectStudentAnswers == null)
            this.numberOfCorrectStudentAnswers = (int) this.getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(QuizType.GENERATED))
                    .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                    .filter(QuestionAnswer::isCorrect)
                    .count();

        return numberOfCorrectStudentAnswers;
    }

    public void setNumberOfCorrectStudentAnswers(Integer numberOfCorrectStudentAnswers) {
        this.numberOfCorrectStudentAnswers = numberOfCorrectStudentAnswers;
    }

    public Integer getNumAnsweredDiscussions() {
        return (int) this.getDiscussions().stream().filter(Discussion::teacherAnswered).count();
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", key=" + key +
                ", role=" + role +
                ", username='" + getUsername() + '\'' +
                ", name='" + name + '\'' +
                ", numberOfTeacherQuizzes=" + numberOfTeacherQuizzes +
                ", numberOfStudentQuizzes=" + numberOfStudentQuizzes +
                ", numberOfInClassQuizzes=" + numberOfInClassQuizzes +
                ", numberOfTeacherAnswers=" + numberOfTeacherAnswers +
                ", numberOfInClassAnswers=" + numberOfInClassAnswers +
                ", numberOfStudentAnswers=" + numberOfStudentAnswers +
                ", numberOfCorrectTeacherAnswers=" + numberOfCorrectTeacherAnswers +
                ", numberOfCorrectInClassAnswers=" + numberOfCorrectInClassAnswers +
                ", numberOfCorrectStudentAnswers=" + numberOfCorrectStudentAnswers +
                ", creationDate=" + creationDate +
                '}';
    }

    public void increaseNumberOfQuizzes(QuizType type) {
        switch (type) {
            case PROPOSED:
                this.numberOfTeacherQuizzes = getNumberOfTeacherQuizzes() + 1;
                break;
            case IN_CLASS:
                this.numberOfInClassQuizzes = getNumberOfInClassQuizzes() + 1;
                break;
            case GENERATED:
                this.numberOfStudentQuizzes = getNumberOfStudentQuizzes() + 1;
                break;
            default:
                break;
        }
    }

    public void increaseNumberOfAnswers(QuizType type) {
        switch (type) {
            case PROPOSED:
                this.numberOfTeacherAnswers = getNumberOfTeacherAnswers() + 1;
                break;
            case IN_CLASS:
                this.numberOfInClassAnswers = getNumberOfInClassAnswers() + 1;
                break;
            case GENERATED:
                this.numberOfStudentAnswers = getNumberOfStudentAnswers() + 1;
                break;
            default:
                break;
        }
    }

    public void increaseNumberOfCorrectAnswers(QuizType type) {
        switch (type) {
            case PROPOSED:
                this.numberOfCorrectTeacherAnswers = getNumberOfCorrectTeacherAnswers() + 1;
                break;
            case IN_CLASS:
                this.numberOfCorrectInClassAnswers = getNumberOfCorrectInClassAnswers() + 1;
                break;
            case GENERATED:
                this.numberOfCorrectStudentAnswers = getNumberOfCorrectStudentAnswers() + 1;
                break;
            default:
                break;
        }
    }

    public void addQuizAnswer(QuizAnswer quizAnswer) {
        this.quizAnswers.add(quizAnswer);
    }

    public void addCourse(CourseExecution course) {
        this.courseExecutions.add(course);
        course.addUser(this);
    }

    public void addQuestionSubmission(QuestionSubmission questionSubmission) {
        questionSubmissions.add(questionSubmission);
    }

    public Set<QuestionSubmission> getQuestionSubmissions() { return questionSubmissions; }

    public Set<Review> getReviews() { return reviews; }

    public boolean isStudent() { return this.role == Role.STUDENT; }

    public boolean isTeacher() { return this.role == Role.TEACHER; }

    public List<Question> filterQuestionsByStudentModel(Integer numberOfQuestions, List<Question> availableQuestions) {
        List<Question> studentAnsweredQuestions = getQuizAnswers().stream()
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .filter(questionAnswer -> availableQuestions.contains(questionAnswer.getQuizQuestion().getQuestion()))
                .filter(questionAnswer -> questionAnswer.getTimeTaken() != null && questionAnswer.getTimeTaken() != 0)
                .map(questionAnswer -> questionAnswer.getQuizQuestion().getQuestion())
                .collect(Collectors.toList());

        List<Question> notAnsweredQuestions = availableQuestions.stream()
                .filter(question -> !studentAnsweredQuestions.contains(question))
                .collect(Collectors.toList());

        List<Question> result = new ArrayList<>();

        // add 80% of notanswered questions
        // may add less if not enough notanswered
        int numberOfAddedQuestions = 0;
        while (numberOfAddedQuestions < numberOfQuestions * 0.8
                && notAnsweredQuestions.size() >= numberOfAddedQuestions + 1) {
            result.add(notAnsweredQuestions.get(numberOfAddedQuestions++));
        }

        // add notanswered questions if there is not enough answered questions
        // it is ok because the total id of available questions > numberOfQuestions
        while (studentAnsweredQuestions.size() + numberOfAddedQuestions < numberOfQuestions) {
            result.add(notAnsweredQuestions.get(numberOfAddedQuestions++));
        }

        // add answered questions
        Random rand = new Random(System.currentTimeMillis());
        while (numberOfAddedQuestions < numberOfQuestions) {
            int next = rand.nextInt(studentAnsweredQuestions.size());
            if (!result.contains(studentAnsweredQuestions.get(next))) {
                result.add(studentAnsweredQuestions.get(next));
                numberOfAddedQuestions++;
            }
        }

        return result;
    }

    public QuizAnswer getQuizAnswer(Quiz quiz) {
        return getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz() == quiz)
                .findAny()
                .orElse(null);
    }

    public boolean isDemoStudent() {
        return getUsername().toLowerCase(Locale.ROOT).startsWith("demo-student");
    }

    public void remove() {
        if (!isDemoStudent() && isActive()) {
                throw new TutorException(USER_IS_ACTIVE, getUsername());
        }
        //if user is not active does it matter to check ?
        if (!quizAnswers.isEmpty()) {
            throw new TutorException(USER_HAS_QUIZ_ANSWERS, getUsername());
        }

        if (!questionSubmissions.isEmpty()) {
            throw new TutorException(USER_HAS_QUESTION_SUBMISSIONS, getUsername());
        }

        if (!discussions.isEmpty()) {
            throw new TutorException(USER_HAS_DISCUSSIONS, getUsername());
        }

        if (!replies.isEmpty()) {
            throw new TutorException(USER_HAS_REPLIES, getUsername());
        }

        courseExecutions.forEach(ce -> ce.getUsers().remove(this));
        courseExecutions.clear();
    }

    public UserDto getUserDto() {
        UserDto dto = new UserDto();
        dto.setId(getId());
        dto.setUsername(getUsername());
        dto.setName(getName());
        dto.setRole(getRole().toString());
        dto.setActive(isActive());
        dto.setCreationDate(DateHandler.toISOString(getCreationDate()));
        return dto;
    }

    public StudentDto getStudentDto() {
        UserDto userDto = getUserDto();
        StudentDto studentDto = new StudentDto();

        studentDto.setId(userDto.getId());
        studentDto.setUsername(userDto.getUsername());
        studentDto.setEmail(userDto.getEmail());
        studentDto.setName(userDto.getName());
        studentDto.setRole(userDto.getRole());
        studentDto.setActive(userDto.isActive());
        studentDto.setCreationDate(userDto.getCreationDate());
        studentDto.setLastAccess(userDto.getLastAccess());

        studentDto.setNumberOfInClassQuizzes(getNumberOfInClassQuizzes());
        studentDto.setNumberOfStudentQuizzes(getNumberOfStudentQuizzes());
        studentDto.setNumberOfAnswers(getNumberOfTeacherAnswers() + getNumberOfInClassAnswers() + getNumberOfStudentAnswers());
        studentDto.setNumberOfTeacherAnswers(getNumberOfTeacherAnswers());
        studentDto.setNumberOfInClassAnswers(getNumberOfInClassAnswers());
        studentDto.setNumberOfStudentAnswers(getNumberOfStudentAnswers());

        if (this.numberOfTeacherAnswers != 0)
            studentDto.setPercentageOfCorrectTeacherAnswers(getNumberOfCorrectTeacherAnswers() * 100 / getNumberOfTeacherAnswers());
        if (this.numberOfInClassAnswers != 0)
            studentDto.setPercentageOfCorrectInClassAnswers(getNumberOfCorrectInClassAnswers() * 100 / getNumberOfInClassAnswers());
        if (this.numberOfStudentAnswers != 0)
            studentDto.setPercentageOfCorrectStudentAnswers(getNumberOfCorrectStudentAnswers() * 100 / getNumberOfStudentAnswers());
        if (studentDto.getNumberOfAnswers() != 0)
            studentDto.setPercentageOfCorrectAnswers((getNumberOfCorrectTeacherAnswers() + getNumberOfCorrectInClassAnswers() + getNumberOfCorrectStudentAnswers())  * 100 / studentDto.getNumberOfAnswers());

        return studentDto;
    }
}