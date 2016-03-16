package com.drive.cool.tool.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class TimeElapse {
	private static  int WAIT_TIME = 10;
	private Log log = null;
	private ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(2);
	private final ReentrantReadWriteLock trwl = new ReentrantReadWriteLock();
	private final Lock tw = trwl.writeLock();
	private ConcurrentHashMap<String, TimeInfo> TIME_MAP = new ConcurrentHashMap<String, TimeInfo>();
	private static final String EMPTY_STR = " ";
	public void addTimeInfo(String name) {
		TimeInfo info = new TimeInfo(name);
		TIME_MAP.put(name, info);
	}
	
	public TimeElapse(String logId) {
		this.log = LogFactory.getLog(logId);
	}
	
	public TimeElapse() {
		this.log = LogFactory.getLog("time");
	}
	
	public TimeElapse(int printInteval) {
		WAIT_TIME = printInteval;
		this.log = LogFactory.getLog("time");
	}
	
	public void startLog() {
		threadPool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				tw.lock();
				try{
					if(log.isInfoEnabled()){
						Date now = new Date();
						SimpleDateFormat datefmt = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.CHINA);
						log.info("上次打印到当前[" + datefmt.format(now) + "]执行情况");
						for(String key : TIME_MAP.keySet()){
							TimeInfo info = TIME_MAP.get(key);
							log.info(info.toString());
							info.init();
						}
					}
				}finally{
					tw.unlock();
				}				
			}
		}, 0, WAIT_TIME, TimeUnit.SECONDS);
	}

	public void calculate(String name, long startTime) {
		tw.lock();
		try{
			long endTime = System.nanoTime();
			TimeInfo info = TIME_MAP.get(name);
			info.calculate(startTime, endTime);
		}finally{
			tw.unlock();
		}
	}

	private class TimeInfo{
		private String name;
		private long cumulative;
		private long times;
		/**
		 * 最慢的
		 */
		private long high = 0L;
		/**
		 * 最快的
		 */
		private long low = Long.MAX_VALUE;
		
		
		public TimeInfo(String name) {
			this.name = name;
		}
		public long getCumulative() {
			return cumulative;
		}
		public void setCumulative(long cumulative) {
			this.cumulative = cumulative;
		}
		public long getTimes() {
			return times;
		}
		public void setTimes(long times) {
			this.times = times;
		}
		public long getHigh() {
			return high;
		}
		public void setHigh(long high) {
			this.high = high;
		}
		public long getLow() {
			return low;
		}
		public void setLow(long low) {
			this.low = low;
		}
		
		public void init() {
			high = 0L;
			low = Long.MAX_VALUE;
			times = 0L;
			cumulative = 0L;
		}
		
		public void calculate(long startTime, long endTime) {
			long elapse = endTime - startTime;
			elapse = elapse < 0 ? 0 : elapse;
			if (elapse > high) {
				high = elapse;
			}
			if (elapse < low) {
				low = elapse;
			}
			cumulative += elapse;
			times++;
			if(log.isDebugEnabled()){
				log.debug(this.name + ": " + elapse/1000000 + "ms");
			}
		}

		@Override
		public String toString() {
			return "[" + name + "] -> 调用次数: " + lPad(String.valueOf(times),
					15) + ",  花费时间: " + lPad(String.valueOf((float) cumulative / 1000000), 15)
					+ "ms,  最慢: " +  (high == 0L ? lPad("-", 15) : lPad(String.valueOf((float) high / 1000000), 15))
					+ "ms,  最快: " +  (low == Long.MAX_VALUE ? lPad("-", 15) : lPad(String.valueOf((float) low / 1000000), 15))
					+ "ms,  平均: " +  lPad(String.valueOf((float) (cumulative / 1000000) / times), 15)
					+ "ms";
		}
		
	}
	
	private String lPad(String info,int length) {
		StringBuffer infoBuffer = new StringBuffer();
		if(null != info){
			infoBuffer.append(info);
		}
		int len = length - infoBuffer.length();
		for(int i=0; i < len; i++){
			infoBuffer.insert(0, EMPTY_STR);
		}
		return infoBuffer.toString();
	}
	
	public static void main(String[] args) {
		TimeElapse a = new TimeElapse();
		a.addTimeInfo("test");
		a.addTimeInfo("test2");
		a.addTimeInfo("test3");
		
		a.startLog();
		for(int i = 0; i< 10000;i++){
			a.calculate("test", System.nanoTime());
		}
	}
	
}