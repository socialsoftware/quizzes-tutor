package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.Review;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type",
        discriminatorType = DiscriminatorType.STRING)
public abstract class User implements DomainEntity {
    public enum Role {STUDENT, TEACHER, ADMIN, DEMO_ADMIN}
    public static class UserTypes {
        public static final String STUDENT = "student";
        public static final String TEACHER = "teacher";
        public static final String DEMO_ADMIN = "demo_admin";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true)
    private Integer key;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String name;

    @Column(columnDefinition = "boolean default false")
    private Boolean admin;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval=true)
    public AuthUser authUser;

    @ManyToMany
    private Set<CourseExecution> courseExecutions = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Reply> replies = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Review> reviews = new HashSet<>();

    public User() {
    }

    public User(String name, String username, String email, Role role, boolean isAdmin, AuthUser.Type type){
        setName(name);
        setRole(role);
        setAdmin(isAdmin);
        setAuthUser(AuthUser.createAuthUser(this, username, email, type));
        setCreationDate(DateHandler.now());
    }

    public User(String name, User.Role role, boolean isAdmin){
        setName(name);
        setRole(role);
        setAdmin(isAdmin);
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

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Set<Reply> getReplies() {
        return replies;
    }

    public void setReplies(Set<Reply> replies) {
        this.replies = replies;
    }

    public String getUsername() {
        if (authUser == null) {
            return String.format("%s-%s", getRole().toString().toLowerCase(), getId());
        }
        return authUser.getUsername();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return this.admin != null && this.admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
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

    public Set<CourseExecution> getCourseExecutions() {
        return courseExecutions;
    }

    public void setCourseExecutions(Set<CourseExecution> courseExecutions) {
        this.courseExecutions = courseExecutions;
    }
    
    public String getEmail() {
        return authUser.getEmail();
    }

    public AuthUser getAuthUser() {
        return authUser;
    }

    public void setAuthUser(AuthUser authUser) {
        this.authUser = authUser;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", key=" + key +
                ", role=" + role +
                ", username='" + getUsername() + '\'' +
                ", name='" + name + '\'' +
                ", creationDate=" + creationDate +
                ", lastAccess=" + authUser.getLastAccess() +
                '}';
    }

    public void addCourse(CourseExecution course) {
        this.courseExecutions.add(course);
        course.addUser(this);
    }

    public Set<Review> getReviews() { return reviews; }

    public boolean isStudent() { return this.role == User.Role.STUDENT; }

    public boolean isTeacher() { return this.role == User.Role.TEACHER; }

    public void remove() {
        if (getAuthUser() != null && !getAuthUser().isDemoStudent() && getAuthUser().isActive()) {
                throw new TutorException(USER_IS_ACTIVE, getUsername());
        }

        if (!replies.isEmpty()) {
            throw new TutorException(USER_HAS_REPLIES, getUsername());
        }

        courseExecutions.forEach(ce -> ce.getUsers().remove(this));
        courseExecutions.clear();
    }
}