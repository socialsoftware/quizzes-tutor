package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue(Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION)
public class MultipleChoiceQuestionAnswer extends QuestionAnswer {

    @ManyToOne
    @JoinColumn(name = "multiple_choice_option_id")
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
    public MultipleChoiceQuestion getQuestion(){
        return (MultipleChoiceQuestion) getQuizQuestion().getQuestion();
    }

    @Override
    public boolean isCorrect() {
        return getOption() != null && getOption().getCorrect();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionAnswer(this);
    }

    @Override
    public void remove() {
        super.remove();
        if (option != null) {
            option.getQuestionAnswers().remove(this);
            option = null;
        }
    }
}
