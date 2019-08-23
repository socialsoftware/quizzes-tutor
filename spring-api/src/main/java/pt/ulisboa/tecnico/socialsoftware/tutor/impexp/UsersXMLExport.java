package pt.ulisboa.tecnico.socialsoftware.tutor.impexp;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import java.util.List;

public class UsersXMLExport {
	public String export(List<User> users) {
		Element element = createHeader();

		exportUsers(element, users);

		XMLOutputter xml = new XMLOutputter();
		xml.setFormat(Format.getPrettyFormat());
		System.out.println(xml.outputString(element));

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
		userElement.setAttribute("username", user.getUsername());
		userElement.setAttribute("name", user.getName());
		userElement.setAttribute("role", user.getRole());

		if (user.getYear() != null) {
			userElement.setAttribute("year", user.getYear().toString());
		}

		element.addContent(userElement);
	}

}
