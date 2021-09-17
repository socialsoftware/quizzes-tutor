package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XMLQuestionExportVisitor implements Visitor {
    public static final String CONTENT = "content";
    public static final String SEQUENCE = "sequence";
    private Element rootElement;
    private Element currentElement;

    public String export(List<Question> questions) {
        createHeader();

        exportQuestions(questions);

        XMLOutputter xml = new XMLOutputter();
        xml.setFormat(Format.getPrettyFormat());

        return xml.outputString(this.rootElement);
    }

    public void createHeader() {
        Document jdomDoc = new Document();
        rootElement = new Element("questions");

        jdomDoc.setRootElement(rootElement);
        this.currentElement = rootElement;
    }

    private void exportQuestions(List<Question> questions) {
        Map<Course, List<Question>> questionMap = questions.stream().collect(Collectors.groupingBy(Question::getCourse));

        for (var courseQuestions : questionMap.entrySet()) {
            Element courseElement = new Element("course");
            courseElement.setAttribute("courseType", courseQuestions.getKey().getType().name());
            courseElement.setAttribute("courseName", courseQuestions.getKey().getName());

            this.currentElement.addContent(courseElement);
            this.currentElement = courseElement;

            courseQuestions.getValue().stream()
                    .sorted(Comparator.comparing(Question::getStatus))
                    .forEach(question -> question.accept(this));

            this.currentElement = this.rootElement;
        }
    }

    @Override
    public void visitQuestion(Question question) {
        Element questionElement = new Element("question");
        questionElement.setAttribute("key", String.valueOf(question.getKey()));
        questionElement.setAttribute(CONTENT, question.getContent());
        questionElement.setAttribute("title", question.getTitle());
        questionElement.setAttribute("status", question.getStatus().name());

        if (question.getCreationDate() != null)
            questionElement.setAttribute("creationDate", DateHandler.toISOString(question.getCreationDate()));
        this.currentElement.addContent(questionElement);

        Element previousCurrent = this.currentElement;
        this.currentElement = questionElement;

        if (question.getImage() != null)
            question.getImage().accept(this);

        Element questionTopics = new Element("topics");
        for (var topic: question.getTopics()) {
            Element questionTopic = new Element("topic");
            questionTopic.setAttribute("name", topic.getName());
            questionTopics.addContent(questionTopic);
        }
        questionElement.addContent(questionTopics);

        question.getQuestionDetails().accept(this);

        this.currentElement = previousCurrent;
    }

    @Override
    public void visitQuestionDetails(MultipleChoiceQuestion question) {
        this.currentElement.setAttribute("type", Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION);

        Element optionsElement = new Element("options");
        this.currentElement.addContent(optionsElement);

        this.currentElement = optionsElement;
        question.visitOptions(this);
    }

    @Override
    public void visitQuestionDetails(CodeFillInQuestion question) {
        this.currentElement.setAttribute("type", Question.QuestionTypes.CODE_FILL_IN_QUESTION);

        Element codeElement = new Element("code");
        codeElement.setAttribute("language", question.getLanguage().toString());
        codeElement.addContent(question.getCode());
        this.currentElement.addContent(codeElement);

        Element spotsElement = new Element("fillInSpots");
        this.currentElement.addContent(spotsElement);

        this.currentElement = spotsElement;
        question.visitFillInSpots(this);
    }

    @Override
    public void visitFillInSpot(CodeFillInSpot spot) {
        Element spotElement = new Element("fillInSpot");

        spotElement.setAttribute(SEQUENCE, String.valueOf(spot.getSequence()));
        this.currentElement.addContent(spotElement);
        var oldElement = this.currentElement;
        this.currentElement = spotElement;

        spot.visitOptions(this);

        this.currentElement = oldElement;
    }

    @Override
    public void visitFillInOption(CodeFillInOption option) {
        Element optionElement = new Element("fillInOption");

        optionElement.setAttribute(SEQUENCE, String.valueOf(option.getSequence()));
        optionElement.setAttribute(CONTENT, option.getContent());
        optionElement.setAttribute("correct", String.valueOf(option.isCorrect()));

        this.currentElement.addContent(optionElement);
    }

    @Override
    public void visitImage(Image image) {
        Element imageElement = new Element("image");
        if (image.getWidth() != null) {
            imageElement.setAttribute("width", String.valueOf(image.getWidth()));
        }
        imageElement.setAttribute("url", image.getUrl());

        this.currentElement.addContent(imageElement);
    }

    @Override
    public void visitOption(Option option) {
        Element optionElement = new Element("option");

        optionElement.setAttribute(SEQUENCE, String.valueOf(option.getSequence()));
        optionElement.setAttribute(CONTENT, option.getContent());
        optionElement.setAttribute("correct", String.valueOf(option.isCorrect()));

        this.currentElement.addContent(optionElement);
    }

    @Override
    public void visitQuestionDetails(CodeOrderQuestion question) {
        this.currentElement.setAttribute("type", Question.QuestionTypes.CODE_ORDER_QUESTION);

        Element codeElement = new Element("orderSlots");
        codeElement.setAttribute("language", question.getLanguage().toString());
        this.currentElement.addContent(codeElement);

        this.currentElement = codeElement;
        question.visitCodeOrderSlots(this);
    }

    @Override
    public void visitCodeOrderSlot(CodeOrderSlot codeOrderSlot) {
        Element spotElement = new Element("slot");

        spotElement.setAttribute("order", String.valueOf(codeOrderSlot.getOrder()));
        spotElement.setAttribute(SEQUENCE, String.valueOf(codeOrderSlot.getSequence()));
        spotElement.addContent(codeOrderSlot.getContent());
        this.currentElement.addContent(spotElement);
    }
}
