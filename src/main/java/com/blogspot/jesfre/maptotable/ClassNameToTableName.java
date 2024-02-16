package com.blogspot.jesfre.maptotable;

public class ClassNameToTableName {
	
	public static String toTableName(String className) {
		StringBuilder tableName = new StringBuilder();
		char[] chars = className.toCharArray();
		boolean isInit = true;
		// TODO change this whole thing with a regex
		for (char c : chars) {
			if (isInit) {
				tableName.append(c);
				isInit = false;
			} else {
				if (Character.isUpperCase(c)) {
					tableName.append('_').append(c);
				} else {
					tableName.append(Character.toUpperCase(c));
				}
			}
		}
		return tableName.toString();
	}

}
