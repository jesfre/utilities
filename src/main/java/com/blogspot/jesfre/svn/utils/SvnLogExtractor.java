package com.blogspot.jesfre.svn.utils;

import static com.blogspot.jesfre.misc.PathUtils.formatPath;
import static com.blogspot.jesfre.svn.utils.SvnLogExtractor.CommandExecutionMode.COMMAND_FILE;
import static com.blogspot.jesfre.svn.utils.SvnLogExtractor.CommandExecutionMode.DIRECT_COMMAND;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

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

		new SvnLogExtractor(svnWorkDir, outFolder)
				.withLimit(5)
				.analyze(fileToAnalyze)
				.extract();

		new SvnLogExtractor(svnWorkDir, outFolder)
				.withLimit(5)
				.analyze(fileToAnalyze)
				.verbosity(true)
				.extractHead();
	}

	private enum ExtractMode {
		// TODO to make public in future to allow other extraction modes
		BY_DATE_RANGE, BY_LIMIT;
	}

	public enum CommandExecutionMode {
		DIRECT_COMMAND, COMMAND_FILE;
	}
	private static final int MAX_LIMIT = 100;
	
	private ExtractMode mode = ExtractMode.BY_LIMIT;
	private CommandExecutionMode executionMode = DIRECT_COMMAND;
	private String svnWorkDir;
	private String outputFolder;

	private String filePathToAnalyze;
	private int limit;
	private boolean verbose;

	public SvnLogExtractor(String svnWorkingDirectory, String outputFolder) {
		this.svnWorkDir = svnWorkingDirectory;
		this.outputFolder = outputFolder;
	}

	public SvnLogExtractor analyze(String filePath) {
		filePathToAnalyze = filePath;
		return this;
	}

	public SvnLogExtractor withLimit(int limit) {
		if(limit > MAX_LIMIT) {
			this.limit = MAX_LIMIT;
		}
		this.limit = limit;
		return this;
	}

	public SvnLogExtractor withExecutionMode(CommandExecutionMode executionMode) {
		this.executionMode = executionMode;
		return this;
	}

	public SvnLogExtractor verbosity(boolean yesNo) {
		this.verbose = yesNo;
		return this;
	}

	public List<SvnLog> extract() {
		List<SvnLog> logList = new ArrayList<SvnLog>();
		String baseName = FilenameUtils.getName(filePathToAnalyze);
		File logsFolder = new File(outputFolder + SvnConstants.LOGS_FOLDER_PATH);
		File cmdFile = new File(outputFolder + String.format(SvnConstants.CMD_FILE_PATH, limit, baseName));
		String outputFilePath = outputFolder + String.format(SvnConstants.LOG_FILE_PATH, limit, baseName);
		File logOutputFile = new File(outputFilePath);
		String command = String.format(SvnConstants.SVN_LOG_BY_LIMIT_CMD, limit, formatPath(filePathToAnalyze), formatPath(outputFilePath));
		
		try {
			logsFolder.mkdirs();
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
				runner.executeCommand(command);
			}

			if (verbose)
				System.out.println("Reading the log file...");

			String folderPath = FilenameUtils.getPathNoEndSeparator(filePathToAnalyze);
			String fileName = FilenameUtils.getName(filePathToAnalyze);
			logList.addAll(readLogs(folderPath, fileName, outputFilePath));
			if (verbose)
				System.out.println("Done reading logs " + outputFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			clear();
			if (executionMode == COMMAND_FILE) {
				cmdFile.delete();
			}
			logOutputFile.delete();
		}

		return logList;
	}

	public SvnLog extractHead() {
		List<SvnLog> log = withLimit(1).extract();
		return log.size() > 0 ? log.get(0) : SvnLog.EMPTY;
	}

	private List<SvnLog> readLogs(String filePath, String fileName, String logFile) throws IOException {
		List<SvnLog> logs = new ArrayList<SvnLog>();
		// TODO read file using a buffer to avoid out-of-memory errors
		List<String> logLines = FileUtils.readLines(new File(logFile));
		int linesInLog = 0;
		long revision = 0;
		String ticket = "";
		String committer = "";
		String commitTime = "";
		StringBuilder comments = new StringBuilder();
		for (String line : logLines) {
			if (line.equals(SvnConstants.LOG_SEPARATOR)) {
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

	private void clear() {
		filePathToAnalyze = null;
		limit = 0;
		verbose = false;
		executionMode = DIRECT_COMMAND;
	}
}
