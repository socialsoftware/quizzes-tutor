package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CodeFillIn)
public class CodeFillInQuestionAnswer extends QuestionAnswer {

    @ManyToOne
    @JoinColumn(name = "code_fill_in_option_id")
    private FillInOption fillInOption;

    public CodeFillInQuestionAnswer(){

    }

    public CodeFillInQuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, int sequence) {
        super(quizAnswer,quizQuestion, sequence);
    }

    public FillInOption getFillInOption() {
        return fillInOption;
    }

    public void setFillInOption(FillInOption fillInOption) {
        this.fillInOption = fillInOption;

        if (fillInOption != null) {
            //TODO CHECK IT -> fillInOption.addQuestionAnswer(this);
        }
    }

    @Override
    public boolean isCorrect() {
        return getFillInOption() != null && getFillInOption().isCorrect();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionAnswer(this);
    }

    @Override
    public void remove() {
        super.remove();
        if (fillInOption != null) {
            //TODO CHECK IT fillInOption.getQuestionAnswers().remove(this);
            fillInOption = null;
        }
    }
}
