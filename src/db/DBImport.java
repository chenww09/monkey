package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import util.DBSettings;

public class DBImport {
	
	public static void main(String[] args) {
		DBImport.rebuildDB();
	}

	public static void rebuildDB() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn = null;

			try {
				conn = DriverManager.getConnection(DBSettings.URL);
			} catch (SQLException e) {
				System.out.println("SQLException " + e.getMessage());
				System.out.println("SQLState " + e.getSQLState());
				System.out.println("VendorError " + e.getErrorCode());
			}
			if (conn == null) {
				return;
			}

			// Step 1 Drop tables.
			Statement stmt = conn.createStatement();
			String sql = "DROP TABLE IF EXISTS history";
			stmt.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS videos";
			stmt.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS users";
			stmt.executeUpdate(sql);

			// Step 2: create tables
			sql = "CREATE TABLE videos "
					+ "(video_id VARCHAR(255) NOT NULL, "
					+ "creation_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, "
					+ "size_kb BIGINT(20) UNSIGNED, "
					+ "PRIMARY KEY ( video_id ))";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE users "
					+ "(user_id bigint(20) unsigned NOT NULL AUTO_INCREMENT, "
					+ "first_name VARCHAR(255), " + "last_name VARCHAR(255), "
					+ "password VARCHAR(255)," + "email VARCHAR(255),"
					+ "PRIMARY KEY ( user_id ))";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE history "
					+ "(visit_history_id bigint(20) unsigned NOT NULL AUTO_INCREMENT, "
					+ " user_id BIGINT(20) unsigned NOT NULL , "
					+ " video_id VARCHAR(255) NOT NULL, "
					+ " last_visited_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, "
					+ " PRIMARY KEY (visit_history_id),"
					+ "FOREIGN KEY (video_id) REFERENCES videos(video_id),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id))";
			stmt.executeUpdate(sql);

			sql = "INSERT INTO users (`first_name`, `last_name`, `password`, `email`) "
					+ "VALUES (\"John\", \"Smith\", \"root\", \"wchenpublic@gmail.com\")";
			stmt.executeUpdate(sql);

			System.out.println("Done Importing");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
