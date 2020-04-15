package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import java.io.*;
import java.nio.charset.Charset;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USERS_IMPORT_ERROR;

@Component
public class UsersXmlImport {
	private UserService userService;

	public void importUsers(InputStream inputStream, UserService userService) {
		this.userService = userService;

		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);

		Document doc;
		try {
			Reader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
			doc = builder.build(reader);
		} catch (FileNotFoundException e) {
			throw new TutorException(USERS_IMPORT_ERROR, "File not found");
		} catch (JDOMException e) {
			throw new TutorException(USERS_IMPORT_ERROR, "Coding problem");
		} catch (IOException e) {
			throw new TutorException(USERS_IMPORT_ERROR, "File type or format");
		}

		if (doc == null) {
			throw new TutorException(USERS_IMPORT_ERROR, "File not found ot format error");
		}

		importUsers(doc);
	}

	public void importUsers(String usersXml, UserService userService) {
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);

		InputStream stream = new ByteArrayInputStream(usersXml.getBytes());

		importUsers(stream, userService);
	}

	private void importUsers(Document doc) {
		XPathFactory xpfac = XPathFactory.instance();
		XPathExpression<Element> xp = xpfac.compile("//users/user", Filters.element());
		for (Element element : xp.evaluate(doc)) {
			Integer key = Integer.valueOf(element.getAttributeValue("key"));

			if (userService.findByKey(key) == null) {
				String name = element.getAttributeValue("name");
				String username = element.getAttributeValue("username");

				User.Role role = null;
				if (element.getAttributeValue("role") != null) {
					role = User.Role.valueOf(element.getAttributeValue("role"));
				}

				User user = userService.createUser(name, username, role);
				user.setKey(key);

				importCourseExecutions(element.getChild("courseExecutions"), user);
			}
		}
	}

	private void importCourseExecutions(Element courseExecutions, User user) {
		for (Element courseExecutionElement: courseExecutions.getChildren("courseExecution")) {
			Integer executionId = Integer.valueOf(courseExecutionElement.getAttributeValue("executionId"));

			userService.addCourseExecution(user.getId(), executionId);
		}
	}
}
