package de.frittenburger.movievocabulary.opensubtitle.model;

public class OpenSubtitle {
	
	private String id;
	private String fileName;
	private String language;
	private String format;
	private String encoding;
	private String downloadlink;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getDownloadlink() {
		return downloadlink;
	}
	public void setDownloadlink(String downloadlink) {
		this.downloadlink = downloadlink;
	}

}
