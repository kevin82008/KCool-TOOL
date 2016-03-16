package com.drive.cool.prop.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyUtil {


	/**
	 * 从properties配置文件中读取默认属性和传入的段的所有属性的集合 　　
	 * 
	 * @param file
	 *            配置文件的路径，可以是jar的相对路径 　　
	 * @param section
	 *            要获取的变量所在段名称　　
	 * @param type
	 *            获取的类型，file是普通目录的文件 jarFile jar里的文件　
	 *           
	 * @return * @throws IOException 抛出文件操作可能出现的io异常 　　
	 */
	public static Map getPropertiesMap(String file, String section,String type)
			throws IOException {
		Map propertiesMap = new HashMap<String, Object>();
		String sectionName = "common";
		String strLine, key = "", value = "";
		boolean isInSpetionSection = false;
		String sectionType = "";
		BufferedReader bufferedReader = null;
		if("file".equals(type)){
			bufferedReader = new BufferedReader(new FileReader(file));
		}else if("jarfile".equals(type)){
			InputStream is=PropertyUtil.class.getResourceAsStream(file);   
			bufferedReader = new BufferedReader(new InputStreamReader(is));  
		}

		try {
			while ((strLine = bufferedReader.readLine()) != null) {
				strLine = strLine.trim();
				// 跳过注释行和空行
				if (strLine.startsWith("#") || "".equals(strLine)) {
					continue;
				}
				// 如果开始读到 [ 而且selectionName还等于common，说明此时进入未进入过分段区域
				if (strLine.startsWith("[")) {
					sectionType = selectionTypeCheck(strLine, section);
					if ("common".equals(sectionType)) {
						continue;
					} else if ("special".equals(sectionType)) {
						isInSpetionSection = true;
					} else if ("other".equals(sectionType)
							&& (isInSpetionSection || "".equals(section))) {
						// 如果当前进入了其他段而且进入过特殊的段或者不需要取特殊段的数据，可以直接退出了
						break;
					}
				} else if (!"other".equals(sectionType)) {
					// 不是在其他分段里的时候，设置属性值
					setPropValue(propertiesMap, strLine);
				}
			}
		} finally {
			bufferedReader.close();
		}
		return propertiesMap;
	}

	private static void setPropValue(Map propertiesMap, String strLine) {
		String key = null;
		String value = null;
		String[] strArray = strLine.split("=");
		key = strArray[0];
		if (1 == strArray.length) {
			value = null;
		} else if (2 == strArray.length) {
			value = strArray[1];
		} else{
			value = strLine.substring(key.length()+1);
		}
		propertiesMap.put(key, decodeUnicode(value));
	}

	private static String selectionTypeCheck(String strLine, String section) {
		String result = "other";
		Pattern p;
		Matcher m;
		p = Pattern.compile("\\[common\\]");
		m = p.matcher(strLine);
		if (m.matches()) {
			result = "common";
		} else {
			p = Pattern.compile("\\[" + section + "\\]");
			m = p.matcher(strLine);

			if (m.matches()) {
				result = "special";
			}
		}
		return result;
	}

	//unicode解码
	private static String decodeUnicode(final String sourceStr) {
		if(null == sourceStr){
			return null;
		}
		int start = sourceStr.indexOf("\\u", 0);
		if(-1 == start){
			return sourceStr;
		}
		int oldStart = 0;
		final StringBuffer buffer = new StringBuffer();
		while (start > -1) {
			if(start > oldStart){
				buffer.append(sourceStr.substring(oldStart,start));
			}
			String charStr = sourceStr.substring(start + 2, start + 6);
			char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
			buffer.append(new Character(letter).toString());
			oldStart = start + 6;
			start = sourceStr.indexOf("\\u", oldStart);
			if(-1 == start && oldStart < sourceStr.length()){
				buffer.append(sourceStr.substring(oldStart));
			}
		}
		return buffer.toString();
	}

	/**
	 * 获取属性值，未配置时取默认值
	 * @param resource
	 * @param section
	 * @param propName
	 */
	public static String getPropValue(Map resource,String propName,String base){
		Object value = resource.get(propName);
		return null == value?base:String.valueOf(value);
	}
}
