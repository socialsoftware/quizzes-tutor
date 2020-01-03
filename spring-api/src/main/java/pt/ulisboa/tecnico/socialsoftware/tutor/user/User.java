package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity(name = "Users")
@Table(name = "users")
public class User implements UserDetails {

    public enum Role {STUDENT, TEACHER, ADMIN}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Integer number;
    private String username;
    private String name;
    private Integer year;
    private String courseExecutionAcronyms;

    private Integer numberOfTeacherQuizzes;
    private Integer numberOfStudentQuizzes ;
    private Integer numberOfAnswers;
    private Integer numberOfTeacherAnswers;
    private Integer numberOfCorrectAnswers;
    private Integer numberOfCorrectTeacherAnswers;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch=FetchType.LAZY)
    private Set<QuizAnswer> quizAnswers = new HashSet<>();

    @ManyToMany
    private Set<CourseExecution> courseExecutions = new HashSet<>();

    public User() {
    }

    public User(String name, String username, Integer number, Integer year, User.Role role) {
        this.name = name;
        setUsername(username);
        this.number = number;
        this.year = year;
        this.role = role;
        this.creationDate = LocalDateTime.now();
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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getCourseExecutionAcronyms() {
        return courseExecutionAcronyms;
    }

    public void setCourseExecutionAcronyms(String courseExecutionAcronyms) {
        this.courseExecutionAcronyms = courseExecutionAcronyms;
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

    public Set<QuizAnswer> getQuizAnswers() {
        return quizAnswers;
    }

    public void setQuizAnswers(Set<QuizAnswer> quizAnswers) {
        this.quizAnswers = quizAnswers;
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
                ", number=" + number +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", year=" + year +
                ", courseExecutionAcronyms='" + courseExecutionAcronyms + '\'' +
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
        // it is ok because the total number of available questions > numberOfQuestions
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
        this.numberOfTeacherQuizzes = (int) this.getQuizAnswers().stream()
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.TEACHER))
                .count();

        this.numberOfStudentQuizzes = (int) this.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.STUDENT))
            .count();

        this.numberOfAnswers = this.getQuizAnswers().stream()
                .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestions().size())
            .sum();

        this.numberOfCorrectAnswers = (int) this.getQuizAnswers().stream()
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .filter(questionAnswer -> questionAnswer.getOption() != null &&
                        questionAnswer.getOption().getCorrect())
                .count();

        this.numberOfTeacherAnswers = this.getQuizAnswers().stream()
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.TEACHER))
                .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestions().size())
                .sum();

        this.numberOfCorrectTeacherAnswers = (int) this.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.TEACHER))
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .filter(questionAnswer -> questionAnswer.getOption() != null &&
                        questionAnswer.getOption().getCorrect())
                .count();
    }
}