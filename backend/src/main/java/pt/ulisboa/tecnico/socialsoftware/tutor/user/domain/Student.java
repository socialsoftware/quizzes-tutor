package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@DiscriminatorValue(User.UserTypes.STUDENT)
public class Student extends User {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "student", fetch = FetchType.LAZY)
    private Set<QuizAnswer> quizAnswers = new HashSet<>();

    @ManyToMany(mappedBy = "participants")
    private Set<Tournament> tournaments = new HashSet<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private Set<Discussion> discussions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "submitter", fetch = FetchType.LAZY)
    private Set<QuestionSubmission> questionSubmissions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "student", orphanRemoval = true)
    private Set<Dashboard> dashboards = new HashSet<>();

    public Student() {
    }

    public Student(String name, String username, String email, boolean isAdmin, AuthUser.Type type) {
        super(name, username, email, Role.STUDENT, isAdmin, type);
    }

    public Student(String name, boolean isAdmin) {
        super(name, Role.STUDENT, isAdmin);
    }

    public void setQuizAnswers(Set<QuizAnswer> quizAnswers) {
        this.quizAnswers = quizAnswers;
    }

    public Set<QuizAnswer> getQuizAnswers() {
        return quizAnswers;
    }

    public void addQuizAnswer(QuizAnswer quizAnswer) {
        this.quizAnswers.add(quizAnswer);
    }

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

    @Override
    public void remove() {
        if (!quizAnswers.isEmpty()) {
            throw new TutorException(USER_HAS_QUIZ_ANSWERS, getUsername());
        }

        if (!questionSubmissions.isEmpty()) {
            throw new TutorException(USER_HAS_QUESTION_SUBMISSIONS, getUsername());
        }

        if (!discussions.isEmpty()) {
            throw new TutorException(USER_HAS_DISCUSSIONS, getUsername());
        }

        dashboards.forEach(Dashboard::remove);
        dashboards.clear();

        super.remove();
    }

    public Set<Tournament> getTournaments() {
        return tournaments;
    }

    public void addTournament(Tournament tournament) {
        this.tournaments.add(tournament);
    }

    public void removeTournament(Tournament tournament) {
        this.tournaments.remove(tournament);
    }

    public Set<Discussion> getDiscussions() {
        return discussions;
    }

    public void addDiscussion(Discussion discussion) {
        this.discussions.add(discussion);
    }

    public void setDiscussions(Set<Discussion> discussions) {
        this.discussions = discussions;
    }

    public Integer getNumAnsweredDiscussions() {
        return (int) this.getDiscussions().stream().filter(Discussion::teacherAnswered).count();
    }


    public void setQuestionSubmissions(Set<QuestionSubmission> questionSubmissions) {
        this.questionSubmissions = questionSubmissions;
    }

    public void addQuestionSubmission(QuestionSubmission questionSubmission) {
        questionSubmissions.add(questionSubmission);
    }

    public Set<QuestionSubmission> getQuestionSubmissions() {
        return questionSubmissions;
    }

    public Set<Dashboard> getDashboards() {
        return dashboards;
    }

    public void addDashboard(Dashboard dashboard) {
        dashboards.add(dashboard);
    }

    public Dashboard getCourseExecutionDashboard(CourseExecution courseExecution) {
        return dashboards.stream()
                .filter(dashboard -> dashboard.getCourseExecution() == courseExecution)
                .findAny()
                .orElse(null);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + getId() +
                ", key=" + getKey() +
                ", role=" + getRole() +
                ", username='" + getUsername() + '\'' +
                ", name='" + getName() + '\'' +
                ", creationDate=" + getCreationDate() +
                ", lastAccess=" + authUser.getLastAccess() +
                '}';
    }

}
