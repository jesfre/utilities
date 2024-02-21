package com.blogspot.jesfre.svn.utils;

import org.apache.commons.lang.StringUtils;

public class SvnConstants {

	public static final String SVN_DIFF_CMD_TEMPLATE = "svn diff -r HEAD:PREV_REV SVN_REPO_FILE_LOCATION > OUTPUT_DIFF_FILE";
	public static final String SVN_EXPORT_CMD_TEMPLATE = "svn export URL_FILE EXPORTED_JAVA_FILEPATH";

	// svn log --limit 999 "/path/to/file >> /path/to/output/file"
	public static final String SVN_LOG_BY_LIMIT_CMD = "svn log --limit %d %s >> %s";
	// svn log -r {2023-11-20}:{2024-01-29}
	public static final String SVN_LOG_BY_DATE_RANGE_CMD = "svn log -r {{0}}:{{1}}";
	public static final String CMD_FILE_PATH = "/svn-logs/svnlog_limit-%d-%s.cmd";
	public static final String LOG_FILE_PATH = "/svn-logs/logs_limit-%d-%s.txt";
	public static final String LOGS_FOLDER_PATH = "/svn-logs";
	public static final String LOG_SEPARATOR = StringUtils.repeat("-", 72);

}