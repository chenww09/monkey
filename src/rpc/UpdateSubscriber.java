package rpc;

import static model.ErrorCode.*;

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
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		LOGGER.log(Level.SEVERE, "User not created.");
		writer.println(ADD_USER_FAILURE.getValue());
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
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LOGGER.log(Level.SEVERE, "User not removed.");
		writer.println(REMOVE_USER_FAILURE.getValue());

	}

}
