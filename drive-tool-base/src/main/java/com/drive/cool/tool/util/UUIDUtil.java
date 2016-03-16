/**
 * kevin 2015年10月21日
 */
package com.drive.cool.tool.util;

import java.util.UUID;

/**
 * @author kevin
 *
 */
public class UUIDUtil {
	public static final String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
