package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

public interface Visitor {
    default void visitQuestion(Question question) {}

    default void visitImage(Image image) {}

    default void visitOption(Option option) {}

    default void visitQuiz(Quiz quiz) {}

    default void visitQuizQuestion(QuizQuestion quizQuestion) {}

    default void visitUser(User user) {}

    default void visitAuthUser(AuthUser authUser) {}

    default void visitQuizAnswer(QuizAnswer quizAnswer) {}

    default void visitQuestionAnswer(QuestionAnswer questionAnswer) {}

    default void visitTopic(Topic topic) {}

    default void visitCourse(Course course) {}

    default void visitAssessment(Assessment assessment) {}

    default void visitCourseExecution(CourseExecution courseExecution) {}

    default void visitAnswerDetails(MultipleChoiceAnswer answer){}

    default void visitQuestionDetails(MultipleChoiceQuestion question) {}

    default void visitAnswerDetails(CodeFillInAnswer answer){}

    default void visitQuestionDetails(CodeFillInQuestion question) {}

    default void visitFillInSpot(CodeFillInSpot spot) {}

    default void visitFillInOption(CodeFillInOption spot) {}

    default void visitDiscussion(Discussion discussion) {}

    default void visitReply(Reply reply) {}

    default void visitAnswerDetails(CodeOrderAnswer answer){}

    default void visitQuestionDetails(CodeOrderQuestion codeOrderQuestion) {}

    default void visitCodeOrderSlot(CodeOrderSlot codeOrderSlot) {}
}
