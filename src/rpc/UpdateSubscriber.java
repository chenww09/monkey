package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import model.User;

/**
 * Servlet implementation class UpdateSubscriber
 */
@WebServlet({ "/UpdateSubscriber", "/subscriber" })
public class UpdateSubscriber extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final DBConnection connection = new DBConnection();
	private final static Logger LOGGER = Logger
			.getLogger(UpdateSubscriber.class.getCanonicalName());

	public UpdateSubscriber() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		final PrintWriter writer = response.getWriter();
		JSONObject input = RpcParser.parseInput(request);

		if (input.has("username") && input.has("first_name")
				&& input.has("last_name") && input.has("email")
				&& input.has("password")) {

			try {
				String username = input.getString("username");
				User user;
				user = new User(input.getString("first_name"),
						input.getString("last_name"), username,
						input.getString("email"), input.getString("password"));

				if (connection.addSubscriber(user)) {
					LOGGER.log(Level.INFO, "User {0} has been created.",
							new Object[] { username });

					writer.println("User " + username + " has been created");
				} else {
					LOGGER.log(Level.SEVERE, "User {0} has not been created.",
							new Object[] { username });

					writer.println("User " + username + " has not been created");
				}
			} catch (JSONException e) {
				LOGGER.log(Level.SEVERE, "User not created.");
				writer.println("Error in creating a new user. ");
				e.printStackTrace();
			}
		} else {
			LOGGER.log(Level.SEVERE, "User not created.");
			writer.println("Error in creating a new user. ");
		}
	}

	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		final PrintWriter writer = response.getWriter();
		JSONObject input = RpcParser.parseInput(request);

		if (input.has("username")) {
			try {
				String username = input.getString("username");

				if (connection.removeSubscriber(username)) {
					LOGGER.log(Level.INFO, "User {0} has been removed.",
							new Object[] { username });
					writer.println("User " + username + " has been removed");
				} else {
					LOGGER.log(Level.SEVERE, "User {0} has not been removed",
							new Object[] { username });
					writer.println("User " + username + " has not been removed");
				}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "User not removed.");
				writer.println("Error in removing a new user. ");
			}
		} else {
			LOGGER.log(Level.SEVERE, "User not removed.");
			writer.println("Error in removing a new user. ");
		}
	}

}
