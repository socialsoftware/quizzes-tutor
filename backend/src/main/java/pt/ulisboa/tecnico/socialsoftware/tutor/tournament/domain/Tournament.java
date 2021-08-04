package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "tournaments")
public class Tournament  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "number_of_questions")
    private Integer numberOfQuestions;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Student creator;

    @Column(name = "is_canceled")
    private boolean isCanceled;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Student> participants = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Topic> topics = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Column(name = "privateTournament")
    private boolean privateTournament;

    @Column(name = "password")
    private String password;

    public Tournament() {
    }

    public Tournament(Student student, CourseExecution courseExecution, Set<Topic> topics, TournamentDto tournamentDto) {
        setStartTime(DateHandler.toLocalDateTime(tournamentDto.getStartTime()));
        setEndTime(DateHandler.toLocalDateTime(tournamentDto.getEndTime()));
        setNumberOfQuestions(tournamentDto.getNumberOfQuestions());
        setCanceled(tournamentDto.isCanceled());
        setCreator(student);
        setCourseExecution(courseExecution);
        setTopics(topics);
        setPassword(tournamentDto.getPassword());
        setPrivateTournament(tournamentDto.isPrivateTournament());
    }

    public Integer getId() { return id; }

    public LocalDateTime getStartTime() { return startTime; }

    public void setStartTime(LocalDateTime startTime) {
        // Added 2 minute as a buffer to take latency into consideration
        if (startTime == null || (this.endTime != null && this.endTime.isBefore(startTime) ||
                !DateHandler.now().isBefore(startTime.plusMinutes(2))))
        {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "startTime");
        }

        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() { return endTime; }

    public void setEndTime(LocalDateTime endTime) {
        if (endTime == null || (this.startTime != null && endTime.isBefore(this.startTime))) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "endTime");
        }

        this.endTime = endTime;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        if (numberOfQuestions <= 0) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "number of questions");
        }
        this.numberOfQuestions = numberOfQuestions;
    }

    public Integer getNumberOfQuestions() { return numberOfQuestions; }

    public Student getCreator() { return creator; }

    public void setCreator(Student student) { this.creator = student; }

    public boolean isCreator(User user) { return creator.getId().equals(user.getId()); }

    public void setCanceled(boolean isCanceled) { this.isCanceled = isCanceled; }

    public boolean isCanceled() { return isCanceled; }

    public void cancel() {
        checkCanChange();
        this.isCanceled = true;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Set<Student> getParticipants() { return participants; }

    public void setCourseExecution(CourseExecution courseExecution) { this.courseExecution = courseExecution; }

    public CourseExecution getCourseExecution() { return courseExecution; }

    public Set<Topic> getTopics() { return topics; }

    public void setTopics(Set<Topic> topics) {
        for (Topic topic: topics) {
            checkTopicCourse(topic);
        }

        this.topics = topics;
    }

    public void updateTopics(Set<Topic> newTopics) {
        if (newTopics.isEmpty()) throw new TutorException(TOURNAMENT_MUST_HAVE_ONE_TOPIC);

        for (Topic topic : newTopics) {
            checkTopicCourse(topic);
        }

        this.topics = newTopics;
    }

    public void checkTopicCourse(Topic topic) {
        if (topic.getCourse() != courseExecution.getCourse()) {
            throw new TutorException(TOURNAMENT_TOPIC_COURSE);
        }
    }

    public void checkIsParticipant(Student student) {
        if (!getParticipants().contains(student)) {
            throw new TutorException(USER_NOT_JOINED, student.getId());
        }

        if (student.getQuizAnswer(getQuiz()) != null) {
            throw new TutorException(USER_ALREDAY_ANSWERED_TOURNAMENT_QUIZ, student.getId());
        }
    }

    public void addParticipant(Student student, String password) {
        if (DateHandler.now().isAfter(getEndTime())) {
            throw new TutorException(TOURNAMENT_NOT_OPEN, getId());
        }

        if (isCanceled()) {
            throw new TutorException(TOURNAMENT_CANCELED, getId());
        }

        if (getParticipants().contains(student)) {
            throw new TutorException(DUPLICATE_TOURNAMENT_PARTICIPANT, student.getUsername());
        }

        if (!student.getCourseExecutions().contains(getCourseExecution())) {
            throw new TutorException(STUDENT_NO_COURSE_EXECUTION, student.getId());
        }

        if (isPrivateTournament() && !password.equals(getPassword())) {
            throw new TutorException(WRONG_TOURNAMENT_PASSWORD, getId());
        }

        this.participants.add(student);
        student.addTournament(this);
    }

    public void removeParticipant(Student student) {
        checkIsParticipant(student);
        this.participants.remove(student);
        student.removeTournament(this);
    }

    public boolean hasQuiz() { return getQuiz() != null; }

    public void remove() {
        checkCanChange();

        creator = null;
        courseExecution = null;

        getTopics().forEach(topic -> topic.getTournaments().remove(this));
        getTopics().clear();

        getParticipants().forEach(participant -> participant.getTournaments().remove(this));
        getParticipants().clear();

        if (this.quiz != null) {
            this.quiz.remove();
        }
    }

    public void checkCanChange() {
        int numberOfAnswers = 0;
        if (this.quiz != null) {
            numberOfAnswers = this.quiz.getQuizAnswers() != null ? this.quiz.getQuizAnswers().size() : 0;
        }

        LocalDateTime now = DateHandler.now();

        if (now.isAfter(getEndTime()) && numberOfAnswers != 0) {
            throw new TutorException(TOURNAMENT_ALREADY_CLOSED, getId());
        }

        if (now.isAfter(getStartTime()) && now.isBefore(getEndTime())) {
            throw new TutorException(TOURNAMENT_IS_OPEN, getId());
        }
    }

    public void updateTournament(TournamentDto tournamentDto, Set<Topic> topics) {
        checkCanChange();

        if (DateHandler.isValidDateFormat(tournamentDto.getStartTime())) {
            DateHandler.toISOString(getStartTime());
            setStartTime(DateHandler.toLocalDateTime(tournamentDto.getStartTime()));
        }

        if (DateHandler.isValidDateFormat(tournamentDto.getEndTime())) {
            DateHandler.toISOString(getEndTime());
            setEndTime(DateHandler.toLocalDateTime(tournamentDto.getEndTime()));
        }

        setNumberOfQuestions(tournamentDto.getNumberOfQuestions());

        updateTopics(topics);
    }

    public boolean isPrivateTournament() { return privateTournament; }

    public void setPrivateTournament(boolean privateTournament) { this.privateTournament = privateTournament; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

}
