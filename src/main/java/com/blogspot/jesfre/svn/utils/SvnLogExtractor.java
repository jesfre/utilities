package com.blogspot.jesfre.svn.utils;

import static com.blogspot.jesfre.misc.PathUtils.formatPath;
import static com.blogspot.jesfre.svn.utils.SvnLogExtractor.CommandExecutionMode.COMMAND_FILE;
import static com.blogspot.jesfre.svn.utils.SvnLogExtractor.CommandExecutionMode.DIRECT_COMMAND;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.blogspot.jesfre.commandline.CommandLineRunner;

/**
 * @author <a href="mailto:jorge.ruiz.aquino@gmail.com">Jorge Ruiz Aquino</a>
 *         Feb 10, 2024
 */
public class SvnLogExtractor {
	
	public static void main(String[] args) {
		String svnWorkDir = "path/to/root/project/with/.svn";
		String outFolder = "path/to/output/folder//code-diff-generator";
		String fileToAnalyze = "path/to/root/project/with/.svn/src/com/blogspot/jesfre/svnutils/MyClassToAnalyze.java";

		System.out.println("Running test with limit 5.");
		new SvnLogExtractor(svnWorkDir, outFolder)
				.withLimit(5)
				.analyze(fileToAnalyze)
				.extract();

		System.out.println("Running test with comment.");
		new SvnLogExtractor(svnWorkDir, outFolder)
				.withComment("JIRATICKET123456")
				.analyze(fileToAnalyze)
				.verbose(true)
				.clearTempFiles(false)
				.extract();
	}

	private enum ExtractMode {
		// TODO to make public in future to allow other extraction modes
		BY_DATE_RANGE, BY_LIMIT, BY_COMMENT;
	}

	public enum CommandExecutionMode {
		DIRECT_COMMAND, COMMAND_FILE;
	}

	private String svnWorkDir;
	private String outputFolder;

	private ExtractMode extractMode = ExtractMode.BY_LIMIT;
	private CommandExecutionMode executionMode = DIRECT_COMMAND;
	private String filePathToAnalyze;
	private int limit;
	private String comment;
	private boolean verbose;
	private boolean exportLog;
	private boolean clearTempFiles;

	private void initializeState() {
		executionMode = DIRECT_COMMAND;
		extractMode = ExtractMode.BY_LIMIT;
		filePathToAnalyze = null;
		comment = null;
		limit = 0;
		verbose = false;
		exportLog = false;
		clearTempFiles = true;
	}

	public SvnLogExtractor(String svnWorkingDirectory, String outputFolder) {
		this.svnWorkDir = svnWorkingDirectory;
		this.outputFolder = outputFolder;
		initializeState();
	}

	public SvnLogExtractor analyze(String filePath) {
		filePathToAnalyze = filePath;
		return this;
	}

	/**
	 * Sets the limit and overrides the comment set with {@link #withComment(String)}
	 * 
	 * @param limit
	 * @return
	 */
	public SvnLogExtractor withLimit(int limit) {
		this.limit = limit;
		this.comment = null;
		this.extractMode = ExtractMode.BY_LIMIT;
		return this;
	}

	/**
	 * Sets the commentToSearch and overrides the limit set with {@link #withLimit(int)}
	 * 
	 * @param commentToSearch
	 * @return
	 */
	public SvnLogExtractor withComment(String commentToSearch) {
		this.comment = commentToSearch;
		this.limit = 0;
		this.extractMode = ExtractMode.BY_COMMENT;
		return this;
	}

	/**
	 * {@link CommandExecutionMode#COMMAND_FILE} execution mode runs with {@link #exportLog(boolean)} to true
	 * @param executionMode
	 * @return
	 */
	public SvnLogExtractor withExecutionMode(CommandExecutionMode executionMode) {
		this.executionMode = executionMode;
		return this;
	}

	public SvnLogExtractor verbose(boolean yesNo) {
		this.verbose = yesNo;
		return this;
	}

	public SvnLogExtractor exportLog(boolean yesNo) {
		this.exportLog = yesNo;
		return this;
	}

	/**
	 * Set clearing of files if {@link #exportLog} is set to true
	 * @param yesNo
	 * @return
	 */
	public SvnLogExtractor clearTempFiles(boolean yesNo) {
		this.clearTempFiles = yesNo;
		return this;
	}

	public List<SvnLog> extract() {
		if(this.executionMode == COMMAND_FILE) {
			this.exportLog = true;
		}
		List<SvnLog> logList = new ArrayList<SvnLog>();
		String baseName = FilenameUtils.getName(filePathToAnalyze);
		File logsFolder = new File(outputFolder + SvnConstants.LOGS_FOLDER_PATH);
		File cmdFile = new File(outputFolder + String.format(SvnConstants.CMD_FILE_PATH, baseName));
		String outputFilePath = outputFolder + String.format(SvnConstants.LOG_FILE_PATH, baseName);
		File logOutputFile = new File(outputFilePath);
		String command = buildCommand(outputFilePath);
		String commandResults = null;
		try {
			if (exportLog) {
				logsFolder.mkdirs();
			}
			if (executionMode == COMMAND_FILE) {
				if (verbose)
					System.out.println("Generating command file...");
				FileUtils.writeStringToFile(cmdFile, command);

				if (verbose)
					System.out.println("Executing command file...");
				CommandLineRunner runner = new CommandLineRunner();
				runner.run(cmdFile.getAbsolutePath());

			} else {

				if (verbose)
					System.out.println("Executing SVN log command...");
				CommandLineRunner runner = new CommandLineRunner();
				runner.setVerbose(this.verbose);
				commandResults = runner.executeCommand(command);
			}

			if (verbose)
				System.out.println("Reading the log file...");

			String folderPath = FilenameUtils.getPathNoEndSeparator(filePathToAnalyze);
			String fileName = FilenameUtils.getName(filePathToAnalyze);
			logList.addAll(readLogs(folderPath, fileName, outputFilePath, commandResults));
			if (verbose)
				System.out.println("Done reading logs " + outputFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			initializeState();
			if (clearTempFiles) {
				if (verbose)
					System.out.println("Clearing files.");
				if (executionMode == COMMAND_FILE) {
					cmdFile.delete();
				}
				if (exportLog) {
					logOutputFile.delete();
					logsFolder.delete();
				}
			}
		}

		return logList;
	}

	public SvnLog extractHead() {
		List<SvnLog> log = withLimit(1).extract();
		return log.size() > 0 ? log.get(0) : SvnLog.EMPTY;
	}

	private String buildCommand(String outputFilePath) {
		switch (extractMode) {
		case BY_LIMIT:
			if (exportLog) {
				return String.format(SvnConstants.SVN_LOG_BY_LIMIT_CMD, limit, formatPath(filePathToAnalyze), formatPath(outputFilePath));
			} else {
				return String.format(SvnConstants.SVN_LOG_BY_LIMIT_NO_EXPORT_CMD, limit, formatPath(filePathToAnalyze));
			}
		case BY_COMMENT:
			if (exportLog) {
				return String.format(SvnConstants.SVN_LOG_BY_COMMENT_CMD, comment, formatPath(filePathToAnalyze), formatPath(outputFilePath));
			} else {
				return String.format(SvnConstants.SVN_LOG_BY_COMMENT_NO_EXPORT_CMD, comment, formatPath(filePathToAnalyze));
			}
		default:
			return String.format(SvnConstants.SVN_LOG_BY_LIMIT_CMD, 1, formatPath(filePathToAnalyze), formatPath(outputFilePath));
		}
	}

	/**
	 * Read the logs, either from the file in the given logFile path exportLog=true, or from theEntrie if exportLog=false
	 */
	private List<SvnLog> readLogs(String filePath, String fileName, String logFile, String logEntries) throws IOException {
		List<SvnLog> logs = new ArrayList<SvnLog>();
		// TODO read file using a buffer to avoid out-of-memory errors
		List<String> logLines = null;
		if (exportLog) {
			logLines = FileUtils.readLines(new File(logFile));
		} else {
			logLines = Arrays.asList(StringUtils.split(logEntries, '\n'));
		}

		int linesInLog = 0;
		long revision = 0;
		String ticket = "";
		String committer = "";
		String commitTime = "";
		StringBuilder comments = new StringBuilder();
		for (String line : logLines) {
			if (line.trim().equals(SvnConstants.LOG_SEPARATOR)) {
				if (linesInLog > 1) {
					SvnLog log = new SvnLog(filePath, fileName, revision, ticket, committer, commitTime, comments.toString());
					logs.add(log);
				}

				// New log file init
				linesInLog = 0;
				revision = 0;
				ticket = "";
				committer = "";
				commitTime = "";
				comments.setLength(0);
				continue;
			}
			linesInLog++;

			if (linesInLog == 1) {
				String[] tokens = line.split("\\|");
				String revString = tokens[0].trim().substring(1);
				revision = Long.parseLong(revString);
				committer = tokens[1].trim();
				commitTime = tokens[2].trim();
				
			} else if (linesInLog > 2) {
				// line 2 is an empty line
				if(linesInLog == 3) {
					ticket = line.substring(0, line.indexOf(' '));
				}
				// Comments
				if (comments.length() > 0) {
					comments.append("\n");
				}
				comments.append(line);
			}
		}
		return logs;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SvnLogExtractor [svnWorkDir=");
		builder.append(svnWorkDir);
		builder.append(", outputFolder=");
		builder.append(outputFolder);
		builder.append(", extractMode=");
		builder.append(extractMode);
		builder.append(", executionMode=");
		builder.append(executionMode);
		builder.append(", filePathToAnalyze=");
		builder.append(filePathToAnalyze);
		builder.append(", limit=");
		builder.append(limit);
		builder.append(", comment=");
		builder.append(comment);
		builder.append(", verbose=");
		builder.append(verbose);
		builder.append(", exportLog=");
		builder.append(exportLog);
		builder.append(", clearTempFiles=");
		builder.append(clearTempFiles);
		builder.append("]");
		return builder.toString();
	}

}