package pt.ulisboa.tecnico.socialsoftware.apigateway.auth.domain;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class UserSecurityInfo {

    @Column(name = "user_id")
    private Integer id;

    @Column(name="user_name")
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "boolean default false")
    private Boolean admin;


    public UserSecurityInfo() {
    }

    public UserSecurityInfo(Integer id, String name, Role role, Boolean admin) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {
        return this.admin != null && this.admin;
    }

    public boolean isTeacher() { return this.role == Role.TEACHER; }

    public boolean isStudent() { return this.role == Role.STUDENT; }
}
