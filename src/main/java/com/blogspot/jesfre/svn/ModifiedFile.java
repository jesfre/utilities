package com.blogspot.jesfre.svn;

public class ModifiedFile {
	private OperationType operation;
	private String file;

	public ModifiedFile(OperationType operation, String file) {
		this.operation = operation;
		this.file = file;
	}

	public OperationType getOperation() {
		return operation;
	}
	public void setOperation(OperationType operation) {
		this.operation = operation;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModifiedFile [operation=");
		builder.append(operation);
		builder.append(", file=");
		builder.append(file);
		builder.append("]");
		return builder.toString();
	}
}