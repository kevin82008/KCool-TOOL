/**
 * kevin 2015年8月8日
 */
package com.drive.cool.tool.util;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * 解析mongodb sql的查询字段和条件字段，按在mongodb sql里出现的顺序排序
 * @author kevin
 *
 */
public class MongoSqlUtil {

	private final static Pattern FIND_PATTERN = Pattern.compile("[)\\.]find", Pattern.CASE_INSENSITIVE);
	
	/**
	 * 参见：http://www.tuicool.com/articles/yemIZf
		匹配二级括号
		String str="(a(b))";
		String regex = "\\([^\\(\\)]*(\\([^\\(\\)]*\\)[^\\(\\)]*)*\\)";
		匹配三级括号
		String str="(a(b(c)))";
		String regex = "\\([^\\(\\)]*(\\([^\\(\\)]*(\\([^\\(\\)]*\\)[^\\(\\)]*)*\\)[^\\(\\)]*)*\\)";
		匹配四级括号
		String str="(a(b(c(d))))";
		String regex = "\\([^\\(\\)]*(\\([^\\(\\)]*(\\([^\\(\\)]*(\\([^\\(\\)]*\\)[^\\(\\)]*)*\\)[^\\(\\)]*)*\\)[^\\(\\)]*)*\\)"
	 */
	//匹配大括号，最多只匹配到4级大括号
	private final static Pattern BRACKET_PATTERN  = Pattern.compile("\\{[^\\{\\}]*(\\{[^\\{\\}]*(\\{[^\\{\\}]*(\\{[^\\{\\}]*\\}[^\\{\\}]*)*\\}[^\\{\\}]*)*\\}[^\\{\\}]*)*\\}");
	
	//匹配#号
	private final static Pattern BRACE_PATTERN = Pattern.compile("(\\#)(.*?)(\\#)");
	
	/**
	 * sql语句是不是包含 find
	 * @param sql
	 * @return
	 */
	private static boolean hasFindCheck(String sql){
		boolean result = false;
		Matcher match = FIND_PATTERN.matcher(sql);
		while (match.find()) {
			result = true;
			break;
		}
		return result;
	}

	
	/**
	 * 获取查询条件的内容
	 * @param sql
	 * @return
	 */
	public static String getConditionStr(String sql){
		return getStrByIndex(sql, 1);
	}
	
	/**
	 * 获取查询字段的内容
	 * @param sql
	 * @return
	 */
	public static String getColumnStr(String sql){
		return getStrByIndex(sql,2);
	}
	
	/**
	 * 获取查询字段的内容
	 * @param sql
	 * @return
	 */
	public static String getOrderByStr(String sql){
		return getStrByIndex(sql,3);
	}
	
	/**
	 * 根据index获取sql内容，其中index是根据一级大括号生成的
	 * @param sql
	 * @param matchIndex
	 * @return
	 */
	private static String getStrByIndex(String sql, int matchIndex){
		int flag = 0;
		int start = 0;
		int end = 0;
		int index = 0;
		int idx = 0;
		char[] arr = sql.toCharArray();
		char[] result = {'{','}'};
		
		for(char str : arr){
			if('{' == str){
				flag++;
				if(1 == flag){
					start = index;
				}
			}else if('}' == str){
				if(1 == flag){
					end = index;
					flag = 0;
					idx++;
					if(matchIndex == idx){
						result = ArrayUtils.subarray(arr, start, end+1);
						break;
					}
				}else{
					flag--;
				}
			}
			index++;
		}
		return String.valueOf(result);
	}
	
	
	public static List getColumnIdList(String sql){
		List result = new ArrayList<String>();
		if(hasFindCheck(sql)){
			//取显示字段的信息，格式为 {column1:1,column2:0,column3:1}
			String columnStr = getColumnStr(sql);
			//变成[{column1:1},{column2:0},{column3:1}]
			columnStr = "[" + columnStr.replaceAll(",", "},{") + "]"; 
			JSONArray columnArray = JSONObject.parseArray(columnStr);
			for(int i=0; i < columnArray.size(); i++){
				JSONObject columnObject = columnArray.getJSONObject(i);
				for(String key : columnObject.keySet()){
					int value = columnObject.getIntValue(key);
					if(1 == value){
						key = key.replaceAll("\"", "").replaceAll("\'", "");
						result.add(key);
					}
				}
			}
		}
		result.add("_id");
		return result;
	}
	
		
	/**
	 * 获取所有的查询参数。参数的形式是 #参数# 所以提取##里的内容就可以
	 * @param sql
	 * @return
	 */
	public static List getContitionList(String sql){
		Matcher match = BRACE_PATTERN.matcher(sql);
		List result = new ArrayList<String>();
		while(match.find()){
			String condition = match.group(2);
			if(!result.contains(condition)){
				result.add(condition);
			}
		}
		return result;
	}
	
		
	
	public static void main(String[] args) {
		String sql = "db.test.find({'$or',[{column1:'中文测试'},{赞哦恩我:'1'}]},{column1:1,column2:1,column3:1,column4:1,_id:0})"; 
		System.out.println(getConditionStr(sql));
		System.out.println(getColumnStr(sql));
	}
}
