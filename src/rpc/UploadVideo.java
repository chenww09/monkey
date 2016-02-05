package rpc;

import static model.ErrorCode.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import db.DBConnection;
import model.Video;
import notification.EmailNotification;

/**
 * Servlet implementation class UploadVideo
 */
@WebServlet({ "/UploadVideo", "/video" })
@MultipartConfig
public class UploadVideo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String VIDEO_PAYLOAD = "video";
	private static final String PARAM_PAYLOAD = "param";
	private static final String PARAM_ANGLE = "angle";
	private static final String VIDEO_TYPE = ".mp4";
	private static final String PARAM_TYPE = ".txt";
	private static final String FILE_PREFIX = "videos";
	private static final String FILE_PERMANENT_STORE = "/tmp/monkey/videos";
	private final static Logger LOGGER = Logger.getLogger(UploadVideo.class
			.getCanonicalName());
	private static final DBConnection connection = new DBConnection();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadVideo() {
		super();
	}

	private boolean validateRequest(String angleStr) {
		try {
			double angle = Double.parseDouble(angleStr);
			if (angle <= 0 || angle >= 90) {
				throw new Exception("Angle is out of scope. ");
			}
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Invalid angle parameter {0}",
					new Object[] { angleStr });
			return false;
		}
	}

	private boolean sendEmailNotification(List<String> fileNames) {
		try {
			List<String> emails = connection.getAllSubscriberEmails();
			boolean status = true;
			for (String address : emails) {
				if (EmailNotification.sendNotification(fileNames, address)) {
					LOGGER.log(Level.INFO, "Email {0} has been sent to {1}",
							new Object[] { fileNames, address });
				} else {
					LOGGER.log(Level.SEVERE,
							"Problems during email notification. Error: {0}",
							new Object[] { address });
					status = false;
				}
			}
			return status;
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
		if (!validateRequest(angleStr)) {
			writer.println(INVALID_PARAMETERS.getValue());
			return;
		}

		List<String> fileList = new ArrayList<>();
		
		String currentTime = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
		String subFolder = getServletContext().getRealPath("/") + FILE_PREFIX +"/" + currentTime;
		new File(subFolder).mkdirs();
		
		String subPermanentFolder = FILE_PERMANENT_STORE + "/" + currentTime;
		new File(subPermanentFolder).mkdirs();
		
		String videoName =  currentTime + "_" + angleStr + VIDEO_TYPE;
		final Part videoPart = request.getPart(VIDEO_PAYLOAD);
		
		if (videoPart == null) {
			writer.println(UPLOAD_FILE_FAILURE.getValue());
			LOGGER.log(Level.SEVERE,
					"Problems during file upload. Error: {0} ",
					new Object[] { videoName });
		}

		boolean status = true;
		String videoPath = subFolder + "/" + videoName;
		status = storeFile(videoPath, videoName, videoPart);
		if (!status) {
			writer.println(STORE_VIDEO_DB_FAILURE.getValue());
		}
		
		String permanentVideoPath = subPermanentFolder + "/" + videoName;
		status = storeFile(permanentVideoPath, videoName, videoPart);
		if (!status) {
			writer.println(STORE_PERMANENET_FAILE_FAILURE.getValue());
		}
		
		final Part paramPart = request.getPart(PARAM_PAYLOAD);
		fileList.add(FILE_PREFIX + "/" + currentTime + "/" + videoName);
		if (paramPart != null) {
			String paramName = currentTime + "_" + angleStr + PARAM_TYPE;
			String paramPath = subFolder + "/" + paramName;
			storeFile(paramPath, paramName, paramPart);
			
			paramPath = subPermanentFolder + "/" + paramName;
			storeFile(paramPath, paramName, paramPart);
			fileList.add(FILE_PREFIX + "/" + currentTime + "/" + paramName);
		}
		
		if (!sendEmailNotification(fileList)) {
			writer.println(EMAIL_NOTIFICATION_FAILURE.getValue());
		}
		
		Video video = new Video(videoName, videoPath, permanentVideoPath, 10,
				Double.parseDouble(angleStr));
		if (!connection.addVideo(video)) {
			writer.println(STORE_VIDEO_DB_FAILURE.getValue());
		}

		if (writer != null) {
			writer.close();
		}
	}
}
