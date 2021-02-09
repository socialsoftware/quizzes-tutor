package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthExternalUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;

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

		userElement.setAttribute("name", user.getName());

		if (user.getRole() != null) {
			userElement.setAttribute("role", user.getRole().name());
		}

		userElement.setAttribute("admin", Boolean.toString(user.isAdmin()));

		if (user.getCreationDate() != null) {
			userElement.setAttribute("creationDate", DateHandler.toISOString(user.getCreationDate()));
		}

		if (user.getAuthUser() != null) {
			exportAuthUsers(userElement, user.getAuthUser());
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

	private void exportAuthUsers(Element userElement, AuthUser authUser) {
		Element authUsersElement = new Element("authUsers");
		Element authUserElement = new Element("authUser");

		authUserElement.setAttribute("username", authUser.getUsername() != null ? authUser.getUsername() : "");

		authUserElement.setAttribute("email", authUser.getEmail() != null ? authUser.getEmail() : "");

		authUserElement.setAttribute("type", authUser.getType().toString());

		if (authUser.getPassword() != null) {
			authUserElement.setAttribute("password", authUser.getPassword());
		}

		if (authUser.getLastAccess() != null) {
			authUserElement.setAttribute("lastAccess",
					DateHandler.toISOString(authUser.getLastAccess()));
		}

		if (authUser.getType() == AuthUser.Type.EXTERNAL) {
			if (((AuthExternalUser)authUser).getConfirmationToken() != null) {
				authUserElement.setAttribute("confirmationToken",
						((AuthExternalUser)authUser).getConfirmationToken());
			}

			if (((AuthExternalUser)authUser).getTokenGenerationDate() != null) {
				authUserElement.setAttribute("tokenGenerationDate",
						DateHandler.toISOString(((AuthExternalUser)authUser).getTokenGenerationDate()));
			}
		}

		authUserElement.setAttribute("isActive", Boolean.toString(authUser.isActive()));


		authUsersElement.addContent(authUserElement);
		userElement.addContent(authUsersElement);
	}

}
