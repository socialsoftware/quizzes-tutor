package com.example.tutor.stats;


import java.io.Serializable;

public class StatsDTO implements Serializable {
    private Integer totalQuizzes;
    private Integer totalAnswers;
    private Integer uniqueCorrectAnswers;
    private Integer uniqueWrongAnswers;
    private Integer totalUniqueQuestions;
    //private TopicsStatsDTO[] topics;
    //private AnsweredQuizDTO[] quizzes;

    public StatsDTO(Integer totalQuizzes, Integer totalAnswers, Integer uniqueCorrectAnswers, Integer uniqueWrongAnswers, Integer totalUniqueQuestions) {
        this.totalQuizzes = totalQuizzes;
        this.totalAnswers = totalAnswers;
        this.uniqueCorrectAnswers = uniqueCorrectAnswers;
        this.uniqueWrongAnswers = uniqueWrongAnswers;
        this.totalUniqueQuestions = totalUniqueQuestions;
    }

    public Integer getTotalQuizzes() {
        return totalQuizzes;
    }

    public void setTotalQuizzes(Integer totalQuizzes) {
        this.totalQuizzes = totalQuizzes;
    }

    public Integer getTotalAnswers() {
        return totalAnswers;
    }

    public void setTotalAnswers(Integer totalAnswers) {
        this.totalAnswers = totalAnswers;
    }

    public Integer getUniqueCorrectAnswers() {
        return uniqueCorrectAnswers;
    }

    public void setUniqueCorrectAnswers(Integer uniqueCorrectAnswers) {
        this.uniqueCorrectAnswers = uniqueCorrectAnswers;
    }

    public Integer getUniqueWrongAnswers() {
        return uniqueWrongAnswers;
    }

    public void setUniqueWrongAnswers(Integer uniqueWrongAnswers) {
        this.uniqueWrongAnswers = uniqueWrongAnswers;
    }

    public Integer getTotalUniqueQuestions() {
        return totalUniqueQuestions;
    }

    public void setTotalUniqueQuestions(Integer totalUniqueQuestions) {
        this.totalUniqueQuestions = totalUniqueQuestions;
    }
}
