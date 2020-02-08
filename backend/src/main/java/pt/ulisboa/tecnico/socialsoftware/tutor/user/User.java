package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.Importable;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User implements UserDetails, Importable {

    public enum Role {STUDENT, TEACHER, ADMIN}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Column(unique=true)
    private String username;

    private String name;
    private String enrolledCoursesAcronyms;

    @Column(name = "number_of_teacher_quizzes", columnDefinition = "integer default 0")
    private Integer numberOfTeacherQuizzes = 0;
    @Column(name = "number_of_student_quizzes", columnDefinition = "integer default 0")
    private Integer numberOfStudentQuizzes = 0;
    @Column(name = "number_of_answers", columnDefinition = "integer default 0")
    private Integer numberOfAnswers = 0;
    @Column(name = "number_of_teacher_answers", columnDefinition = "integer default 0")
    private Integer numberOfTeacherAnswers = 0;
    @Column(name = "number_of_correct_answers", columnDefinition = "integer default 0")
    private Integer numberOfCorrectAnswers = 0;
    @Column(name = "number_of_correct_teacher_answers", columnDefinition = "integer default 0")
    private Integer numberOfCorrectTeacherAnswers = 0;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "last_access")
    private LocalDateTime lastAccess;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval=true)
    private Set<QuizAnswer> quizAnswers = new HashSet<>();

    @ManyToMany
    private Set<CourseExecution> courseExecutions = new HashSet<>();

    public User() {
    }

    public User(String name, String username, Integer key, User.Role role) {
        this.name = name;
        setUsername(username);
        this.key = key;
        this.role = role;
        this.creationDate = LocalDateTime.now();
        this.numberOfTeacherQuizzes = 0;
        this.numberOfStudentQuizzes = 0;
        this.numberOfAnswers = 0;
        this.numberOfTeacherAnswers = 0;
        this.numberOfCorrectAnswers = 0;
        this.numberOfCorrectTeacherAnswers = 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    @Override
    public String getUsername() {
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

    public String getEnrolledCoursesAcronyms() {
        return enrolledCoursesAcronyms;
    }

    public void setEnrolledCoursesAcronyms(String enrolledCoursesAcronyms) {
        this.enrolledCoursesAcronyms = enrolledCoursesAcronyms;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
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

    public Integer getNumberOfTeacherQuizzes() {
        return numberOfTeacherQuizzes;
    }

    public void setNumberOfTeacherQuizzes(Integer numberOfTeacherQuizzes) {
        this.numberOfTeacherQuizzes = numberOfTeacherQuizzes;
    }

    public Integer getNumberOfStudentQuizzes() {
        return numberOfStudentQuizzes;
    }

    public void setNumberOfStudentQuizzes(Integer numberOfStudentQuizzes) {
        this.numberOfStudentQuizzes = numberOfStudentQuizzes;
    }

    public Integer getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(Integer numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    public Integer getNumberOfTeacherAnswers() {
        return numberOfTeacherAnswers;
    }

    public void setNumberOfTeacherAnswers(Integer numberOfTeacherAnswers) {
        this.numberOfTeacherAnswers = numberOfTeacherAnswers;
    }

    public Integer getNumberOfCorrectAnswers() {
        return numberOfCorrectAnswers;
    }

    public void setNumberOfCorrectAnswers(Integer numberOfCorrectAnswers) {
        this.numberOfCorrectAnswers = numberOfCorrectAnswers;
    }

    public Integer getNumberOfCorrectTeacherAnswers() {
        return numberOfCorrectTeacherAnswers;
    }

    public void setNumberOfCorrectTeacherAnswers(Integer numberOfCorrectTeacherAnswers) {
        this.numberOfCorrectTeacherAnswers = numberOfCorrectTeacherAnswers;
    }

    public void increaseNumberOfTeacherQuizzes() {
        if (numberOfTeacherQuizzes == null) this.calculateNumbers();
        this.numberOfTeacherQuizzes += 1;
    }

    public void increaseNumberOfTeacherAnswers(int size) {
        if (numberOfTeacherAnswers == null) this.calculateNumbers();
        this.numberOfTeacherAnswers += size;
    }

    public void increaseNumberOfStudentQuizzes() {
        if (numberOfStudentQuizzes == null) this.calculateNumbers();
        this.numberOfStudentQuizzes += 1;
    }

    public void increaseNumberOfAnswers(int size) {
        if (numberOfAnswers == null) this.calculateNumbers();
        this.numberOfAnswers += size;
    }

    public void increaseNumberOfCorrectTeacherAnswers(int size) {
        if (numberOfCorrectTeacherAnswers == null) this.calculateNumbers();
        this.numberOfCorrectTeacherAnswers += size;
    }

    public void increaseNumberOfCorrectAnswers(int size) {
        if (numberOfCorrectAnswers == null) this.calculateNumbers();
        this.numberOfCorrectAnswers += size;
    }

    public void addQuizAnswer(QuizAnswer quizAnswer) {
        this.quizAnswers.add(quizAnswer);
    }

    public void addCourse(CourseExecution course) {
        this.courseExecutions.add(course);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", role=" + role +
                ", id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", courseAcronyms='" + enrolledCoursesAcronyms + '\'' +
                ", numberOfTeacherQuizzes=" + numberOfTeacherQuizzes +
                ", numberOfStudentQuizzes=" + numberOfStudentQuizzes +
                ", numberOfAnswers=" + numberOfAnswers +
                ", numberOfTeacherAnswers=" + numberOfTeacherAnswers +
                ", numberOfCorrectAnswers=" + numberOfCorrectAnswers +
                ", numberOfCorrectTeacherAnswers=" + numberOfCorrectTeacherAnswers +
                ", creationDate=" + creationDate +
                ", courseExecutions=" + courseExecutions +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();

        list.add(new SimpleGrantedAuthority("ROLE_" + role));

        return list;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public List<Question> filterQuestionsByStudentModel(Integer numberOfQuestions, List<Question> availableQuestions) {
        List<Question> studentAnsweredQuestions = getQuizAnswers().stream()
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .filter(questionAnswer -> availableQuestions.contains(questionAnswer.getQuizQuestion().getQuestion()))
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
            if(!result.contains(studentAnsweredQuestions.get(next))) {
                result.add(studentAnsweredQuestions.get(next));
                numberOfAddedQuestions++;
            }
        }

        return result;
    }

    public void calculateNumbers() {
        this.numberOfTeacherQuizzes = (int) getQuizAnswers().stream()
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.PROPOSED))
                .count();

        this.numberOfStudentQuizzes = (int) getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.GENERATED))
            .count();

        this.numberOfAnswers = getQuizAnswers().stream()
                .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestions().size())
            .sum();

        this.numberOfCorrectAnswers = (int) getQuizAnswers().stream()
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .filter(questionAnswer -> questionAnswer.getOption() != null &&
                        questionAnswer.getOption().getCorrect())
                .count();

        this.numberOfTeacherAnswers = getQuizAnswers().stream()
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.PROPOSED))
                .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestions().size())
                .sum();

        this.numberOfCorrectTeacherAnswers = (int) this.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.PROPOSED))
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .filter(questionAnswer -> questionAnswer.getOption() != null &&
                        questionAnswer.getOption().getCorrect())
                .count();
    }
}