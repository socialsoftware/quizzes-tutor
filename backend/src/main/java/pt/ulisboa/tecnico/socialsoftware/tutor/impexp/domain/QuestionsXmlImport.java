package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTIONS_IMPORT_ERROR;

public class QuestionsXmlImport {
	private QuestionService questionService;
	private CourseRepository courseRepository;

	public void importQuestions(InputStream inputStream, QuestionService questionService, CourseRepository courseRepository) {
		this.questionService = questionService;
		this.courseRepository = courseRepository;

		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);

		Document doc;
		try {
			Reader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
			doc = builder.build(reader);
		} catch (FileNotFoundException e) {
			throw new TutorException(QUESTIONS_IMPORT_ERROR, "File not found");
		} catch (JDOMException e) {
			throw new TutorException(QUESTIONS_IMPORT_ERROR, "Coding problem");
		} catch (IOException e) {
			throw new TutorException(QUESTIONS_IMPORT_ERROR, "File type or format");
		}

		if (doc == null) {
			throw new TutorException(QUESTIONS_IMPORT_ERROR, "File not found ot format error");
		}

		importQuestions(doc);
	}

	public void importQuestions(String questionsXml, QuestionService questionService, CourseRepository courseRepository) {
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);

		InputStream stream = new ByteArrayInputStream(questionsXml.getBytes());

		importQuestions(stream, questionService, courseRepository);
	}

	private void importQuestions(Document doc) {
		XPathFactory xpfac = XPathFactory.instance();
		XPathExpression<Element> xp = xpfac.compile("//questions/question", Filters.element());
		for (Element element : xp.evaluate(doc)) {
			importQuestion(element);
		}
	}

	private void importQuestion(Element questionElement) {
		String courseType = questionElement.getAttributeValue("courseType");
		String courseName = questionElement.getAttributeValue("courseName");
		Integer key = Integer.valueOf(questionElement.getAttributeValue("key"));
		String content = questionElement.getAttributeValue("content");
		String title = questionElement.getAttributeValue("title");
		String status = questionElement.getAttributeValue("status");

		QuestionDto questionDto = new QuestionDto();
		questionDto.setKey(key);
		questionDto.setContent(content);
		questionDto.setTitle(title);
		questionDto.setStatus(status);

		Element imageElement = questionElement.getChild("image");
		if (imageElement != null) {
			Integer width = imageElement.getAttributeValue("width") != null ?
					Integer.valueOf(imageElement.getAttributeValue("width")) : null;
			String url = imageElement.getAttributeValue("url");

			ImageDto imageDto = new ImageDto();
			imageDto.setWidth(width);
			imageDto.setUrl(url);

			questionDto.setImage(imageDto);
		}

		List<OptionDto> optionDtos = new ArrayList<>();
		for (Element optionElement : questionElement.getChild("options").getChildren("option")) {
			Integer optionSequence = Integer.valueOf( optionElement.getAttributeValue("sequence"));
			String optionContent = optionElement.getAttributeValue("content");
			boolean correct = Boolean.parseBoolean(optionElement.getAttributeValue("correct"));

			OptionDto optionDto = new OptionDto();
			optionDto.setSequence(optionSequence);
			optionDto.setContent(optionContent);
			optionDto.setCorrect(correct);

			optionDtos.add(optionDto);
		}
		questionDto.setOptions(optionDtos);

		Course course = courseRepository.findByNameType(courseName, courseType).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseName));
		questionService.createQuestion(course.getId(), questionDto);
	}

}
