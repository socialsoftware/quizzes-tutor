package pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;
import java.util.Map;

@Entity
@DiscriminatorValue(Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION)
public class CodeFillInAnswerItem extends QuestionAnswerItem {

    // TODO IMPLEMENTAR OPTIONS
    //private List<Integer> optionId;

    public CodeFillInAnswerItem() {
    }

    public CodeFillInAnswerItem(String username, int quizId, StatementAnswerDto answer) {
        super(username, quizId, answer);
    }

    @Override
    public String getAnswerRepresentation(Map<Integer, Option> options) {
        // TODO: IMPLEMENTAR
        return null;
    }
}
