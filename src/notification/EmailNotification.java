package notification;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import util.Settings;

public class EmailNotification {
	private static final String USERNAME = "chenww05";
	private static final String PASSWORD = "100%packetLoss";

	public static void main(String[] args) {
		sendNotification("xxx", "wchenpublic@gmail.com");
	}

	public static void sendNotification(String fileName, String toAddress) {

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "mail.workflowsim.org");
		props.put("mail.smtp.port", "25");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(USERNAME, PASSWORD);
					}
				});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("no-reply@mtomgroup.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(toAddress));
			message.setSubject("Monkey API: You have a new video uploaded");
			message.setText("http://" + Settings.HOSTNAME + ":" + Settings.PORT
					+ "/" + Settings.PROJECT_NAME + "/" + fileName);

			Transport.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}