package pt.ulisboa.tecnico.socialsoftware.tutor.impexp;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;

import java.util.List;

public class TopicsXmlExport {
	public String export(List<Topic> topics) {
		Element element = createHeader();

		exportTopics(element, topics);

		XMLOutputter xml = new XMLOutputter();
		xml.setFormat(Format.getPrettyFormat());
		System.out.println(xml.outputString(element));

		return xml.outputString(element);
	}

	public Element createHeader() {
		Document jdomDoc = new Document();
		Element rootElement = new Element("topics");

		jdomDoc.setRootElement(rootElement);
		return rootElement;
	}

	private void exportTopics(Element element, List<Topic> topics) {
		for (Topic topic : topics) {
			exportTopic(element, topic);
		}
	}

	private void exportTopic(Element element, Topic topic) {
		Element topicElement = new Element("topic");
		topicElement.setAttribute("name", topic.getName());

		exportQuestions(topicElement, topic);

		element.addContent(topicElement);
	}

	private void exportQuestions(Element topicElement, Topic topic) {
		Element questionsElement = new Element("questions");

		for (Question question: topic.getQuestions()) {
			exportQuestion(questionsElement, question);
		}

		topicElement.addContent(questionsElement);
	}

	private void exportQuestion(Element questionsElement, Question question) {
		Element questionElement = new Element("question");

		questionElement.setAttribute("number", question.getNumber().toString());

		questionsElement.addContent(questionElement);
	}

}
