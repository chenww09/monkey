package rpc;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.DBImport;

/**
 * Servlet implementation class RebuildDB
 */
@WebServlet({ "/RebuildDB", "/db_rebuild" })
public class RebuildDB extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(RebuildDB.class
			.getCanonicalName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RebuildDB() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		DBImport.rebuildDB();
		LOGGER.log(Level.SEVERE, "Rebuild DB");
	}
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		DBImport.rebuildDB();
		LOGGER.log(Level.SEVERE, "Rebuild DB");
	}

}
