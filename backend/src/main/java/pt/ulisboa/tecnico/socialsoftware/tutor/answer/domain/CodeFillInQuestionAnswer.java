package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import org.springframework.boot.autoconfigure.condition.ConditionalOnJava;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInSpot;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CodeFillIn)
public class CodeFillInQuestionAnswer extends QuestionAnswer {

    @ManyToMany
    @JoinColumn(name = "options_in_spots_id")
    private List<FillInOption> fillInOptions = new ArrayList<>();

    public CodeFillInQuestionAnswer(){

    }

    public CodeFillInQuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, int sequence) {
        super(quizAnswer,quizQuestion, sequence);
    }

    public List<FillInOption> getFillInOptions() {
        return fillInOptions;
    }

    public void setFillInOptions(List<FillInOption> fillInOptions) {
        this.fillInOptions = fillInOptions;
    }

    /*
    public FillInOption getFillInOption() {
        return fillInOption;
    }

    public void setFillInOption(FillInOption fillInOption) {
        this.fillInOption = fillInOption;

        if (fillInOption != null) {
            //TODO CHECK IT -> fillInOption.addQuestionAnswer(this);
        }
    }*/

    @Override
    public boolean isCorrect() {
        return getFillInOptions() != null &&
                !getFillInOptions().isEmpty() &&
                getFillInOptions().stream().allMatch(FillInOption::isCorrect);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionAnswer(this);
    }

    @Override
    public void remove() {
        super.remove();
        if (getFillInOptions() != null) {
            //TODO CHECK IT fillInOption.getQuestionAnswers().remove(this);
            setFillInOptions(null);
        }
    }
}
