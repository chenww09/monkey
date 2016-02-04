package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.User;
import model.Video;
import util.DBSettings;

public class DBConnection {
	private Connection conn = null;
	private final static Logger LOGGER = Logger.getLogger(DBConnection.class
			.getCanonicalName());

	public DBConnection() {
		this(DBSettings.URL);
	}

	public DBConnection(String url) {
		try {
			// Forcing the class representing the MySQL driver to load and
			// initialize.
			// The newInstance() call is a work around for some broken Java
			// implementations
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url);
			LOGGER.log(Level.INFO, "DB inited succeeded");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.log(Level.INFO, "DB inited failed");
		}
	}

	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) { /* ignored */
			}
		}
	}

	/**
	 * @return true if succeeds
	 */
	private boolean executeUpdateStatement(String query) {
		if (conn == null) {
			return false;
		}
		try {
			Statement stmt = conn.createStatement();
			LOGGER.log(Level.INFO, "DB query {0} is done.",
					new Object[] { query });
			stmt.executeUpdate(query);
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "DB query {0} is not done.",
					new Object[] { query });
			e.printStackTrace();
			return false;
		}
	}

	/** 
	 * @return null if fails
	 */
	private ResultSet executeFetchStatement(String query) {
		if (conn == null) {
			return null;
		}
		try {
			Statement stmt = conn.createStatement();
			LOGGER.log(Level.INFO, "DB query {0} is done.",
					new Object[] { query });
			return stmt.executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @return null if fails
	 */
	public List<String> getAllSubscriberEmails() {
		List<String> emails = new ArrayList<> ();
		String sql = "SELECT email FROM users ";
		ResultSet result = executeFetchStatement(sql);
		if (result == null) {
			return null;
		}
		try {
			while (result.next()) {
				emails.add(result.getString("email"));
			}
			return emails;
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "Cannot fetch subscribers.");
			return null;
		}
	}

	/**
	 * @return true if succeeds
	 */
	public boolean addSubscriber(User user) {
		String sql = "INSERT IGNORE INTO users (`first_name`, `last_name`, `password`, `username`, `email`) "
				+ "VALUES (\""
				+ user.getFirstName()
				+ "\", \""
				+ user.getLastName()
				+ "\", \""
				+ user.getPassword()
				+ "\", \""
				+ user.getUsername() + "\", \"" + user.getEmail() + "\")";
		return executeUpdateStatement(sql);
	}

	/**
	 * @return true if succeeds
	 */
	public boolean removeSubscriber(String username) {
		String sql = "DELETE FROM users WHERE `username`=\"" + username
				+ "\"";
		return executeUpdateStatement(sql);
	}

	/** 
	 * @return true if succeed.
	 */
	public boolean addVideo(Video video) {
		String sql = "INSERT INTO videos (`video_id`, `file_name`, `file_path`, `file_permanent_path`, `size_kb`, `angle`) "
				+ "VALUES (\""
				+ video.getFileName()
				+ "\", \""
				+ video.getFileName()
				+ "\", \""
				+ video.getFilePath()
				+ "\", \""
				+ video.getPermanentFilePath()
				+ "\", \""
				+ video.getSizeKB() 
				+ "\", \"" 
				+ video.getAngle() + "\")";
		return executeUpdateStatement(sql);
	}
}
