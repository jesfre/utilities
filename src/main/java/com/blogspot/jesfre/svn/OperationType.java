package com.blogspot.jesfre.svn;

public enum OperationType {
	ADDED('A'), MODIFIED('M'), BROKEN_LOCK('B'), DELETED('D'),
	UPDATED('U'), CONFLICTED('C'), MERGED('G'), EXISTED('E'),
	NO_OPERATION('*');

	private char op;
	private OperationType(char op) {
		this.op = op;
	}

	public char operation() {
		return op;
	}

	public static OperationType getOperationType(char operation) {
		for(OperationType ot : values()) {
			if(ot.operation() == operation) {
				return ot;
			}
		}
		// No OperationType exist for the given value
		return NO_OPERATION;
	}
}