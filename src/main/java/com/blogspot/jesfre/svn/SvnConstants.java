package com.blogspot.jesfre.svn;

import org.apache.commons.lang.StringUtils;

public class SvnConstants {

	public static final String SVN_DIFF_CMD_TEMPLATE = "svn diff -r HEAD:PREV_REV SVN_REPO_FILE_LOCATION > OUTPUT_DIFF_FILE";
	public static final String SVN_EXPORT_CMD_TEMPLATE = "svn export OPT1 URL_FILE EXPORTED_JAVA_FILEPATH";
	public static final String SVN_EXPORT_REV_CMD_TEMPLATE = "svn export OPT1 -r REVISION URL_FILE EXPORTED_JAVA_FILEPATH";

	public static final String LOG_OPT_VERBOSE = "-v";
	public static final String EXPORT_OPT_FORCE = "--force";

	// svn log --limit 999 "/path/to/file" >> "/path/to/output/file"
	public static final String SVN_LOG_BY_LIMIT_CMD = "svn log %s --limit %d %s >> %s";
	public static final String SVN_LOG_BY_LIMIT_NO_EXPORT_CMD = "svn log %s --limit %d %s";

	// svn log --search "*comment*" "/path/to/file" >> /path/to/output/file"
	public static final String SVN_LOG_BY_COMMENT_CMD = "svn log %s --search \"*%s*\" %s >> %s";
	public static final String SVN_LOG_BY_COMMENT_NO_EXPORT_CMD = "svn log %s --search \"*%s*\" %s";

	// svn log -r {2023-11-20}:{2024-01-29}
	public static final String SVN_LOG_BY_DATE_RANGE_CMD = "svn log -r {{0}}:{{1}}";
	public static final String CMD_FILE_PATH = "/svn-logs/svnlog-%s.cmd";
	public static final String LOG_FILE_PATH = "/svn-logs/%s.log.txt";
	public static final String LOGS_FOLDER_PATH = "/svn-logs";
	public static final String LOG_SEPARATOR = StringUtils.repeat("-", 72);
	public static final String LOG_CHANGED_PATHS_START = "Changed paths:";

}