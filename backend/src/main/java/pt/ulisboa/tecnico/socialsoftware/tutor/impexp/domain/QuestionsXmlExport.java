package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.List;

public class QuestionsXmlExport {
	public String export(List<Question> questions) {
		Element element = createHeader();

		exportQuestions(element, questions);

		XMLOutputter xml = new XMLOutputter();
		xml.setFormat(Format.getPrettyFormat());

		return xml.outputString(element);
	}

	public Element createHeader() {
		Document jdomDoc = new Document();
		Element rootElement = new Element("questions");

		jdomDoc.setRootElement(rootElement);
		return rootElement;
	}

	private void exportQuestions(Element element, List<Question> questions) {
		for (Question question : questions) {
			exportQuestion(element, question);
		}
	}

	private void exportQuestion(Element element, Question question) {
		Element questionElement = new Element("question");
		questionElement.setAttribute("course", question.getCourse().getName());
		questionElement.setAttribute("key", String.valueOf(question.getKey()));
		questionElement.setAttribute("content", question.getContent());
		questionElement.setAttribute("title", question.getTitle());
		questionElement.setAttribute("status", question.getStatus().name());

		if (question.getImage() != null) {
			exportImage(questionElement, question.getImage());
		}

		exportOptions(questionElement, question.getOptions());

		element.addContent(questionElement);
	}

	private void exportImage(Element questionElement, Image image) {
		Element imageElement = new Element("image");
		if (image.getWidth() != null) {
			imageElement.setAttribute("width",String.valueOf(image.getWidth()));
		}
		imageElement.setAttribute("url", image.getUrl());

		questionElement.addContent(imageElement);
	}

	private void exportOptions(Element questionElement, List<Option> options) {
		Element optionsElement = new Element("options");

		for (Option option: options) {
			exportOption(optionsElement, option);
		}

		questionElement.addContent(optionsElement);
	}

	private void exportOption(Element optionsElement, Option option) {
		Element optionElement = new Element("option");

		optionElement.setAttribute("sequence", String.valueOf(option.getSequence()));
		optionElement.setAttribute("content", option.getContent());
		optionElement.setAttribute("correct", String.valueOf(option.getCorrect()));

		optionsElement.addContent(optionElement);
	}

}
