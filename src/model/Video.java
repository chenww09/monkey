package model;

public class Video {
	private String fileName;
	private String filePath;
	private String permanentFilePath;
	private double angle;
	private long sizeKB;

	public Video(String fileName, String filePath, String permanentFilePath,
			long sizeKB, double angle) {
		super();
		this.fileName = fileName;
		this.filePath = filePath;
		this.permanentFilePath = permanentFilePath;
		this.angle = angle;
		this.sizeKB = sizeKB;
	}

	public long getSizeKB() {
		return sizeKB;
	}

	public void setSizeKB(long sizeKB) {
		this.sizeKB = sizeKB;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getPermanentFilePath() {
		return permanentFilePath;
	}

	public void setPermanentFilePath(String permanentFilePath) {
		this.permanentFilePath = permanentFilePath;
	}
}
