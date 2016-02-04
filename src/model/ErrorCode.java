package model;

public enum ErrorCode {
	MISSING_PARAMETERS("Missing required parameters"), 
	INVALID_PARAMETERS("Invalid required parameters"),
	UPLOAD_FILE_FAILURE("Upload file failed."),
	STORE_FILE_FAILURE("Store file failed."),
	STORE_PERMANENET_FAILE_FAILURE("Store permanent file failed."),
	STORE_VIDEO_DB_FAILURE("Store video to db failed."),
	REMOVE_USER_FAILURE("Remove user failed"),
	ADD_USER_FAILURE("Add user failed"),
	EMAIL_NOTIFICATION_FAILURE("Email notification failed.");
	
	private final String value;

	private ErrorCode(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
