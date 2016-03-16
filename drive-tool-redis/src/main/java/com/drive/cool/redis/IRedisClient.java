/**
 * kevin 2015年8月27日
 */
package com.drive.cool.redis;

import java.util.List;
import java.util.Map;

/**
 * @author kevin
 *
 */
public interface IRedisClient {
	/**
	 * 写入byte数组，有有效期<br>
	 * @param cacheKey
	 * @param value
	 * @param timeout 超时时间，单位秒
	 */
	public void set(String cacheKey, byte[] value,  int timeout);
	
	/**
	 * 写入字符串，永久有效<br>
	 * @param cacheKey
	 * @param value
	 */
	public void set(String cacheKey, String value);
	
	/**
	 * 写入map，永久有效<br>
	 * @param cacheKey
	 * @param map
	 */
	public void set(byte[] cacheKey, Map<byte[], byte[]> map);
	
	/**
	 * 写入map，永久有效<br>
	 * @param cacheKey
	 * @param map
	 */
	public void set(String cacheKey, Map<String, String> map);
	
	/**
	 * 写入map，有有效期<br>
	 * @param cacheKey
	 * @param map
	 * @param timeout 单位 分钟
	 */
	public void set(byte[] cacheKey, Map<byte[], byte[]> map, int timeout);
	
	/**
	 * 写入map，有有效期<br>
	 * @param cacheKey
	 * @param map
	 * @param timeout 单位 分钟
	 */
	public void set(String cacheKey, Map<String, String> map, int timeout);
	
	
	
	/**
	 * 写入map的一个值，永久有效<br>
	 * @param cacheKey<br>
	 * @param key map里的key
	 * @param value map里的值
	 * @param map
	 */
	public void set(String cacheKey, String key, String value);
	
	/**
	 * 写入list，永久有效<br>
	 * @param cacheKey
	 * @param list
	 */
	public void set(String cacheKey, List<String> list);
	
	
	
	/**
	 * 获取byte数组，增加有效期<br>
	 * @param cacheKey
	 * @return
	 */
	public byte[] get(String cacheKey, int timeout);
	
	/**
	 * 获取byte数组<br>
	 * @param cacheKey
	 * @param key map的key
	 * @return
	 */
	public byte[] hget(String cacheKey, String key);
	
	/**
	 * 获取多个btye数组
	 * @param cacheKey
	 * @param key
	 * @return
	 */
	public List<byte[]> hmget(String cacheKey, String... key);
	
	/**
	 * 获取字符串，本来存的就是字符串<br>
	 * @param cacheKey
	 * @return
	 */
	public String getString(String cacheKey);
	
	/**
	 * 获取字符串，本来存的是map<br>
	 * @param cacheKey，缓存的key
	 * @param keys map的key，支持一次取多个
	 * @return
	 */
	public String getString(String cacheKey, String... keys);
	
	/**
	 * 获取列表，本来存的是列表<br>
	 * @param cacheKey
	 * @return
	 */
	public List<String> getList(String cacheKey);
	
	/**
	 * 获取map，本来存的是map 增加有效期
	 * @param cacheKey
	 * @param timeout 单位 分钟
	 * @return
	 */
	public Map<String, String> hmget(String cacheKey, int timeout);
	
	
	/**
	 * 移除cache<br>
	 * @param cacheKey
	 */
	public void remove(String cacheKey);
	
	/**
	 * 存储byte[]，整个是map存储的，这里是更新其中一个key值
	 * @param cacheKey
	 * @param key
	 * @param value
	 */
	public void set(byte[] cacheKey, byte[] key, byte[] value);
}
