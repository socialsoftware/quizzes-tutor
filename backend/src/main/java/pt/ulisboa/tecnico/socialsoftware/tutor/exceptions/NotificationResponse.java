package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

import java.io.Serializable;

public class NotificationResponse<T> implements Serializable {
	private Notification notification;
	private T response;

	public NotificationResponse(Notification notification, T response) {
		this.notification = notification;
		this.response = response;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	public T getResponse() {
		return response;
	}

	public void setResponse(T response) {
		this.response = response;
	}

}