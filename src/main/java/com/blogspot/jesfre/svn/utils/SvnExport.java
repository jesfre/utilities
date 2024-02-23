package com.blogspot.jesfre.svn.utils;

import org.apache.commons.lang.StringUtils;

import com.blogspot.jesfre.commandline.CommandLineRunner;

public class SvnExport {

	/**
	 * @param svnManagedFile
	 * @param exportOutputFile
	 * @return the path to the exported file
	 */
	public String export(String svnManagedFile, String exportOutputFile) {
		String cmdExportCommand = getCommand(svnManagedFile, exportOutputFile);

		CommandLineRunner runner = new CommandLineRunner();
		runner.setVerbose(false);
		runner.executeCommand(cmdExportCommand);
		return exportOutputFile;
	}

	public String getCommand(String svnManagedFile, String exportOutputFile) {
		String cmdExportCommand = StringUtils.replace(SvnConstants.SVN_EXPORT_CMD_TEMPLATE, "URL_FILE", svnManagedFile);
		cmdExportCommand = StringUtils.replace(cmdExportCommand, "EXPORTED_JAVA_FILEPATH", exportOutputFile);
		return cmdExportCommand;
	}
}