package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;

import java.io.*;
import java.nio.charset.Charset;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.TOPICS_IMPORT_ERROR;

public class TopicsXmlImport {
	private QuestionService questionService;
    private TopicService topicService;

	public void importTopics(InputStream inputStream, TopicService topicService, QuestionService questionService) {
		this.topicService = topicService;
        this.questionService = questionService;

		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);

		Document doc;
		try {
			Reader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
			doc = builder.build(reader);
		} catch (FileNotFoundException e) {
			throw new TutorException(TOPICS_IMPORT_ERROR, "File not found");
		} catch (JDOMException e) {
			throw new TutorException(TOPICS_IMPORT_ERROR, "Coding problem");
		} catch (IOException e) {
			throw new TutorException(TOPICS_IMPORT_ERROR, "File type or format");
		}

		if (doc == null) {
			throw new TutorException(TOPICS_IMPORT_ERROR, "File not found ot format error");
		}

		importTopics(doc);
	}

	public void importTopics(String topicsXml, TopicService topicService, QuestionService questionService) {
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);

		InputStream stream = new ByteArrayInputStream(topicsXml.getBytes());

		importTopics(stream, topicService, questionService);
	}

	private void importTopics(Document doc) {
		XPathFactory xpfac = XPathFactory.instance();
		XPathExpression<Element> xp = xpfac.compile("//topics/topic", Filters.element());
		for (Element element : xp.evaluate(doc)) {
			importTopic(element);
		}
	}

	private void importTopic(Element topicElement) {
		String course = topicElement.getAttributeValue("course");
		String name = topicElement.getAttributeValue("name");

        TopicDto topicDto = new TopicDto();
        topicDto.setName(name);

		topicService.createTopic(course, topicDto);

		for (Element questionElement: topicElement.getChild("questions").getChildren("question")) {
			importQuestion(questionElement, name);
		}
	}

	private void importQuestion(Element questionElement, String name) {
		Integer number = Integer.valueOf(questionElement.getAttributeValue("number"));

		QuestionDto questionDto = questionService.findQuestionByNumber(number);
        TopicDto topicDto = new TopicDto();
        topicDto.setName(name);

		questionDto.getTopics().add(topicDto);

		questionService.updateQuestionTopics(questionDto.getId(), questionDto.getTopics().toArray(new TopicDto[0]));
	}

}
