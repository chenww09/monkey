package notification;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import util.Settings;

public class EmailNotification {
	private final static Logger LOGGER = Logger
			.getLogger(EmailNotification.class.getCanonicalName());
	private static final String USERNAME = "AKIAJGOE7CEEDWLLX23Q";
	private static final String PASSWORD = "AvfjYInsb+ZfgH5eCN1hiRG7ym0+ppAA8tMQNKOAA43S";
	
	public static void main(String[] args) {
		EmailNotification.sendNotification(Arrays.asList("2015/2015.mp4", "2016/2016.txt"), "wchenpublic@gmail.com");
	}

	public static boolean sendNotification(List<String> fileNames, String toAddress) {

		Properties props = new Properties();

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(USERNAME, PASSWORD);
					}
				});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("wchenpublic@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(toAddress));
			message.setSubject("Monkey API: You have a new video uploaded");
			String body = "";
			for (String fileName : fileNames) {
				body += "http://" + Settings.HOSTNAME + ":" + Settings.PORT
						+ "/" + Settings.PROJECT_NAME + "/" + fileName + "\n";
			}
			message.setText(body);
			message.saveChanges();

			try {
				Transport.send(message);
				return true;
			} catch (Exception ex) {
				LOGGER.log(Level.SEVERE, "Failed to send email to server {0}",
						new Object[] { ex.getMessage() });
				return false;
			}

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}