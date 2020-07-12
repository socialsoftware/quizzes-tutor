package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;

import javax.persistence.*;
import java.util.*;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "tournaments")
public class Tournament  {
    public enum Status {
        NOT_CANCELED, CANCELED
    }

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
    private User creator;

    @Enumerated(EnumType.STRING)
    private Status state;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<User> participants = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @ManyToOne
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @Column(name = "quizID")
    private Integer quizId;

    @Column(name = "privateTournament")
    private boolean privateTournament;

    @Column(name = "password")
    private String password;

    public Tournament() {
    }

    public Tournament(User user, Assessment assessment, TournamentDto tournamentDto) {
        setStartTime(DateHandler.toLocalDateTime(tournamentDto.getStartTime()));
        setEndTime(DateHandler.toLocalDateTime(tournamentDto.getEndTime()));
        setNumberOfQuestions(tournamentDto.getNumberOfQuestions());
        this.state = tournamentDto.getState();
        this.creator = user;
        setCourseExecution(user);
        setAssessment(assessment);
        for (Topic topic: assessment.getTopicConjunctions().get(0).getTopics())
            checkTopicCourse(topic);
        setPassword(tournamentDto.getPassword());
        setPrivateTournament(tournamentDto.isPrivateTournament());
    }

    public Integer getId() { return id; }

    public LocalDateTime getStartTime() { return startTime; }

    public void setStartTime(LocalDateTime startTime) {
        if (startTime == null) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "startTime");
        }
        // Added 1 minute as a buffer to take latency into consideration
        if (this.endTime != null && this.endTime.isBefore(startTime) ||
                startTime.plusMinutes(1).isBefore(DateHandler.now())) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "startTime");
        }

        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() { return endTime; }

    public void setEndTime(LocalDateTime endTime) {
        if (endTime == null) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "endTime");
        }
        if (this.startTime != null && endTime.isBefore(this.startTime)) {
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

    public User getCreator() { return creator; }

    public Status getState() { return state; }

    public void setState(Status status) { this.state = status; }

    public Set<User> getParticipants() { return participants; }

    public void setCourseExecution(User user) { this.courseExecution = user.getCourseExecutions().iterator().next(); }

    public CourseExecution getCourseExecution() { return courseExecution; }

    public Integer getQuizId() { return quizId; }

    public void setQuizId(Integer quizId) { this.quizId = quizId; }

    public Assessment getAssessment() { return assessment; }

    public void setAssessment(Assessment assessment) { this.assessment = assessment; }

    public TopicConjunction getTopicConjunction() { return assessment.getTopicConjunctions().get(0); }

    public void setTopicConjunction(TopicConjunction topicConjunction) {
        List<TopicConjunction> topicConjunctions = new ArrayList<>();
        topicConjunctions.add(topicConjunction);
        this.assessment.setTopicConjunctions(topicConjunctions);
    }

    public void updateTopics(Set<Topic> newTopics) {
        if (newTopics.isEmpty()) throw new TutorException(TOURNAMENT_MUST_HAVE_ONE_TOPIC);

        for (Topic topic : newTopics) {
            checkTopicCourse(topic);
        }

        this.getTopicConjunction().updateTopics(newTopics);
    }

    public void checkTopicCourse(Topic topic) {
        if (topic.getCourse() != courseExecution.getCourse()) {
            throw new TutorException(TOURNAMENT_TOPIC_COURSE);
        }
    }

    public void addParticipant(User user) {
        this.participants.add(user);
        user.addTournament(this);
    }

    public void removeParticipant(User user) {
        this.participants.remove(user);
        user.removeTournament(this);
    }

    public boolean hasQuiz() { return this.getQuizId() != null; }

    public void remove() {
        creator = null;
        courseExecution = null;

        getParticipants().forEach(participant -> participant.getTournaments().remove(this));
        getParticipants().clear();
    }

    public boolean isPrivateTournament() { return privateTournament; }

    public void setPrivateTournament(boolean privateTournament) { this.privateTournament = privateTournament; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
}
