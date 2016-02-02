package util;

public class DBSettings {
	public static final String HOSTNAME = "localhost";
	public static final String PORT_NUM = "3306";
	public static final String DB_NAME = "monkey";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";
	public static final String URL;
	static {
		URL = "jdbc:mysql://" + HOSTNAME + ":" + PORT_NUM + "/" + DB_NAME
				+ "?user=" + USERNAME + "&password=" + PASSWORD;
	}
}