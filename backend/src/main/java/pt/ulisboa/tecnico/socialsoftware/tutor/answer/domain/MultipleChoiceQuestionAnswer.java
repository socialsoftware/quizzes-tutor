package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("MultipleChoice")
public class MultipleChoiceQuestionAnswer extends QuestionAnswer {

    @ManyToOne
    @JoinColumn(name = "option_id")
    private Option option;

    public MultipleChoiceQuestionAnswer(){

    }

    public MultipleChoiceQuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, Integer timeTaken, Option option, int sequence){
        super(quizAnswer,quizQuestion, timeTaken, sequence);
        setOption(option);
    }

    public MultipleChoiceQuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, int sequence) {
        super(quizAnswer,quizQuestion, sequence);
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;

        if (option != null)
            option.addQuestionAnswer(this);
    }

    @Override
    public boolean isCorrect() {
        return getOption() != null && getOption().getCorrect();
    }

    @Override
    protected void removeChild() {
        if (option != null) {
            option.getQuestionAnswers().remove(this);
            option = null;
        }
    }
}
