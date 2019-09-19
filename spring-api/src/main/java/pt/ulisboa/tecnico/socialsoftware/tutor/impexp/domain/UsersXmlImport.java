package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import java.io.*;
import java.nio.charset.Charset;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.USERS_IMPORT_ERROR;

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
			Integer number = Integer.valueOf(element.getAttributeValue("number"));

			if (userService.findByNumber(number) == null) {
				String name = element.getAttributeValue("name");
				String username = element.getAttributeValue("username");

				User.Role role = null;
				if (element.getAttributeValue("role") != null) {
					role = User.Role.valueOf(element.getAttributeValue("role"));
				}

				User user = userService.create(name, username, role);
				user.setNumber(number);

				if (element.getAttributeValue("year") != null) {
					user.setYear(Integer.valueOf(element.getAttributeValue("year")));
				}
			}
		}
	}

}
