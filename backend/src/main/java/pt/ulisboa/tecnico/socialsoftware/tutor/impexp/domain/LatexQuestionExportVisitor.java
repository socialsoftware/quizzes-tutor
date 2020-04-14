package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.List;

public class LatexQuestionExportVisitor extends LatexVisitor {
    public String export(List<Question> questions) {
        exportQuestions(questions);

        return this.result;
    }

    private void exportQuestions(List<Question> questions) {
        for (Question question : questions) {
            question.accept(this);
        }
    }
}
