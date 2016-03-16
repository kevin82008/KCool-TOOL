package com.drive.cool.tool.util;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;


/**
 * 解析sql的查询字段和条件字段，按在sql里出现的顺序排序
 * @author kevin
 *
 */
public class SqlUtil {

	private final static Pattern FROM_PATTERN = Pattern.compile("[)\\s]from\\s", Pattern.CASE_INSENSITIVE);
	
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
	//匹配括号，最多只匹配到4级括号
	private final static Pattern BRACKET_PATTERN  = Pattern.compile("\\([^\\(\\)]*(\\([^\\(\\)]*(\\([^\\(\\)]*(\\([^\\(\\)]*\\)[^\\(\\)]*)*\\)[^\\(\\)]*)*\\)[^\\(\\)]*)*\\)");
	
	//匹配大括号，只会有一级大括号
	private final static Pattern BRACE_PATTERN = Pattern.compile("(\\{)(.*?)(\\})");
	
	public static List getColumnIdList(String sql){
		//先把第一个from前面的内容解析出来
		int firstFromIdx = getFirstIndex(FROM_PATTERN, sql);
		String fromSql = sql.substring(0, firstFromIdx+1);
		
		//把所有括号里的内容替换掉
		fromSql = replaceAllBracket(fromSql);
		fromSql = StringUtils.trim(fromSql);
		
		//提取字段
		List result = new ArrayList<String>();
		extractColumnId(result, fromSql);
		
		return result;
	}
	
	/**
	 * 初步处理后的sql提取字段
	 * @param sql
	 */
	private static void extractColumnId(List result, String sql){
		//根据 逗号提取字段，分割后按空格拆分，最后一个值就是字段
		String[] columnArr = StringUtils.split(sql, ",");
		for(String columnInfo : columnArr){
			extractColumnByDetail(result, columnInfo);
		}
	}

	/**
	 * 根据逗号拆分后的字段提取
	 * @param result
	 * @param columnInfo 类似 a.xx as columnA 或者 a.xx 或者 xx 的形式
	 */
	private static void extractColumnByDetail(List result, String columnInfo) {
		String[] detail = StringUtils.split(columnInfo, " ");
		String columnId = detail[detail.length-1].trim();
		//如果是 a.xx 的形式，再去除小数点
		int index = columnId.indexOf(".");
		if(index >= 0){
			columnId = columnId.substring(index + 1);
		}
		result.add(columnId);
	}
	
	/**
	 * 获取所有的查询参数。参数的形式是 {参数} 所以提取大括号里的内容就可以
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
	
	/**
	 * 把括号里的内容替换掉，如 count(a.xx) as xx 替换成 count() as xx
	 * @param str
	 * @return
	 */
	private static String replaceAllBracket(String str){
		Matcher match = BRACKET_PATTERN.matcher(str);
		while (match.find()) {
			String content = match.group();
			str = str.replace(content, "");
			match = BRACKET_PATTERN.matcher(str);
		} 
		return str;
	}
	public static void main(String[] args) {
		String sql = "select 1 as test1,sum(a.xx) as test2,"
				+ "sum(a.xx,decode(a.xx,1,1,0),b.xx) as test2,c.xx from table n "
				+ "where n.ax={test1} and n.ab = {test2} group by c.xx ";
		System.out.println(getColumnIdList(sql));
		System.out.println(getContitionList(sql));
	
	}
	
	/**
	 * 获取第一个匹配的索引
	 * @param p
	 * @param sb
	 * @return
	 */
	public static int getFirstIndex(Pattern p, String sb){
		Matcher m = p.matcher(sb);
		int index =-1;
		if(m.find()){
			index = m.start();
		}
		return index;
	}
}
