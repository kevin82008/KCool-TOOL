/**
 * kevin 2015年9月7日
 */
package com.drive.cool.tool.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author kevin
 *
 */
public class FileUtil {

	/**
	 * 获取文件的根路径
	 * @return
	 */
	public static String getRootPath(){
		return FileUtil.class.getClassLoader().getResource("").getPath();
	}
	
	public static long getFileLength(File file){
		return file.length();
	}
	
	public static String ReadFile(RandomAccessFile file,int start) {
		StringBuffer fileBuffer = new StringBuffer();
		try {
			file.skipBytes(start);
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = file.readLine()) != null) {
				fileBuffer.append(new String(tempString.getBytes("ISO8859_1"),"UTF-8")).append("\n");
			}
			
		} catch (IOException e) {
			throw new RuntimeException("读取文件失败，文件不存在");
		} finally {
			try {
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileBuffer.toString();
	}
}
