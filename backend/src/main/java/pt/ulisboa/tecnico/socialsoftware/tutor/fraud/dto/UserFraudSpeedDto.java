package pt.ulisboa.tecnico.socialsoftware.tutor.fraud.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * A student whose answers were quicker, or more even, than a person can read and decide. The
 * level is NONE, LOW, MEDIUM or HIGH; the reasons say why in words. A sign for the teacher to look
 * at, never proof.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserFraudSpeedDto implements Serializable {
    private UserFraudInfoDto userInfo;
    private Integer answered;
    private Integer tooFast;
    private Double meanSeconds;
    private Integer points;
    private String level;
    private List<String> reasons;

    public UserFraudSpeedDto() {
    }

    public UserFraudInfoDto getUserInfo() { return userInfo; }

    public void setUserInfo(UserFraudInfoDto userInfo) { this.userInfo = userInfo; }

    public Integer getAnswered() { return answered; }

    public void setAnswered(Integer answered) { this.answered = answered; }

    public Integer getTooFast() { return tooFast; }

    public void setTooFast(Integer tooFast) { this.tooFast = tooFast; }

    public Double getMeanSeconds() { return meanSeconds; }

    public void setMeanSeconds(Double meanSeconds) { this.meanSeconds = meanSeconds; }

    public Integer getPoints() { return points; }

    public void setPoints(Integer points) { this.points = points; }

    public String getLevel() { return level; }

    public void setLevel(String level) { this.level = level; }

    public List<String> getReasons() { return reasons; }

    public void setReasons(List<String> reasons) { this.reasons = reasons; }
}
