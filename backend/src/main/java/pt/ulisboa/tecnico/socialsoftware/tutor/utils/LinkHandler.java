package pt.ulisboa.tecnico.socialsoftware.tutor.utils;

public class LinkHandler {

	private final static String BASE = "https://quizzes-tutor.tecnico.ulisboa.pt";

	private LinkHandler() {}

	public static String createConfirmRegistrationLink(String email, String token) {
		String format = "/registration/confirmation?email=%s&token=%s";
		return BASE + String.format(format, email, token);
	}


}
