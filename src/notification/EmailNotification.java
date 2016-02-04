package notification;

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

	private static final String HOST = "email-smtp.us-west-2.amazonaws.com";
	private static final String PORT = "25";// 25, 465, 587.

	public static boolean sendNotification(String fileName, String toAddress) {

		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.port", PORT);

		// Set properties indicating that we want to use STARTTLS to encrypt the
		// connection.
		// The SMTP session will begin on an unencrypted connection, and then
		// the client
		// will issue a STARTTLS command to upgrade to an encrypted connection.
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");

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
			message.setText("http://" + Settings.HOSTNAME + ":" + Settings.PORT
					+ "/" + Settings.PROJECT_NAME + "/" + fileName);
			message.saveChanges();

			Transport transport = session.getTransport();

			try {
				transport.connect(HOST, USERNAME, PASSWORD);
				transport.sendMessage(message, message.getAllRecipients());
				return true;
			} catch (Exception ex) {
				LOGGER.log(Level.SEVERE, "Failed to send email to server {0}",
						new Object[] { ex.getMessage() });
				return false;
			} finally {
				transport.close();
			}

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}