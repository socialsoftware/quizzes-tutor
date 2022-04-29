package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.List;

public class LatexQuestionExportVisitor extends LatexVisitor {
    public String export(List<Question> questions) {
        return exportQuestions(questions);
    }

    private String exportQuestions(List<Question> questions) {
        StringBuilder bld = new StringBuilder();

        for (Question question : questions) {
            question.accept(this);
            bld.append(this.result);
        }

        return bld.toString();
    }
}
