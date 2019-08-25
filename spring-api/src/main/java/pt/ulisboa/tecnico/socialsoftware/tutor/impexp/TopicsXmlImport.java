package pt.ulisboa.tecnico.socialsoftware.tutor.impexp;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.service.QuestionService;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TopicsXmlImport {
	private QuestionService questionService;

	public void importTopics(InputStream inputStream) {
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);

		Document doc;
		try {
			Reader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
			doc = builder.build(reader);
		} catch (FileNotFoundException e) {
			throw new TutorException(TutorException.ExceptionError.TAGS_IMPORT_ERROR, "File not found");
		} catch (JDOMException e) {
			throw new TutorException(TutorException.ExceptionError.TAGS_IMPORT_ERROR, "Coding problem");
		} catch (IOException e) {
			throw new TutorException(TutorException.ExceptionError.TAGS_IMPORT_ERROR, "File type or format");
		}

		if (doc == null) {
			throw new TutorException(TutorException.ExceptionError.TAGS_IMPORT_ERROR, "File not found ot format error");
		}

		importTopics(doc);
	}

	public void importTopics(String tagsXML, QuestionService questionService) {
		this.questionService = questionService;

		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);

		InputStream stream = new ByteArrayInputStream(tagsXML.getBytes());

		importTopics(stream);
	}

	private void importTopics(Document doc) {
		XPathFactory xpfac = XPathFactory.instance();
		XPathExpression<Element> xp = xpfac.compile("//topics/topic", Filters.element());
		for (Element element : xp.evaluate(doc)) {
			importTopic(element);
		}
	}

	private void importTopic(Element topicElement) {
		String name = topicElement.getAttributeValue("name");

		questionService.createTopic(name);

		for (Element questionElement: topicElement.getChild("questions").getChildren("question")) {
			importQuestion(questionElement, name);
		}
	}

	private void importQuestion(Element questionElement, String name) {
		Integer number = Integer.valueOf(questionElement.getAttributeValue("number"));

		QuestionDto questionDto = questionService.findQuestionByNumber(number);

		questionDto.getTopics().add(name);

		questionService.updateQuestionTopics(questionDto.getId(), questionDto.getTopics().stream().toArray(size -> new String[size]));
	}

}
