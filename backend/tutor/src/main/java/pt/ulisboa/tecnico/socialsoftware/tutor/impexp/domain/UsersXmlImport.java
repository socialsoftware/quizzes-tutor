package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Element;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.common.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import java.time.LocalDateTime;


@Component
public class UsersXmlImport {
	private UserService userService;
	//TODO: Uncomment when export works again
	/*public void importUsers(InputStream inputStream, UserService userService) {
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
	}*/

	/*private void importUsers(Document doc) {
		XPathFactory xpfac = XPathFactory.instance();
		XPathExpression<Element> xp = xpfac.compile("//users/user", Filters.element());
		for (Element element : xp.evaluate(doc)) {
			Integer key = Integer.valueOf(element.getAttributeValue("key"));

			if (!userService.existsUserWithKey(key)) {
				User user;
				if (element.getChild("authUsers") != null) {
					user = importUserWithAuth(element);
				} else {
					user = importUser(element);
				}
				importCourseExecutions(element.getChild("courseExecutions"), user);
			}
		}
	}*/

	//TODO: Uncomment when import is working
	/*private User importUser(Element userElement) {
		Integer key = Integer.valueOf(userElement.getAttributeValue("key"));
		String name = userElement.getAttributeValue("name");
		LocalDateTime creationDate = DateHandler.toLocalDateTime(userElement.getAttributeValue("creationDate"));
		boolean admin =  Boolean.parseBoolean(userElement.getAttributeValue("admin"));
		Role role = getUserRole(userElement);

		UserDto userDto = userService.createUser(name, role, null, false, admin);
		user.setKey(key);
		user.setCreationDate(creationDate);
		return user;
	}*/

	/*private User importUserWithAuth(Element userElement) {
		Element authUserElement = userElement.getChild("authUsers").getChild("authUser");
		Integer key = Integer.valueOf(userElement.getAttributeValue("key"));
		String name = userElement.getAttributeValue("name");
		Role role = getUserRole(userElement);

		String username = authUserElement.getAttributeValue("username");
		if (username.trim().isEmpty()) {
			username = null;
		}
		String email = authUserElement.getAttributeValue("email");
		if (email.trim().isEmpty()) {
			email = null;
		}
		AuthUser.Type type = AuthUser.Type.EXTERNAL;
		String password = null;
		Boolean isActive = null;
		LocalDateTime lastAccess = null;
		String confirmationDate = null;
		LocalDateTime tokenGenerationDate = null;

		if (authUserElement.getAttributeValue("type") != null) {
			type = AuthUser.Type.valueOf(authUserElement.getAttributeValue("type"));
		}
		if (authUserElement.getAttributeValue("password") != null) {
			password = authUserElement.getAttributeValue("password");
		}
		if (authUserElement.getAttributeValue("lastAccess") != null) {
			lastAccess = DateHandler.toLocalDateTime(
					authUserElement.getAttributeValue("lastAccess"));
		}

		if (authUserElement.getAttributeValue("confirmationToken") != null) {
			confirmationDate = authUserElement.getAttributeValue("confirmationToken");
		}

		if (authUserElement.getAttributeValue("tokenGenerationDate") != null) {
			tokenGenerationDate = DateHandler.toLocalDateTime(
					authUserElement.getAttributeValue("tokenGenerationDate"));

		}
		if (authUserElement.getAttributeValue("isActive") != null) {
			isActive = Boolean.parseBoolean(authUserElement.getAttributeValue("isActive"));
		}

		AuthUser authUser = userService.createUserWithAuth(name, username, email, role, type);
		authUser.getUser().setKey(key);
		authUser.setPassword(password);
		if (type == AuthUser.Type.EXTERNAL) {
			((AuthExternalUser)authUser).setActive(isActive);
			((AuthExternalUser)authUser).setConfirmationToken(confirmationDate);
			((AuthExternalUser)authUser).setTokenGenerationDate(tokenGenerationDate);
		}
		authUser.setLastAccess(lastAccess);

		return authUser.getUser();
	}*/
	//TODO: When import is working again course execution should be added to authUser
	private void importCourseExecutions(Element courseExecutions, User user) {
		for (Element courseExecutionElement: courseExecutions.getChildren("courseExecution")) {
			Integer executionId = Integer.valueOf(courseExecutionElement.getAttributeValue("executionId"));

			userService.addCourseExecution(user.getId(), executionId);
		}
	}

	private Role getUserRole(Element userElement) {
		Role role = null;
		if (userElement.getAttributeValue("role") != null) {
			role = Role.valueOf(userElement.getAttributeValue("role"));
		}
		return role;
	}
}