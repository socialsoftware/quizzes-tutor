package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
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
    @JoinColumn(name = "option_id")
    private FillInOption option;

    public CodeFillInQuestionAnswer(){

    }

    public CodeFillInQuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, int sequence) {
        super(quizAnswer,quizQuestion, sequence);
    }

    public FillInOption getOption() {
        return option;
    }

    public void setOption(FillInOption option) {
        this.option = option;

        if (option != null) {
            //TODO CHECK IT -> option.addQuestionAnswer(this);
        }
    }

    @Override
    public boolean isCorrect() {
        return getOption() != null && getOption().isCorrect();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionAnswer(this);
    }

    @Override
    public void remove() {
        super.remove();
        if (option != null) {
            //TODO CHECK IT option.getQuestionAnswers().remove(this);
            option = null;
        }
    }
}
