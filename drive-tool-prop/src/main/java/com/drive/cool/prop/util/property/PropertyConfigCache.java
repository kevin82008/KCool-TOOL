package com.drive.cool.prop.util.property;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.drive.cool.prop.util.PropertyUtil;

/**
 * 保存resource文件的缓存
 * @author kevin
 *
 */
public class PropertyConfigCache {
	private static final Log log = LogFactory.getLog(PropertyConfigCache.class);
	private static final String PROP_BASE = "BASE";
	private static final String PROP_TYPE = "propType";
	
	/**
	 * prop文件绝对路径
	 */
	private String absolutePath;
	
	/**
	 * prop文件绝对路径 支持jar里的文件
	 */
	private String jarUrlPath;
	
	/**
	 * Property文件里所属的段
	 */
	private String section;
	
	/**
	 * Property文件名
	 */
	private String propName;
	
	/**
	 * 最后更新时间
	 */
	private long lastModified = 0;
	/**
	 * 缓存内容
	 */
	private Map resourceMap;
		
	/**
	 * 额外要写入数据的接口
	 */
	private ExProperties exProp = null;
	

	public PropertyConfigCache(String filePath,String propName,String section,String type,ExProperties exProp){
		this.exProp = exProp;
		refreshCache(filePath, propName, section, type);
	}
	public PropertyConfigCache(String filePath,String propName,String section,String type){
		refreshCache(filePath, propName, section, type);
	}
	private void refreshCache(String filePath, String propName, String section,
			String type) {
		if("jarfile".equals(type)){
			this.jarUrlPath = filePath;
		}else{
			this.absolutePath = filePath;
		}
		this.propName = propName;
		this.section = section;
		refresh();
	}
	
	
	
	public boolean needRefresh(){
		boolean result = false;
		if(null != this.absolutePath){
			File file = new File(absolutePath);
			if(file.lastModified()>lastModified){
				result = true;
			}
		}
		return result;
	}
	
	public void refresh(){
		PropertyConfig prop = null;
		try {
			Map resource = null;
			if(null != absolutePath){
				resource = PropertyUtil.getPropertiesMap(absolutePath,section,"file");	
			}else{
				resource = PropertyUtil.getPropertiesMap(jarUrlPath,section,"jarfile");
			}
			 
			//设置基本属性
			prop = new PropertyConfig(resource);
			prop.setBaseProperties(propName, section);
			
			//根据不同类型属性文件设置扩展属性
			//可以在prop文件里覆盖此属性
			if(null != exProp){
				exProp.setExtProp(resource, propName, section);
			}
		} catch (Exception e) {
			if(null != absolutePath){
				log.error("Cannot get Resource:" + absolutePath);
			}else{
				log.error("Cannot get Resource:" + jarUrlPath);
			}
			e.printStackTrace();
		}
		
		resourceMap = Collections.unmodifiableMap(prop.getResource());
		if(null != absolutePath){
			lastModified = (new File(absolutePath)).lastModified();
		}else{
			lastModified = 0;
		}
	}
	
	
	public Map getResourceMap() {
		if(needRefresh()){
			refresh();
		}
		return resourceMap;
	}
	
	public Map getEditableResourceMap(){
		Map map = new HashMap<String,Object>();
		map.putAll(getResourceMap());
		return map;
	}
}
