package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

public interface Visitor {
    default void visitQuestion(Question question) {};

    default void visitImage(Image image) {};

    default void visitOption(Option option) {};

    default void visitQuiz(Quiz quiz) {};

    default void visitQuizQuestion(QuizQuestion quizQuestion) {};
}
