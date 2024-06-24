package com.blogspot.jesfre.svn.utils.diff;

import java.util.ArrayList;
import java.util.List;

import com.blogspot.jesfre.svn.utils.diff.BlockType;

public class DifferenceContent {
	private String fileName;
	private long leftRevision;
	private long rightRevision;
	private List<DifferenceLine> lines = new ArrayList<DifferenceLine>();

	public DifferenceContent() {
	}

	DifferenceContent(String fileName, long leftRevision, long rightRevision) {
		this.fileName = fileName;
		this.leftRevision = leftRevision;
		this.rightRevision = rightRevision;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getLeftRevision() {
		return leftRevision;
	}

	public void setLeftRevision(long leftRevision) {
		this.leftRevision = leftRevision;
	}

	public long getRightRevision() {
		return rightRevision;
	}

	public void setRightRevision(long rightRevision) {
		this.rightRevision = rightRevision;
	}

	public List<DifferenceLine> getLines() {
		return lines;
	}

	public List<DifferenceLine> addLine(BlockType blockType, DiffType diffType, String leftIndentation, String leftText, String rightIndentation, String rightText) {
		lines.add(new DifferenceLine(blockType, diffType, leftIndentation, leftText, rightIndentation, rightText));
		return lines;
	}

	public List<DifferenceLine> addLine(BlockType blockType, DiffType diffType, int leftLinePosition,
			String leftIndentation, String leftText, int rightLinePosition, String rightIndentation, String rightText) {
		DifferenceLine d = new DifferenceLine(blockType, diffType, leftIndentation, leftText, rightIndentation,
				rightText);
		d.setLeftLinePosition(leftLinePosition);
		d.setRightLinePosition(rightLinePosition);
		lines.add(d);
		return lines;
	}

	public List<DifferenceLine> addLine(DiffType diffType, String leftIndentation, String leftText, String rightIndentation, String rightText) {
		addLine(BlockType.NO_BLOCK, diffType, leftIndentation, leftText, rightIndentation, rightText);
		return lines;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DifferenceContent [fileName=");
		builder.append(fileName);
		builder.append(", leftRevision=");
		builder.append(leftRevision);
		builder.append(", rightRevision=");
		builder.append(rightRevision);
		builder.append(", lines=");
		builder.append(lines);
		builder.append("]");
		return builder.toString();
	}

}