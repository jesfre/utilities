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
	 * @param svnManagedFile
	 * @param exportOutputFile
	 * @return the path to the exported file
	 */
	public String export(String svnManagedFile, String exportOutputFile) {
		String cmdExportCommand = getCommand(svnManagedFile, exportOutputFile);

		CommandLineRunner runner = new CommandLineRunner();
		runner.setVerbose(verbose);
		runner.executeCommand(cmdExportCommand);
		return exportOutputFile;
	}

	public String getCommand(String svnManagedFile, String exportOutputFile) {
		String cmdExportCommand = StringUtils.replace(SvnConstants.SVN_EXPORT_CMD_TEMPLATE, "URL_FILE", svnManagedFile);
		cmdExportCommand = StringUtils.replace(cmdExportCommand, "EXPORTED_JAVA_FILEPATH", exportOutputFile);
		return cmdExportCommand;
	}
}