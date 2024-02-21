package com.blogspot.jesfre.svn.utils;

import org.apache.commons.lang.StringUtils;

import com.blogspot.jesfre.commandline.CommandLineRunner;

public class SvnDiff {

	public String exportDiff(String svnManagedFile, String diffOutputFile, long headRevision, long previousRevision) {
		String svnDiffCommand = getCommand(svnManagedFile, diffOutputFile, headRevision, previousRevision);
		CommandLineRunner runner = new CommandLineRunner();
		runner.executeCommand(svnDiffCommand);
		return diffOutputFile;
	}

	public String getCommand(String svnManagedFile, String diffOutputFile, long headRevision, long previousRevision) {
		String svnDiffCommand = StringUtils.replace(SvnConstants.SVN_DIFF_CMD_TEMPLATE, "SVN_REPO_FILE_LOCATION", svnManagedFile);
		svnDiffCommand = StringUtils.replace(svnDiffCommand, "HEAD", Long.toString(headRevision));
		svnDiffCommand = StringUtils.replace(svnDiffCommand, "PREV_REV", Long.toString(previousRevision));
		svnDiffCommand = StringUtils.replace(svnDiffCommand, "OUTPUT_DIFF_FILE", diffOutputFile);
		return svnDiffCommand;
	}
}