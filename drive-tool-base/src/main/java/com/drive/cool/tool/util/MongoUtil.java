/**
 * kevin 2015年8月10日
 */
package com.drive.cool.tool.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kevin
 *
 */
public class MongoUtil {
	private final static Pattern COLLECTION_PATTERN  = Pattern.compile("(db\\.)(.*?)(\\.find)");
	
	public static String getCollectionName(String sql){
		Matcher match = COLLECTION_PATTERN.matcher(sql);
		String collection = null;
		if(match.find()) {
			collection = match.group(2);
		}
		return collection;
	}
}
