package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.List;

public class AnswersXmlExportVisitor implements Visitor {
    private static final String SEQUENCE = "sequence";
    public static final String QUESTION_KEY = "questionKey";

    private Element rootElement;
    private Element currentElement;
    private Element currentQuestionAnswer;

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
        quizElement.setAttribute("courseName", quizAnswer.getQuiz().getCourseExecution().getCourse().getName());
        quizElement.setAttribute("courseType", quizAnswer.getQuiz().getCourseExecution().getCourse().getType().name());
        quizElement.setAttribute("courseExecutionType", quizAnswer.getQuiz().getCourseExecution().getType().name());
        quizElement.setAttribute("acronym", quizAnswer.getQuiz().getCourseExecution().getAcronym());
        quizElement.setAttribute("academicTerm", quizAnswer.getQuiz().getCourseExecution().getAcademicTerm());
        quizElement.setAttribute("key", String.valueOf(quizAnswer.getQuiz().getNonNullKey()));
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
        quizQuestionElement.setAttribute("key", String.valueOf(questionAnswer.getQuizQuestion().getQuiz().getNonNullKey()));
        quizQuestionElement.setAttribute(SEQUENCE, String.valueOf(questionAnswer.getQuizQuestion().getSequence()));
        questionAnswerElement.addContent(quizQuestionElement);

        if (questionAnswer.getAnswerDetails() != null) {
            Element tmp = this.currentElement;
            this.currentElement = questionAnswerElement;
            this.currentQuestionAnswer = questionAnswerElement;

            questionAnswer.getAnswerDetails().accept(this);

            this.currentElement = tmp;
        }

        this.currentElement.addContent(questionAnswerElement);
    }

    @Override
    public void visitAnswerDetails(MultipleChoiceAnswer answer) {
        this.currentQuestionAnswer.setAttribute("type", Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION);
        if (answer.getOption() != null) {
            Element optionElement = new Element("option");
            optionElement.setAttribute(QUESTION_KEY, String.valueOf(answer.getQuestionAnswer().getQuestion().getKey()));
            optionElement.setAttribute(SEQUENCE, String.valueOf(answer.getOption().getSequence()));
            this.currentElement.addContent(optionElement);
        }
    }

    @Override
    public void visitAnswerDetails(CodeOrderAnswer answer) {
        this.currentQuestionAnswer.setAttribute("type", Question.QuestionTypes.CODE_ORDER_QUESTION);
        if (answer.isAnswered()){
            Element slotContainerElement = new Element("slots");
            slotContainerElement.setAttribute(QUESTION_KEY, String.valueOf(answer.getQuestionAnswer().getQuestion().getKey()));

            for (var slot:answer.getOrderedSlots()) {
                Element slotElement = new Element("slot");
                slotElement.setAttribute(SEQUENCE, String.valueOf(slot.getCodeOrderSlot().getSequence()));
                slotElement.setAttribute("order", String.valueOf(slot.getAssignedOrder()));
                slotContainerElement.addContent(slotElement);
            }

            this.currentElement.addContent(slotContainerElement);
        }
    }

    @Override
    public void visitAnswerDetails(CodeFillInAnswer answer) {
        this.currentQuestionAnswer.setAttribute("type", Question.QuestionTypes.CODE_FILL_IN_QUESTION);
        if (answer.isAnswered()){
            Element spotContainerElement = new Element("fillInSpots");
            spotContainerElement.setAttribute(QUESTION_KEY, String.valueOf(answer.getQuestionAnswer().getQuestion().getKey()));

            for (var spot:answer.getFillInOptions()) {
                Element spotElement = new Element("fillInSpot");
                spotElement.setAttribute("spotSequence", String.valueOf(spot.getFillInSpot().getSequence()));
                spotElement.setAttribute("optionSequence", String.valueOf(spot.getSequence()));
                spotContainerElement.addContent(spotElement);
            }

            this.currentElement.addContent(spotContainerElement);
        }
    }
}
