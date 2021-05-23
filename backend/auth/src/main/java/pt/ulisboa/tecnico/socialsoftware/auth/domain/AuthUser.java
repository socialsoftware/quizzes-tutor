package pt.ulisboa.tecnico.socialsoftware.auth.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.auth.AuthDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.auth.AuthUserDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionStatus;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.UnsupportedStateTransitionException;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUserState.*;
import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.*;

@Entity
@Table(name = "auth_users",
        indexes = {
                @Index(name = "auth_users_indx_0", columnList = "username")
        })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="auth_type",
        discriminatorType = DiscriminatorType.STRING)
public abstract class AuthUser implements /*DomainEntity,*/ UserDetails {

    public enum Type { EXTERNAL, TECNICO, DEMO }

    public static final String MAIL_FORMAT = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    private AuthUserState state;

    private String email;
    private String password;

    @Column(unique=true)
    private String username;

    @Column(name = "last_access")
    private LocalDateTime lastAccess;

    @Embedded
    private UserSecurityInfo userSecurityInfo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "authuser_course_executions")
    private Set<Integer> userCourseExecutions = new HashSet<>();

    protected AuthUser() {}

    protected AuthUser(UserSecurityInfo userSecurityInfo, String username, String email) {
        setUserSecurityInfo(userSecurityInfo);
        setUsername(username);
        setEmail(email);
        setState(APPROVAL_PENDING);
    }

    public static AuthUser createAuthUser(UserSecurityInfo userSecurityInfo, String username, String email, Type type) {
        switch (type) {
            case EXTERNAL:
                return new AuthExternalUser(userSecurityInfo, username, email);
            case TECNICO:
                return new AuthTecnicoUser(userSecurityInfo, username, email);
            case DEMO:
                return new AuthDemoUser(userSecurityInfo, username, email);
            default:
                throw new TutorException(INVALID_TYPE_FOR_AUTH_USER);
        }
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public UserSecurityInfo getUserSecurityInfo() {
        return userSecurityInfo;
    }

    public void setUserSecurityInfo(UserSecurityInfo userSecurityInfo) {
        this.userSecurityInfo = userSecurityInfo;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username != null ? username.toLowerCase() : null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches(MAIL_FORMAT))
            throw new TutorException(INVALID_EMAIL, email);

        this.email = email.toLowerCase();
    }

    public Set<Integer> getUserCourseExecutions() {
        return userCourseExecutions;
    }

    public void setUserCourseExecutions(Set<Integer> userCourseExecutions) {
        this.userCourseExecutions = userCourseExecutions;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    public boolean isActive() {
        return true;
    }

    public abstract Type getType();

    public void checkRole(boolean isActive) {
        if (!isActive && !(userSecurityInfo.getRole().equals(Role.STUDENT) || userSecurityInfo.getRole().equals(Role.TEACHER))) {
            throw new TutorException(INVALID_ROLE, userSecurityInfo.getRole().toString());
        }
    }

    public AuthUserState getState() {
        return state;
    }

    public void setState(AuthUserState state) {
        this.state = state;
    }

    /*@Override
    public void accept(Visitor visitor) {
        visitor.visitAuthUser(this);
    }*/
 
    public boolean isDemoStudent() {
        return false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();

        list.add(new SimpleGrantedAuthority("ROLE_" + userSecurityInfo.getRole()));

        if (userSecurityInfo.isAdmin())
            list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return list;
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

    public void remove() {
        setUserSecurityInfo(null);
        setUserCourseExecutions(null);
    }

    public void addCourseExecution(Integer courseExecutionId) {
        getUserCourseExecutions().add(courseExecutionId);
    }

    @Override
    public String toString() {
        return "AuthUser{" +
                "id=" + id +
                ", version=" + version +
                ", state=" + state +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", lastAccess=" + lastAccess +
                ", userSecurityInfo=" + userSecurityInfo +
                ", userCourseExecutions=" + userCourseExecutions +
                '}';
    }

    public AuthDto getAuthDto(String token, List<CourseExecutionDto> currentCourses, List<CourseExecutionDto> courseExecutionList) {
        AuthDto dto = new AuthDto();
        AuthUserDto authUserDto = getAuthUserDto(currentCourses, courseExecutionList);
        dto.setToken(token);
        dto.setUser(authUserDto);
        return dto;
    }

    public AuthUserDto getAuthUserDto(List<CourseExecutionDto> currentCourses, List<CourseExecutionDto> courseExecutionList) {
        AuthUserDto dto = new AuthUserDto();
        dto.setId(getUserSecurityInfo().getId());
        dto.setName(getUserSecurityInfo().getName());
        dto.setUsername(getUsername());
        dto.setEmail(getEmail());
        dto.setRole(getUserSecurityInfo().getRole());
        dto.setAdmin(getUserSecurityInfo().isAdmin());
        dto.setCourses(getActiveAndInactiveCourses(courseExecutionList, Objects.requireNonNullElseGet(currentCourses, ArrayList::new)));
        return dto;
    }

    private Map<String, List<CourseExecutionDto>> getActiveAndInactiveCourses(List<CourseExecutionDto> courseExecutions, List<CourseExecutionDto> courses) {
        courses.stream()
                .forEach(courseDto -> {
                    if (courseExecutions.stream().noneMatch(c -> c.getAcronym().equals(courseDto.getAcronym()) && c.getAcademicTerm().equals(courseDto.getAcademicTerm()))) {
                        if (courseDto.getStatus() == null) {
                            courseDto.setStatus(CourseExecutionStatus.INACTIVE);
                        }
                        courseExecutions.add(courseDto);
                    }
                });

        return courseExecutions.stream().sorted(Comparator.comparing(CourseExecutionDto::getName))
                .collect(Collectors.groupingBy(CourseExecutionDto::getAcademicTerm,
                        Collectors.mapping(courseDto -> courseDto, Collectors.toList())));
    }

    public void authUserApproved(Integer userId, Integer courseExecutionId, boolean isActive) {
        switch (state) {
            case APPROVAL_PENDING:
                getUserSecurityInfo().setId(userId);
                addCourseExecution(courseExecutionId);
                this.state = APPROVED;
                break;
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    public void authUserRejected() {
        switch (state) {
            case APPROVAL_PENDING:
                this.state = REJECTED;
                break;
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }
}