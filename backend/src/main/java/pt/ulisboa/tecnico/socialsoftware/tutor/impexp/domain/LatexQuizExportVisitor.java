package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LatexQuizExportVisitor extends LatexVisitor {
    public String export(Quiz quiz) {
        quiz.accept(this);

        quiz.getQuizQuestions().stream()
                .sorted(Comparator.comparing(QuizQuestion::getSequence))
                .forEach(quizQuestion -> quizQuestion.accept(this));

        exportQuestions(quiz.getQuizQuestions().stream().map(QuizQuestion::getQuestion).collect(Collectors.toList()));

        return this.result;
    }

    private void exportQuestions(List<Question> questions) {
        for (Question question : questions) {
            question.accept(this);
        }
    }

}
