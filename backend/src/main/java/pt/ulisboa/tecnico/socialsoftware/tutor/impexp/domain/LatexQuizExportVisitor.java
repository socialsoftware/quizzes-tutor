package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LatexQuizExportVisitor extends LatexVisitor {
    public String exportQuiz(Quiz quiz) {
        quiz.accept(this);

        return this.result;
    }

    public String exportQuestions(List<Question> questions) {
        String result = "";

        for (Question question : questions) {
            question.accept(this);
            result = result + this.result;
        }

        return result;
    }
}
