/**
 * kevin 2015年10月6日
 */
package com.drive.cool.tool.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author kevin
 *
 */
public class DateUtil {
	private static SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
	
	/**
	 * 获取移动days天后的日期
	 * @param date 字符串格式的日期，格式为 yyyyMMdd
	 * @param days 日期，负数表示date之前
	 * @return
	 */
	public static String moveUpDate(String date, int days){
		Date tmpDate;
		String result = null;
		try {
			tmpDate = simpleDate.parse(date);
			GregorianCalendar gc=new GregorianCalendar(); 
			gc.setTime(tmpDate); 
			gc.add(5,days); 
			result = simpleDate.format(gc.getTime());
		} catch (ParseException e) {
			throw new RuntimeException("日期格式化失败："+ date);
		}
		return result;
	}
}
