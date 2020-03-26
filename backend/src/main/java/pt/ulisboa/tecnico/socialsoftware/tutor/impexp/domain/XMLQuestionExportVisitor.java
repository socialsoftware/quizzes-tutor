package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class XMLQuestionExportVisitor implements Visitor {
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
        for (Question question : questions) {
            question.accept(this);
        }
    }

    @Override
    public void visitQuestion(Question question) {
        DateTimeFormatter formatter = Course.formatter;

        Element questionElement = new Element("question");
        questionElement.setAttribute("courseType", question.getCourse().getType().name());
        questionElement.setAttribute("courseName", question.getCourse().getName());
        questionElement.setAttribute("key", String.valueOf(question.getKey()));
        questionElement.setAttribute("content", question.getContent());
        questionElement.setAttribute("title", question.getTitle());
        questionElement.setAttribute("status", question.getStatus().name());
        if (question.getCreationDate() != null)
            questionElement.setAttribute("creationDate", question.getCreationDate().format(formatter));
        this.currentElement.addContent(questionElement);

        this.currentElement = questionElement;

        if (question.getImage() != null)
            question.getImage().accept(this);

        Element optionsElement = new Element("options");
        this.currentElement.addContent(optionsElement);

        this.currentElement = optionsElement;
        for (Option option: question.getOptions()) {
            option.accept(this);
        }

        this.currentElement = this.rootElement;
    }

    @Override
    public void visitImage(Image image) {
        Element imageElement = new Element("image");
        if (image.getWidth() != null) {
            imageElement.setAttribute("width",String.valueOf(image.getWidth()));
        }
        imageElement.setAttribute("url", image.getUrl());

        this.currentElement.addContent(imageElement);
    }

    @Override
    public void visitOption(Option option) {
        Element optionElement = new Element("option");

        optionElement.setAttribute("sequence", String.valueOf(option.getSequence()));
        optionElement.setAttribute("content", option.getContent());
        optionElement.setAttribute("correct", String.valueOf(option.getCorrect()));

        this.currentElement.addContent(optionElement);
    }
}
