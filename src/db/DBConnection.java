package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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

	private void executeUpdateStatement(String query) {
		if (conn == null) {
			return;
		}
		try {
			Statement stmt = conn.createStatement();
			LOGGER.log(Level.INFO, "DB query {0} is done.",
					new Object[] { query });
			stmt.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
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

	public void addSubscriber(User user) {
		String sql = "INSERT INTO users (`first_name`, `last_name`, `password`, `username`, `email`) "
				+ "VALUES (\""
				+ user.getFirstName()
				+ "\", \""
				+ user.getLastName()
				+ "\", \""
				+ user.getPassword()
				+ "\", \""
				+ user.getUsername() + "\", \"" + user.getEmail() + "\")";
		executeUpdateStatement(sql);
	}

	public void removeSubscriber(String username) {
		String sql = "DELETE FROM history WHERE `username`=\"" + username
				+ "\"";
		executeUpdateStatement(sql);
	}

	public boolean addVideo(Video video) {
		String sql = "INSERT INTO users (`video_id`, `file_name`, `file_path`, `file_permanent_path`, `size_kb`, `angle`) "
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
		executeUpdateStatement(sql);
		return true;
	}
}
