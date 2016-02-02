package rpc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import notification.EmailNotification;

/**
 * Servlet implementation class UploadVideo
 */
@WebServlet({ "/UploadVideo", "/video" })
@MultipartConfig
public class UploadVideo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String FILE_PAYLOAD = "file";
	private static final String PARAM_ANGLE = "angle";
	private static final String VIDEO_TYPE = ".mp4";
	private static final String FILE_PREFIX = "videos";
	private static final String FILE_PERMANENT_STORE = "/tmp/monkey/videos";
	private final static Logger LOGGER = Logger.getLogger(UploadVideo.class
			.getCanonicalName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadVideo() {
		super();
	}

	private boolean validateRequest(PrintWriter writer, String angleStr) {
		try {
			double angle = Double.parseDouble(angleStr);
			if (angle <= 0 || angle >= 90) {
				throw new Exception("Angle is out of scope. ");
			}
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Invalid angle parameter {0}",
					new Object[] { angleStr });
			writer.println("Invalid or non-existing angle parameter");
			return false;
		}
	}

	private String generateFileName(String angleStr) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date date = new Date();
		return dateFormat.format(date) + "_" + angleStr + VIDEO_TYPE;
	}

	private boolean sendEmailNotification(String fileName) {
		try {
			for (String address : EmailNotification.NOTIFICATION_LIST) {
				EmailNotification.sendNotification(
						FILE_PREFIX + "/" + fileName, address);
				LOGGER.log(Level.INFO, "Email {0} has been sent to {1}",
						new Object[] { fileName, address });
			}
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,
					"Problems during email notification. Error: {0}",
					new Object[] { e.getMessage() });
			return false;
		}
	}

	private boolean storeFile(String filePath, String fileName, Part filePart)
			throws IOException {
		File outputFile = new File(filePath);
		OutputStream out = null;
		InputStream filecontent = null;
		try {
			out = new FileOutputStream(outputFile);
			filecontent = filePart.getInputStream();

			int read = 0;
			final byte[] bytes = new byte[1024];

			while ((read = filecontent.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			LOGGER.log(Level.INFO, "File {0} has been uploaded to {1}",
					new Object[] { fileName, filePath });

			return true;
		} catch (IOException fne) {

			LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}",
					new Object[] { fne.getMessage() });
			return false;
		} finally {
			if (out != null) {
				out.close();
			}
			if (filecontent != null) {
				filecontent.close();
			}
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		final PrintWriter writer = response.getWriter();
		final String angleStr = request.getParameter(PARAM_ANGLE);
		if (!validateRequest(writer, angleStr)) {
			return;
		}

		final String fileName = generateFileName(angleStr);
		String fileFolder = getServletContext().getRealPath("/") + FILE_PREFIX;

		new File(fileFolder).mkdirs();
		String filePath =  fileFolder + "/" + fileName;

		final Part filePart = request.getPart(FILE_PAYLOAD);
		if (storeFile(filePath, fileName, filePart)) {
			writer.println("File has been uploaded");
			LOGGER.log(Level.INFO, "File {0} has been uploaded to {1}",
					new Object[] { fileName, filePath });

			if (!sendEmailNotification(fileName)) {
				writer.println("Email notification failed.");
			}
			
			new File(FILE_PERMANENT_STORE).mkdirs();
			String permanentFilePath = FILE_PERMANENT_STORE + "/" + fileName;
			if (!storeFile(permanentFilePath, fileName, filePart)) {
				writer.println("Failed to copy it to permanent store");
			}
		} else {
			writer.println("Upload failed");
			LOGGER.log(Level.SEVERE,
					"Problems during file upload. Error: {0} to {1}",
					new Object[] { fileName, filePath });
		}

		if (writer != null) {
			writer.close();
		}
	}
}
