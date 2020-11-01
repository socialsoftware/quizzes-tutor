package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;

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

    default void visitDiscussion(Discussion discussion) {}

    default void visitReply(Reply reply) {}
}
