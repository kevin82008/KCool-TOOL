/**
 * kevin 2015年8月27日
 */
package com.drive.cool.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 目前采用非分布式的方式处理<br>
 * @author kevin
 *
 */
public class RedisClient implements IRedisClient, InitializingBean{
	private JedisPool jedisPool;
	private String host;
	private int port = 0;
	private int maxTotal = 0;
	private int minIdle = 0;
	private int timeout = 0;
	private static int TIMEOUT_UNIT_TRANS = 60;
	private int timeoutSecond = 0;
	private static final Log log = LogFactory.getLog(RedisClient.class);
	/**
     * 初始化非分布式池
     */
    private void initialPool() 
    { 
        // 池基本配置 
        JedisPoolConfig config = new JedisPoolConfig(); 
        setMaxTotal(config);
        setMinIdle(config);
        
        config.setMaxWaitMillis(1000L); 
        config.setTestOnBorrow(false); 
        
        host = StringUtils.isEmpty(host) ? "localhost" : host;
        port = (0 == port) ? 6379 : port;
        timeout = (0 == timeout) ? 30 : timeout;
        timeoutSecond = timeout * TIMEOUT_UNIT_TRANS;
        jedisPool = new JedisPool(config,host,port);
    }

	/**
	 * @param config
	 */
	private void setMinIdle(JedisPoolConfig config) {
		if( 0 == minIdle){
        	minIdle = 5;
        }else if(minIdle > 50){
        	minIdle = 50;
        }
        config.setMinIdle(minIdle);
	}

	/**
	 * @param config
	 */
	private void setMaxTotal(JedisPoolConfig config) {
		if( 0 == maxTotal){
        	maxTotal = 100;
        }else if(maxTotal > 1000){
        	maxTotal = 1000;
        }
        config.setMaxTotal(maxTotal);
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		initialPool();
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the maxTotal
	 */
	public int getMaxTotal() {
		return maxTotal;
	}

	/**
	 * @param maxTotal the maxTotal to set
	 */
	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	/**
	 * @return the minIdle
	 */
	public int getMinIdle() {
		return minIdle;
	}

	/**
	 * @param minIdle the minIdle to set
	 */
	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	/* (non-Javadoc)
	 * @see com.drive.cool.session.IRedisClient#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public void set(String key, byte[] value, int timeout) {
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			byte[] keyByte = key.getBytes();
			if(0 == timeout) timeout = this.timeoutSecond;
			jedis.set(keyByte, value);
			jedis.expire(keyByte, timeout);
		}catch(Exception e){
			log.error("取缓存异常", e);
		}finally{
			if(null != jedis)
			jedisPool.returnResource(jedis);
		}
	}

	/* (non-Javadoc)
	 * @see com.drive.cool.session.IRedisClient#get(java.lang.String)
	 */
	@Override
	public byte[] get(String key, int timeout) {
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			byte[] byteKey = key.getBytes();
			byte[] result = jedis.get(byteKey);
			if(null != result && timeout > 0){
				jedis.expire(byteKey, timeout);
			}
			return result;
		}catch(Exception e){
			log.error("取缓存异常", e);
			return new byte[0];
		}finally{
			if(null != jedis)
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}


	/* (non-Javadoc)
	 * @see com.drive.cool.redis.IRedisClient#set(java.lang.String, java.lang.String)
	 */
	@Override
	public void set(String cacheKey, String value) {
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			jedis.set(cacheKey, value);
		}catch(Exception e){
			log.error("取缓存异常", e);
		}finally{
			if(null != jedis)
			jedisPool.returnResource(jedis);
		}
	}

	/* (non-Javadoc)
	 * @see com.drive.cool.redis.IRedisClient#set(java.lang.String, java.util.Map)
	 */
	@Override
	public void set(String cacheKey, Map<String, String> map) {
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			jedis.del(cacheKey);
			jedis.hmset(cacheKey, map);
		}catch(Exception e){
			log.error("取缓存异常", e);
		}finally{
			if(null != jedis)
			jedisPool.returnResource(jedis);
		}
	}

	/* (non-Javadoc)
	 * @see com.drive.cool.redis.IRedisClient#set(java.lang.String, java.util.List)
	 */
	@Override
	public void set(String cacheKey, List<String> list) {
		Jedis jedis = null;
		String[] valueArr = new String[list.size()];
		int i = 0;
		try{
			jedis = jedisPool.getResource();
			jedis.del(cacheKey);
			for(String key : list){
				valueArr[i++] = key;
			}
			jedis.rpush(cacheKey, valueArr);
		}catch(Exception e){
			log.error("取缓存异常", e);
		}finally{
			if(null != jedis)
			jedisPool.returnResource(jedis);
		}
	}

	/* (non-Javadoc)
	 * @see com.drive.cool.redis.IRedisClient#getString(java.lang.String)
	 */
	@Override
	public String getString(String cacheKey) {
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			return jedis.get(cacheKey);
		}catch(Exception e){
			log.error("取缓存异常", e);
			return "";
		}finally{
			if(null != jedis)
			jedisPool.returnResource(jedis);
		}
	}

	/* (non-Javadoc)
	 * @see com.drive.cool.redis.IRedisClient#getString(java.lang.String, java.lang.String)
	 */
	@Override
	public String getString(String cacheKey, String... keys) {
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			List<String> values = jedis.hmget(cacheKey, keys);
			if(0 == values.size()) return null;
			if(1 == values.size()) return values.get(0);
			String result = "";
			for(String value : values){
				result = result + "," + value;
			}
			return result.substring(1);
		}catch(Exception e){
			log.error("取缓存异常", e);
			return "";
		}finally{
			if(null != jedis)
			jedisPool.returnResource(jedis);
		}
	}

	/* (non-Javadoc)
	 * @see com.drive.cool.redis.IRedisClient#getList(java.lang.String)
	 */
	@Override
	public List<String> getList(String cacheKey) {
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			return jedis.lrange(cacheKey, 0, -1);
		}catch(Exception e){
			log.error("取缓存异常", e);
			return new ArrayList<String>();
		}finally{
			if(null != jedis)
				jedisPool.returnResource(jedis);
		}
	}

	/* (non-Javadoc)
	 * @see com.drive.cool.redis.IRedisClient#remove(java.lang.String)
	 */
	@Override
	public void remove(String cacheKey) {
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			jedis.del(cacheKey);
		}catch(Exception e){
			log.error("移除缓存异常", e);
		}finally{
			if(null != jedis)
			jedisPool.returnResource(jedis);
		}
	}

	/* (non-Javadoc)
	 * @see com.drive.cool.redis.IRedisClient#set(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void set(String cacheKey, String key, String value) {
		Jedis jedis = jedisPool.getResource();
		try{
			jedis.hset(cacheKey, key, value);
		}catch(Exception e){
			log.error("设置缓存异常", e);
		}finally{
			if(null != jedis)
			jedisPool.returnResource(jedis);
		}
	}

	@Override
	public void set(byte[] cacheKey, byte[] key, byte[] value) {
		Jedis jedis = jedisPool.getResource();
		try{
			jedis.hset(cacheKey, key, value);
		}catch(Exception e){
			log.error("设置缓存异常", e);
		}finally{
			if(null != jedis)
			jedisPool.returnResource(jedis);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.drive.cool.redis.IRedisClient#get(java.lang.String)
	 */
	@Override
	public Map<String, String> hmget(String cacheKey, int timeout) {
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			Map result = jedis.hgetAll(cacheKey);
			if(0 < timeout){
				jedis.expire(cacheKey, timeout * TIMEOUT_UNIT_TRANS);
			}
			return result;
		}catch(Exception e){
			log.error("取缓存异常", e);
			return new HashMap<String, String>();
		}finally{
			if(null != jedis)
			jedisPool.returnResource(jedis);
		}
	}

	/* (non-Javadoc)
	 * @see com.drive.cool.redis.IRedisClient#set(byte[], java.util.Map)
	 */
	@Override
	public void set(byte[] cacheKey, Map<byte[], byte[]> map) {
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			jedis.del(cacheKey);
			jedis.hmset(cacheKey, map);
		}catch(Exception e){
			log.error("取缓存异常", e);
		}finally{
			if(null != jedis)
			jedisPool.returnResource(jedis);
		}
	}

	/* (non-Javadoc)
	 * @see com.drive.cool.redis.IRedisClient#hmget(java.lang.String, java.lang.String)
	 */
	@Override
	public byte[] hget(String cacheKey, String key) {
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			byte[] byteCacheKey = cacheKey.getBytes();
			byte[] byteKey = key.getBytes();
			byte[] result = jedis.hget(byteCacheKey, byteKey);
			return result;
		}catch(Exception e){
			log.error("取缓存异常", e);
			return new byte[0];
		}finally{
			if(null != jedis)
			jedisPool.returnResource(jedis);
		}
	}

	/* (non-Javadoc)
	 * @see com.drive.cool.redis.IRedisClient#set(byte[], java.util.Map, int)
	 */
	@Override
	public void set(byte[] cacheKey, Map<byte[], byte[]> map, int timeout) {
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			jedis.del(cacheKey);
			jedis.hmset(cacheKey, map);
			jedis.expire(cacheKey, timeout * TIMEOUT_UNIT_TRANS);
		}catch(Exception e){
			log.error("取缓存异常", e);
		}finally{
			if(null != jedis)
			jedisPool.returnResource(jedis);
		}
	}

	/* (non-Javadoc)
	 * @see com.drive.cool.redis.IRedisClient#set(java.lang.String, java.util.Map, int)
	 */
	@Override
	public void set(String cacheKey, Map<String, String> map, int timeout) {
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			jedis.del(cacheKey);
			jedis.hmset(cacheKey, map);
			if(0 < timeout){
				jedis.expire(cacheKey, timeout * TIMEOUT_UNIT_TRANS);
			}
		}catch(Exception e){
			log.error("设置缓存异常", e);
		}finally{
			if(null != jedis)
			jedisPool.returnResource(jedis);
		}
	}

	/* (non-Javadoc)
	 * @see com.drive.cool.redis.IRedisClient#hmget(java.lang.String, java.lang.String[])
	 */
	@Override
	public List<byte[]> hmget(String cacheKey, String... key) {
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			byte[] byteCacheKey = cacheKey.getBytes();
			int length = key.length;
			byte[][] byteKey =  new byte[length][];
			for(int index = 0; index < length; index++){
				byteKey[index] = key[index].getBytes();
			}
			List<byte[]>  result = jedis.hmget(byteCacheKey, byteKey);
			return result;
		}catch(Exception e){
			log.error("取缓存异常", e);
			return new ArrayList<byte[]>();
		}finally{
			if(null != jedis)
			jedisPool.returnResource(jedis);
		}
	}
	
}
