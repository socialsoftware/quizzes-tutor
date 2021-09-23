package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.List;

public class LatexQuestionExportVisitor extends LatexVisitor {
    public String export(List<Question> questions) {
        return exportQuestions(questions);
    }

    private String exportQuestions(List<Question> questions) {
        String result = "";

        for (Question question : questions) {
            question.accept(this);
            result = result + this.result;
        }

        return result;
    }
}
