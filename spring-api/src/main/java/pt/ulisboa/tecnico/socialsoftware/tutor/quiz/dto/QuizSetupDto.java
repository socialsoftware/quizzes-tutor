package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto;

public class QuizSetupDto {
    private Integer userId;
    private Integer numberOfQuestions;
    private String questionType;
    private String[] topics;

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }
}
