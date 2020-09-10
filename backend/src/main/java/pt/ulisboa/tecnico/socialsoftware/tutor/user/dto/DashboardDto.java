package pt.ulisboa.tecnico.socialsoftware.tutor.user.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;

public class DashboardDto implements Serializable {
    private String name;
    private String username;
    private Integer numDiscussions;
    private Integer numAnsweredDiscussions;

    public DashboardDto() {
    }

    public DashboardDto(User user) {
        this.name = user.getName();
        this.username = user.getUsername();
        this.numDiscussions = user.getDiscussions().size();
        this.numAnsweredDiscussions = user.getDiscussions().size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getNumDiscussions() {
        return numDiscussions;
    }

    public void setNumDiscussions(Integer numDiscussions) {
        this.numDiscussions = numDiscussions;
    }

    public Integer getNumAnsweredDiscussions() {
        return numAnsweredDiscussions;
    }

    public void setNumAnsweredDiscussions(Integer numAnsweredDiscussions) {
        this.numAnsweredDiscussions = numAnsweredDiscussions;
    }
}
