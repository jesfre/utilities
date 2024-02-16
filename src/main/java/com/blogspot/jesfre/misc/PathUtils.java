package com.blogspot.jesfre.misc;

public class PathUtils {

	/**
	 * The path must be a final path and not intended for concatenation
	 * 
	 * @param path
	 * @return
	 */
	public static String formatPath(String path) {
		// Avoid spaces in Windows
		String newPath = "\"" + path + "\"";
		// TODO Format for Window/Unix
		return newPath;
	}

}
