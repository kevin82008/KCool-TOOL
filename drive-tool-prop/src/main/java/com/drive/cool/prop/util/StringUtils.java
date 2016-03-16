package com.drive.cool.prop.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

	/**
	 * 是否不为空判断，空格不认为为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 是否为空判断，空格不认为为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return (str == null) || (str.length() == 0);
	}

	/**
	 * 是否包含判断
	 * 
	 * @param str
	 * @param searchChar
	 * @return
	 */
	public static boolean contains(String str, char searchChar) {
		if (isEmpty(str)) {
			return false;
		}
		return str.indexOf(searchChar) >= 0;
	}

	/**
	 * 是否包含判断
	 * 
	 * @param str
	 * @param searchStr
	 * @return
	 */
	public static boolean contains(String str, String searchStr) {
		if ((str == null) || (searchStr == null)) {
			return false;
		}
		return str.indexOf(searchStr) >= 0;
	}

	/**
	 * 字符串转成数组，空忽略，空格保留
	 * 
	 * @param str
	 * @param separatorChars
	 * @return
	 */
	public static String[] split(String str, String separatorChars) {
		return splitWorker(str, separatorChars, -1, false);
	}

	public static String[] split(String str, char separatorChar) {
		return splitWorker(str, separatorChar, false);
	}

	public static String[] splitPreserveAllTokens(String str,
			String separatorChars) {
		return splitWorker(str, separatorChars, -1, true);
	}

	private static String[] splitWorker(String str, String separatorChars,
			int max, boolean preserveAllTokens) {
		if (str == null) {
			return null;
		}
		int len = str.length();
		if (len == 0) {
			return new String[0];
		}
		List list = new ArrayList();
		int sizePlus1 = 1;
		int i = 0;
		int start = 0;
		boolean match = false;
		boolean lastMatch = false;
		if (separatorChars == null) {
			while (i < len) {
				if (Character.isWhitespace(str.charAt(i))) {
					if ((match) || (preserveAllTokens)) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					i++;
					start = i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		}
		if (separatorChars.length() == 1) {
			char sep = separatorChars.charAt(0);
			while (i < len) {
				if (str.charAt(i) == sep) {
					if ((match) || (preserveAllTokens)) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					i++;
					start = i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		} else {
			while (i < len) {
				if (separatorChars.indexOf(str.charAt(i)) >= 0) {
					if ((match) || (preserveAllTokens)) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					i++;
					start = i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		}
		if ((match) || ((preserveAllTokens) && (lastMatch))) {
			list.add(str.substring(start, i));
		}
		return (String[]) (String[]) list.toArray(new String[list.size()]);
	}

	public static String[] splitPreserveAllTokens(String str, char separatorChar) {
		return splitWorker(str, separatorChar, true);
	}

	private static String[] splitWorker(String str, char separatorChar,
			boolean preserveAllTokens) {
		if (str == null) {
			return null;
		}
		int len = str.length();
		if (len == 0) {
			return new String[0];
		}
		List list = new ArrayList();
		int i = 0;
		int start = 0;
		boolean match = false;
		boolean lastMatch = false;
		while (i < len) {
			if (str.charAt(i) == separatorChar) {
				if ((match) || (preserveAllTokens)) {
					list.add(str.substring(start, i));
					match = false;
					lastMatch = true;
				}
				i++;
				start = i;
				continue;
			}
			lastMatch = false;
			match = true;
			i++;
		}
		if ((match) || ((preserveAllTokens) && (lastMatch))) {
			list.add(str.substring(start, i));
		}
		return (String[]) (String[]) list.toArray(new String[list.size()]);
	}
	public static void main(String[] args) {
		System.out.println("");
	}

	
}
