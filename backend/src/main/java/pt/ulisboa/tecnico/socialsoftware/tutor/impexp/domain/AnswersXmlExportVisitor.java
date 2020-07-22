package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.List;

public class AnswersXmlExportVisitor implements Visitor {
    private static final String SEQUENCE = "sequence";

    private Element rootElement;
    private Element currentElement;

    public String export(List<QuizAnswer> quizAnswers) {
        createHeader();

        exportQuizAnswers(quizAnswers);

        XMLOutputter xml = new XMLOutputter();
        xml.setFormat(Format.getPrettyFormat());

        return xml.outputString(this.rootElement);
    }

    private void createHeader() {
        Document jdomDoc = new Document();
        this.rootElement = new Element("quizAnswers");
        jdomDoc.setRootElement(this.rootElement);
        this.currentElement = rootElement;
    }

    private void exportQuizAnswers(List<QuizAnswer> quizAnswers) {
        for (QuizAnswer quizAnswer : quizAnswers) {
            quizAnswer.accept(this);
        }
    }

    private void exportQuestionAnswers(List<QuestionAnswer> questionAnswers) {
        this.currentElement = new Element("questionAnswers");

        for (QuestionAnswer questionAnswer : questionAnswers) {
            questionAnswer.accept(this);
        }
    }

    @Override
    public void visitQuizAnswer(QuizAnswer quizAnswer) {
        Element quizAnswerElement = new Element("quizAnswer");

        if (quizAnswer.getAnswerDate() != null) {
            quizAnswerElement.setAttribute("answerDate", String.valueOf(quizAnswer.getAnswerDate()));
        }

        quizAnswerElement.setAttribute("completed", String.valueOf(quizAnswer.isCompleted()));

        Element quizElement = new Element("quiz");
        quizElement.setAttribute("key", String.valueOf(quizAnswer.getQuiz().getKey()));
        quizAnswerElement.addContent(quizElement);

        Element userElement = new Element("user");
        userElement.setAttribute("key", String.valueOf(quizAnswer.getUser().getKey()));
        quizAnswerElement.addContent(userElement);

        exportQuestionAnswers(quizAnswer.getQuestionAnswers());

        quizAnswerElement.addContent(this.currentElement);
        this.rootElement.addContent(quizAnswerElement);
    }

    @Override
    public void visitQuestionAnswer(QuestionAnswer questionAnswer) {
        Element questionAnswerElement = new Element("questionAnswer");

        if (questionAnswer.getTimeTaken() != null) {
            questionAnswerElement.setAttribute("timeTaken", String.valueOf(questionAnswer.getTimeTaken()));
        }

        questionAnswerElement.setAttribute(SEQUENCE, String.valueOf(questionAnswer.getSequence()));

        Element quizQuestionElement = new Element("quizQuestion");
        quizQuestionElement.setAttribute("key", String.valueOf(questionAnswer.getQuizQuestion().getQuiz().getKey()));
        quizQuestionElement.setAttribute(SEQUENCE, String.valueOf(questionAnswer.getQuizQuestion().getSequence()));
        quizQuestionElement.setAttribute("type", Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION);
        questionAnswerElement.addContent(quizQuestionElement);

        if (questionAnswer.getAnswerDetails() != null) {
            Element tmp = this.currentElement;
            this.currentElement = questionAnswerElement;

            questionAnswer.getAnswerDetails().accept(this);

            this.currentElement = tmp;
        }

        this.currentElement.addContent(questionAnswerElement);
    }

    @Override
    public void visitAnswerDetails(MultipleChoiceAnswer answer) {
        if (answer.getOption() != null) {
            Element optionElement = new Element("option");
            optionElement.setAttribute("questionKey", String.valueOf(answer.getOption().getQuestionDetails().getQuestion().getKey()));
            optionElement.setAttribute(SEQUENCE, String.valueOf(answer.getOption().getSequence()));
            this.currentElement.addContent(optionElement);
        }
    }
}
