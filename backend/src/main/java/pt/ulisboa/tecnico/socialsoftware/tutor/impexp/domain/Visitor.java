package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

public interface Visitor {
    default void visitQuestion(Question question) {};

    default void visitImage(Image image) {};

    default void visitOption(Option option) {};

    default void visitQuiz(Quiz quiz) {};

    default void visitQuizQuestion(QuizQuestion quizQuestion) {};

    default void visitUser(User user) {};

    default void visitQuizAnswer(QuizAnswer quizAnswer) {};

    default void visitQuestionAnswer(QuestionAnswer questionAnswer) {};

    default String convertSequenceToLetter(Integer value) {
        switch (value) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            default:
                return "X";
        }
    }
}
