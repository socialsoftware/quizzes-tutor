package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.util.List;
import java.util.Set;

public class UsersXmlExport {
	public String export(List<User> users) {
		Element element = createHeader();

		exportUsers(element, users);

		XMLOutputter xml = new XMLOutputter();
		xml.setFormat(Format.getPrettyFormat());

		return xml.outputString(element);
	}

	public Element createHeader() {
		Document jdomDoc = new Document();
		Element rootElement = new Element("users");

		jdomDoc.setRootElement(rootElement);
		return rootElement;
	}

	private void exportUsers(Element element, List<User> users) {
		for (User user : users) {
			exportUser(element, user);
		}
	}

	private void exportUser(Element element, User user) {
		Element userElement = new Element("user");

		userElement.setAttribute("key", String.valueOf(user.getKey()));

		if (user.getUsername() != null) {
			userElement.setAttribute("username", user.getUsername());
		}

		userElement.setAttribute("name", user.getName());

		if (user.getRole() != null) {
			userElement.setAttribute("role", user.getRole().name());
		}

		exportUserCourseExecutions(userElement, user.getCourseExecutions());

		element.addContent(userElement);
	}

	private void exportUserCourseExecutions(Element userElement, Set<CourseExecution> courseExecutions) {
		Element courseExecutionsElement = new Element("courseExecutions");
		for (CourseExecution courseExecution : courseExecutions) {
			Element courseExecutionElement = new Element("courseExecution");

			courseExecutionElement.setAttribute("executionId", courseExecution.getId().toString());

			courseExecutionsElement.addContent(courseExecutionElement);
		}
		userElement.addContent(courseExecutionsElement);
	}
}
