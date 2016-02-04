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
		if (request.getParameterMap().containsKey("username")
				&& request.getParameterMap().containsKey("first_name")
				&& request.getParameterMap().containsKey("last_name")
				&& request.getParameterMap().containsKey("email")
				&& request.getParameterMap().containsKey("password")) {
			String username = request.getParameter("username");
			User user = new User(request.getParameter("first_name"),
					request.getParameter("last_name"), username,
					request.getParameter("email"),
					request.getParameter("password"));
			connection.addSubscriber(user);
			LOGGER.log(Level.INFO, "User {0} has been created.",
					new Object[] { username });
			writer.println("User" + username + " has been created");
		} else {
			LOGGER.log(Level.SEVERE, "User not created.");
			writer.println("Error in creating a new user. ");
		}
	}

	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		final PrintWriter writer = response.getWriter();
		if (request.getParameterMap().containsKey("username")) {
			String username = request.getParameter("username");

			connection.removeSubscriber(username);
			LOGGER.log(Level.INFO, "User {0} has been removed.",
					new Object[] { username });
			writer.println("User" + username + " has been removed");
		} else {
			LOGGER.log(Level.SEVERE, "User not removed.");
			writer.println("Error in removing a new user. ");
		}
	}

}
