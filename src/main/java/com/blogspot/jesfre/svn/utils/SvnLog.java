package com.blogspot.jesfre.svn.utils;

/**
 * @author <a href="mailto:jorge.ruiz.aquino@gmail.com">Jorge Ruiz Aquino</a>
 *         Feb 10, 2024
 */
public class SvnLog {
	private static final String NOT_FOUND = "NOT-FOUND";
	public static final SvnLog EMPTY = new SvnLog(NOT_FOUND, NOT_FOUND, 0, NOT_FOUND, NOT_FOUND, NOT_FOUND, NOT_FOUND);
	
	private String fileLocation;
	private String fileName;
	private long revision;
	private String committer;
	private String commitTime;
	private String ticket;
	private String comment;

	SvnLog(String fileLocation, String fileName, long revision, String committer, String commitTime, String ticket, String comment) {
		super();
		this.fileLocation = fileLocation;
		this.fileName = fileName;
		this.revision = revision;
		this.committer = committer;
		this.commitTime = commitTime;
		this.ticket = ticket;
		this.comment = comment;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getRevision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String jiraTicket) {
		this.ticket = jiraTicket;
	}

	public String getCommitter() {
		return committer;
	}

	public void setCommitter(String committer) {
		this.committer = committer;
	}

	public String getCommitTime() {
		return commitTime;
	}

	public void setCommitTime(String commitTime) {
		this.commitTime = commitTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SvnLog [fileLocation=");
		builder.append(fileLocation);
		builder.append(", fileName=");
		builder.append(fileName);
		builder.append(", revision=");
		builder.append(revision);
		builder.append(", committer=");
		builder.append(committer);
		builder.append(", commitTime=");
		builder.append(commitTime);
		builder.append(", ticket=");
		builder.append(ticket);
		builder.append(", comment=");
		builder.append(comment);
		builder.append("]");
		return builder.toString();
	}

}
