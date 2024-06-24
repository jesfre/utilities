package com.blogspot.jesfre.svn.utils.diff;

import static com.blogspot.jesfre.svn.utils.diff.BlockType.BLOCK_END;
import static com.blogspot.jesfre.svn.utils.diff.BlockType.BLOCK_INIT;
import static com.blogspot.jesfre.svn.utils.diff.BlockType.NO_BLOCK;
import static com.blogspot.jesfre.svn.utils.diff.BlockType.SINGLE_LINE;
import static com.blogspot.jesfre.svn.SvnConstants.BLANK_SP;
import static com.blogspot.jesfre.svn.SvnConstants.TAB_SPS;
import static com.blogspot.jesfre.svn.utils.diff.DiffType.BOTH;
import static com.blogspot.jesfre.svn.utils.diff.DiffType.LEFT;
import static com.blogspot.jesfre.svn.utils.diff.DiffType.RIGHT;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.blogspot.jesfre.svn.SvnConstants;

/**
 * @author jruizaquino
 *
 */
public class DifferenceAnalyzer {

	public static DifferenceContent getDifferenceContent(File javaFile, File diffFile) {
		try {
			return new DifferenceAnalyzer().populateDifferences(javaFile, diffFile);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private DifferenceContent populateDifferences(File javaFile, File diffFile) throws IOException {
		DifferenceContent difference = new DifferenceContent();
		List<String> originalCodeLines = Collections.emptyList();
		if(javaFile != null) {
			originalCodeLines = (List<String>) FileUtils.readLines(javaFile);
		}

		LineIterator iterator = IOUtils.lineIterator(new FileReader(diffFile));
		if (!iterator.hasNext()) {
			System.out.println("No differences found in file " + diffFile.getName());
			return difference;
		}
		String fname = iterator.nextLine();
		fname = fname.replace("Index: ", "");
		difference.setFileName(fname);

		iterator.nextLine(); // Separator =====
		String leftRev = iterator.nextLine();
		leftRev = leftRev.substring(leftRev.indexOf("(revision ") + 10, leftRev.indexOf(")"));
		difference.setLeftRevision(Long.valueOf(leftRev));

		String rightRev = iterator.nextLine();
		if (rightRev.trim().endsWith("(nonexistent)")) {
			// Is first revision of the file
			difference.setRightRevision(Long.valueOf(leftRev));
		} else {
			rightRev = rightRev.substring(rightRev.indexOf("(revision ") + 10, rightRev.indexOf(")"));
			difference.setRightRevision(Long.valueOf(rightRev));
		}

		int blockNum = 0;
		int leftLinePosition = 0;
		int leftNumberOfLines = 0;
		int rightLinePosition = 0;
		int rightNumberOfLines = 0;
		int maxLeftIndexProcessedInPrevBlock = 0;
		int previousLeftStartPosition = 0;
		int previousRightStartPosition = 0;

		List<String> leftStringList = new ArrayList<String>();
		List<String> rightStringList = new ArrayList<String>();
		while (iterator.hasNext()) {
			String line = iterator.nextLine();

			if (line.startsWith(SvnConstants.BLOCK)) {
				// Example where line = "@@ -1923,32 +1919,38 @@"
				String startLineLeft = line.substring(line.indexOf('-') + 1, line.indexOf(','));
				leftLinePosition = Integer.valueOf(startLineLeft.trim());

				String leftNumberOfLinesStr = line.substring(line.indexOf(',') + 1, line.indexOf('+'));
				leftNumberOfLines = Integer.valueOf(leftNumberOfLinesStr.trim());

				String startLineRight = line.substring(line.indexOf('+') + 1, line.lastIndexOf(','));
				rightLinePosition = Integer.valueOf(startLineRight.trim());

				String rightNumberOfLinesStr = line.substring(line.lastIndexOf(',') + 1, line.lastIndexOf("@@"));
				rightNumberOfLines = Integer.valueOf(rightNumberOfLinesStr.trim());

				populateDifferenceContentBlock(difference, leftStringList, rightStringList, previousLeftStartPosition,
						previousRightStartPosition);
				leftStringList.clear();
				rightStringList.clear();
				previousLeftStartPosition = leftLinePosition;
				previousRightStartPosition = rightLinePosition;

				// Add unchanged block from original code
				if(!originalCodeLines.isEmpty()) {
					for (int i = maxLeftIndexProcessedInPrevBlock; i < leftLinePosition - 1; i++) {
						String codeLine = originalCodeLines.get(i);
						String spaces = formatIndentation(codeLine);
						String escapedLine = escapeText(codeLine);
						difference.addLine(DiffType.SAME, spaces, escapedLine, spaces, escapedLine);
					}
				}

				maxLeftIndexProcessedInPrevBlock = leftLinePosition - 1 + leftNumberOfLines;
				blockNum++;
				continue;
			}

			if (line.startsWith(SvnConstants.LEFT)) {
				leftStringList.add(line);
				rightStringList.add("");
			} else if (line.startsWith(SvnConstants.RIGHT)) {
				leftStringList.add("");
				rightStringList.add(line);
			} else {
				// No difference
				leftStringList.add(line);
				rightStringList.add(line);
			}
		}

		// Remaining strings
		populateDifferenceContentBlock(difference, leftStringList, rightStringList, previousLeftStartPosition,
				previousRightStartPosition);
		leftStringList.clear();
		rightStringList.clear();

		// Add unchanged last block from original code
		for (int j = maxLeftIndexProcessedInPrevBlock; j < originalCodeLines.size(); j++) {
			String codeLine = originalCodeLines.get(j);
			String spaces = formatIndentation(codeLine);
			String escapedLine = escapeText(codeLine);
			difference.addLine(DiffType.SAME, spaces, escapedLine, spaces, escapedLine);
		}

		updateBlockTypes(difference);
		return difference;
	}

	private void populateDifferenceContentBlock(DifferenceContent difference, List<String> leftStringList,
			List<String> rightStringList, int leftStartPosition, int rightStartPosition) {
		int lastDiffIndex = -1;
		int maxBlockSize = leftStringList.size() > rightStringList.size() ? leftStringList.size()
				: rightStringList.size();
		for (int i = 0; i < maxBlockSize; i++) {
			String leftString = BLANK_SP;
			String rightString = BLANK_SP;
			String leftIndent = BLANK_SP;
			String rightIndent = BLANK_SP;
			String originalLeftText = "";
			String originalRightText = "";

			if (i < leftStringList.size()) {
				originalLeftText = leftStringList.get(i);
				String noDiffSymbolLine = originalLeftText;
				if (noDiffSymbolLine.length() > 0) {
					noDiffSymbolLine = noDiffSymbolLine.substring(1);
				}
				leftString = escapeText(noDiffSymbolLine);
				leftIndent = formatIndentation(noDiffSymbolLine);
			}

			if (i < rightStringList.size()) {
				originalRightText = rightStringList.get(i);
				String noDiffSymbolLine = originalRightText;
				if (noDiffSymbolLine.length() > 0) {
					noDiffSymbolLine = noDiffSymbolLine.substring(1);
				}
				rightString = escapeText(noDiffSymbolLine);
				rightIndent = formatIndentation(noDiffSymbolLine);
			}

			DiffType diffType = DiffType.SAME;
			if (StringUtils.isNotBlank(originalLeftText) && StringUtils.isNotBlank(originalRightText)) {
				if (!originalLeftText.equals(originalRightText)) {
					diffType = BOTH;
				}
			} else if (StringUtils.isNotBlank(originalLeftText)) {
				diffType = LEFT;
				lastDiffIndex = i;
			} else if (StringUtils.isNotBlank(originalRightText)) {
				diffType = RIGHT;
			} else {
				lastDiffIndex = -1;
			}

			if (diffType == RIGHT && lastDiffIndex >= 0) {
				// This should be BOTH. Move this RIGHT lines above.
				DifferenceLine prevLine = null;
				for (int j = difference.getLines().size() - 1; j >= lastDiffIndex; j--) {
					DifferenceLine tempPrevious = difference.getLines().get(j);
					if (tempPrevious.getDiffType() == BOTH) {
						diffType = BOTH;
						break;
					} else if (tempPrevious.getDiffType() != LEFT) {
						break;
					}
					prevLine = tempPrevious;
					// prevLine.setDiffType(BOTH);
				}
				if (prevLine != null) {
					prevLine.setDiffType(BOTH);
					prevLine.setRightIndentation(rightIndent);
					prevLine.setRightText(rightString);
				} else {
					difference.addLine(NO_BLOCK, diffType, leftStartPosition++, leftIndent, leftString,
							rightStartPosition++, rightIndent, rightString);
				}
			} else {
				difference.addLine(NO_BLOCK, diffType, leftStartPosition++, leftIndent, leftString, rightStartPosition++,
						rightIndent, rightString);
			}
		}
	}

	private void updateBlockTypes(DifferenceContent difference) {
		// Set first line of the file as BLOCK_INIT
		DifferenceLine firstLine = difference.getLines().get(0);
		if (firstLine.getBlockType() == NO_BLOCK) {
			firstLine.setBlockType(BLOCK_INIT);
		}

		DifferenceLine lastLine = difference.getLines().get(difference.getLines().size() - 1);
		if (lastLine.getBlockType() == NO_BLOCK) {
			lastLine.setBlockType(BLOCK_END);
		}

		// Update the type of block for all blocks of differences
		DiffType tempCurrentType = DiffType.SAME;
		DiffType tempPrevType = DiffType.SAME;
		for (int i = 0; i < difference.getLines().size(); i++) {
			DifferenceLine prevDiff = null;
			if (i > 0) {
				prevDiff = difference.getLines().get(i - 1);
				tempPrevType = prevDiff.getDiffType();
				if (tempPrevType == LEFT || tempPrevType == RIGHT || tempPrevType == BOTH) {
					tempPrevType = DiffType.BOTH;
				}
			}

			DifferenceLine diff = difference.getLines().get(i);
			tempCurrentType = diff.getDiffType();
			if (tempCurrentType == LEFT || tempCurrentType == RIGHT || tempCurrentType == BOTH) {
				tempCurrentType = DiffType.BOTH;
			}

			if (tempCurrentType != tempPrevType) {
				diff.setBlockType(BLOCK_INIT);

				if (prevDiff != null) {
					if (prevDiff.getBlockType() == BLOCK_INIT) {
						prevDiff.setBlockType(SINGLE_LINE);
					} else {
						prevDiff.setBlockType(BLOCK_END);
					}
				}
			}
		}
	}

	private String formatIndentation(String text) {
		if (StringUtils.isBlank(text)) {
			return BLANK_SP;
		}
		// Replace spaces
		StringBuilder spaces = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == ' ') {
				spaces.append(BLANK_SP);
			} else if (c == '\t') {
				spaces.append(TAB_SPS);
			} else {
				break;
			}
		}
		return spaces.toString();
	}

	private String escapeText(String text) {
		if (StringUtils.isEmpty(text)) {
			return BLANK_SP;
		}
		return StringEscapeUtils.escapeHtml(text).trim();
	}
}