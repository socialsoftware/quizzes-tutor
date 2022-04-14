package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.WeeklyScore;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.io.Serializable;

public class WeeklyScoreDto implements Serializable {

  private Integer id;

  private int quizzesAnswered;
  private int questionsAnswered;
  private int questionsUniquelyAnswered;
  private int percentageCorrect;
  private int improvedCorrectAnswers;

  private String week;

  public WeeklyScoreDto() {
  }

  public WeeklyScoreDto(WeeklyScore weeklyScore) {
    setId(weeklyScore.getId());
    setQuizzesAnswered(weeklyScore.getQuizzesAnswered());
    setQuestionsAnswered(weeklyScore.getQuestionsAnswered());
    setQuestionsUniquelyAnswered(weeklyScore.getQuestionsUniquelyAnswered());
    setPercentageCorrect(weeklyScore.getPercentageCorrect());
    setImprovedCorrectAnswers(weeklyScore.getImprovedCorrectAnswers());
    setWeek(DateHandler.toISOString(weeklyScore.getWeek().atStartOfDay()));
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public int getQuizzesAnswered() {
    return quizzesAnswered;
  }

  public void setQuizzesAnswered(int quizzesAnswered) {
    this.quizzesAnswered = quizzesAnswered;
  }

  public int getQuestionsAnswered() {
    return questionsAnswered;
  }

  public void setQuestionsAnswered(int questionsAnswered) {
    this.questionsAnswered = questionsAnswered;
  }

  public int getQuestionsUniquelyAnswered() {
    return questionsUniquelyAnswered;
  }

  public void setQuestionsUniquelyAnswered(int questionsUniquelyAnswered) {
    this.questionsUniquelyAnswered = questionsUniquelyAnswered;
  }

  public int getPercentageCorrect() {
    return percentageCorrect;
  }

  public void setPercentageCorrect(int percentageCorrect) {
    this.percentageCorrect = percentageCorrect;
  }

  public int getImprovedCorrectAnswers() {
    return improvedCorrectAnswers;
  }

  public void setImprovedCorrectAnswers(int improvedCorrectAnswers) {
    this.improvedCorrectAnswers = improvedCorrectAnswers;
  }

  public String getWeek() {
    return week;
  }

  public void setWeek(String week) {
    this.week = week;
  }
}
