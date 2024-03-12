package com.blogspot.jesfre.svn.utils;

import org.apache.commons.lang.StringUtils;

import com.blogspot.jesfre.commandline.CommandLineRunner;
import com.blogspot.jesfre.svn.SvnConstants;

public class SvnExport {
	private boolean verbose;

	public SvnExport verbose(boolean yesNo) {
		this.verbose = yesNo;
		return this;
	}

	/**
	 * Exports the given revision of the given file
	 * @param revision
	 * @param svnManagedFile
	 * @param exportOutputFile
	 * @return the path to the exported file
	 */
	public String export(long revision, String svnManagedFile, String exportOutputFile) {
		String cmdExportCommand = getCommand(revision, svnManagedFile, exportOutputFile);

		CommandLineRunner runner = new CommandLineRunner();
		runner.setVerbose(verbose);
		runner.executeCommand(cmdExportCommand);
		return exportOutputFile;
	}

	/**
	 * Exports the HEAD revision of the given file
	 * @param svnManagedFile
	 * @param exportOutputFile
	 * @return the path to the exported file
	 */
	public String exportHead(String svnManagedFile, String exportOutputFile) {
		String cmdExportCommand = getCommand(svnManagedFile, exportOutputFile);

		CommandLineRunner runner = new CommandLineRunner();
		runner.setVerbose(verbose);
		runner.executeCommand(cmdExportCommand);
		return exportOutputFile;
	}

	/**
	 * Gets the command to export the HEAD revision
	 * @param svnManagedFile
	 * @param exportOutputFile
	 * @return
	 */
	public String getCommand(String svnManagedFile, String exportOutputFile) {
		String cmdExportCommand = StringUtils.replace(SvnConstants.SVN_EXPORT_CMD_TEMPLATE, "URL_FILE", svnManagedFile);
		cmdExportCommand = StringUtils.replace(cmdExportCommand, "EXPORTED_JAVA_FILEPATH", exportOutputFile);
		return cmdExportCommand;
	}

	/**
	 * Gets the command to export the given revision
	 * @param revision
	 * @param svnManagedFile
	 * @param exportOutputFile
	 * @return
	 */
	public String getCommand(long revision, String svnManagedFile, String exportOutputFile) {
		String cmdExportCommand = StringUtils.replace(SvnConstants.SVN_EXPORT_REV_CMD_TEMPLATE, "URL_FILE", svnManagedFile);
		cmdExportCommand = StringUtils.replace(cmdExportCommand, "EXPORTED_JAVA_FILEPATH", exportOutputFile);
		cmdExportCommand = StringUtils.replace(cmdExportCommand, "REVISION", String.valueOf(revision));
		return cmdExportCommand;
	}
}