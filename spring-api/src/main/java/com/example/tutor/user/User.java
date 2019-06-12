package com.example.tutor.user;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "Users")
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "username")
    private String username;

    @Column(columnDefinition = "name")
    private String name;

    @Column(columnDefinition = "year")
    private Integer year;

    @Column(columnDefinition = "role")
    private String role;

    public User() {

    }

    public User(String name, String username, String role) {
        this.name = name;
        this.username = username;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}