package pt.ulisboa.tecnico.socialsoftware.tutor.utils;

public class LinkHandler {

	private static final String BASE = "https://quizzes-tutor.tecnico.ulisboa.pt";

	private LinkHandler() {}

	public static String createConfirmRegistrationLink(String username, String token) {
		String format = "/registration/confirmation?username=%s&token=%s";
		return BASE + String.format(format, username, token);
	}


}
