/**
 * kevin 2015年11月7日
 */
package com.drive.cool.tool.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author kevin
 *
 */
public class AuthUtil {

	/**
	 * @param hasAuthRole 有权限的byte数组
	 * @param currRole 当前的byte数组
	 * @return
	 */
	public static  boolean checkAuth(byte[] hasAuthRole, byte[] currRole){
		if(null == hasAuthRole) return false;
		int length = Math.min(hasAuthRole.length, currRole.length);
		for(int i = 1; i < length; i++){
			if((hasAuthRole[i] & currRole[i]) > 0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param version 当前版本号
	 * @param allList 所有权限的列表
	 * @param authStr 权限id串，逗号分隔
	 * @return 转化后的byte数组
	 */
	public static byte[] toAuthByte(byte version, List<String> allList, String authStr){
		byte[] valueByte = {0x01,0x02,0x04,0x08,0x10,0x20,0x40};
		//为了方便后续处理，只使用7位
		int resultLenth = (int)Math.ceil(allList.size()/7d) + 1;
		byte[] result = new byte[resultLenth];
		result[0] = version;
		int resultIndex = 1;
		int byteIndex = 0;
		byte value = 0x00;
		Set<String> authSet = getAuthSet(authStr);
		for(String str : allList){
			if(authSet.contains(str)){
				value = (byte) (value | valueByte[byteIndex]);
			}
			if(isLastByte(byteIndex)){
				result[resultIndex++] = value;
				value = 0x00;
				byteIndex=0;
			}else{
				byteIndex++;
			}
		}
		if(hasSuffixByte(byteIndex)){
			result[resultIndex] = value;
		}
		return result;
	}
	
	/**
	 * 
	 * @param version 当前版本号
	 * @param allList 所有权限的列表
	 * @param authList 权限id串的列表，每个id串逗号分隔
	 * @return 转化后的byte数组
	 */
	public static byte[] toAuthByte(byte version, List<String> allList, List<String> authList){
		byte[] valueByte = {0x01,0x02,0x04,0x08,0x10,0x20,0x40};
		//为了方便后续处理，只使用7位
		int authSize = authList.size();
		int resultLenth = ((int)Math.ceil(allList.size()/7d)) * authSize + 1;
		byte[] result = new byte[resultLenth];
		result[0] = version;
		int resultIndex = 1;
		int byteIndex = 0;
		byte value = 0x00;
		for(String authStr : authList){
			Set<String> authSet = getAuthSet(authStr);
			for(String str : allList){
				if(authSet.contains(str)){
					value = (byte) (value | valueByte[byteIndex]);
				}
				if(isLastByte(byteIndex)){
					result[resultIndex++] = value;
					value = 0x00;
					byteIndex=0;
				}else{
					byteIndex++;
				}
			}
			if(hasSuffixByte(byteIndex)){
				result[resultIndex] = value;
				byteIndex=0;
			}
		}
		
		return result;
	}

	/**
	 * @param byteIndex
	 * @return
	 */
	private static boolean hasSuffixByte(int byteIndex) {
		return 0 != byteIndex;
	}

	/**
	 * @param byteIndex
	 * @return
	 */
	private static boolean isLastByte(int byteIndex) {
		return 6 == byteIndex;
	}

	/**
	 * @param authStr
	 * @return
	 */
	private static Set<String> getAuthSet(String authStr) {
		Set<String> strSet = new HashSet<String>();
		String[] inStrArr = authStr.split(",");
		for(String perStr : inStrArr){
			strSet.add(perStr);
		}
		return strSet;
	}
	
	
	public static boolean authVersionCheck(byte[] currAuth, byte[] checkAuth){
		if(isEmptyArr(currAuth) || isEmptyArr(checkAuth)){
			return true;
		}
		return currAuth[0] == checkAuth[0];
	}
	
	public static boolean isEmptyArr(byte[] arr){
		return null == arr || 0 == arr.length;
	}
}
