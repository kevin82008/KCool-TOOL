package com.drive.cool.prop.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.drive.cool.prop.util.property.ExProperties;
import com.drive.cool.prop.util.property.PropertyConfigCache;
import com.drive.cool.prop.util.property.UIExtProperties;

public class ResourceUtil {
	/**
	 * 
	 */
	private static final String PROP_BASE_PATH = "prop";

	/**
	 * 
	 */
	private static final String BASE_PATH = "drive-";

	private static final Log log = LogFactory.getLog(ResourceUtil.class);

	private static Map<String, String> FILE_MAP = new HashMap();
	private static Map<String, String> JAR_FILE_MAP = new HashMap();
	private static Map<String, PropertyConfigCache> RESOURCE_MAP = new HashMap<String, PropertyConfigCache>();

	protected static ExProperties exProp = new UIExtProperties();
	
	static {
		initFileName();
		initJarFileName();
	}

	private static void initFileName(String basePath){
		URL url = ResourceUtil.class.getResource("/");
		String path = null;
		if(null != url && "file".equals(url.getProtocol())){
			//做兼容test处理
			path = url.getPath();
			int index = path.indexOf("test-classes");
			if(index > 0){
				path = path.substring(0,index) + "classes" + path.substring(index + 12);
			}
		}else if(null == url){
			path = System.getProperty("user.dir") + File.separator;
		}
			
		path = path + basePath;
		File directory = new File(path);
		
		if(directory.isDirectory()){
			Iterator it = FileUtils.iterateFiles(directory,new String[] { "properties" }, true);
			while (it.hasNext()) {
				File file = (File) it.next();
				log.debug("load prop:" + file.getName());
				FILE_MAP.put(file.getName(), file.getAbsolutePath());
			}
		}
	}
	private static void initFileName() {
		initFileName(PROP_BASE_PATH);
	}

	private static void initJarFileName(){
		URL url = ResourceUtil.class.getResource("/");
		if(null == url) return;
		String rootPath = url.getPath();
		String baseClassPath = "/classes";
		
		int index = rootPath.indexOf(baseClassPath);
		if(index < 0){
			String baseTestClassPath = "/classes";
			index = rootPath.indexOf(baseTestClassPath);
		}
		if(index < 0){
			rootPath = rootPath + File.separator + "lib" + File.separator;
		}else{
			rootPath = rootPath.substring(0,index) + File.separator + "lib" + File.separator;
		}
		log.debug("load jar prop,rooPath:" + rootPath);
		if(null != rootPath && new File(rootPath).exists() ){
			Iterator it = FileUtils.iterateFiles(new File(rootPath),new String[] { "jar" }, true);
			while (it.hasNext()) {
				File file = (File) it.next();
				String path = file.getAbsolutePath();
				
				if(!(path.indexOf(BASE_PATH)>0)){
					log.debug("ignore jar:" + path);
					continue;
				}
				log.debug("loaded jar:" + path);
				JarFile jar;  
                try {  
                    jar = new JarFile(file); 
                    Enumeration<JarEntry> entries = jar.entries();  
                    //查找jar里的prop
                    while (entries.hasMoreElements()) {  
                        JarEntry entry = entries.nextElement();
                        
                        String name = entry.getName();
                        //只获取prop里的文件
                        if(name.startsWith(PROP_BASE_PATH) && name.endsWith(".properties")){
                        	log.debug("load jar prop:" + name);
                        	JAR_FILE_MAP.put(name.substring(name.lastIndexOf("/")+1), "/"+name);
                        }
                        
                    }
                }catch(IOException e){
                	log.error("打开jar文件失败：" + path);
                }
			}
		}
	}
		
	public synchronized static Map getResource(String propName) {
		return getResource(propName, "");
	}

	public synchronized static Map getResource(String propName, String section) {
		return getResourceCache(propName, section).getResourceMap();
	}

	public synchronized static Map getEditableResource(String propName, String section) {
		return getResourceCache(propName, section).getEditableResourceMap();
	}

	private static PropertyConfigCache getResourceCache(String propName, String section) {
		String key = propName + "$" + section;
		PropertyConfigCache cache = RESOURCE_MAP.get(key);

		try {
			if (null == cache) {
				String filePath,jarFilePath;
				filePath = FILE_MAP.get(propName + ".properties");
				//不重启，不更新jar里的prop文件
				jarFilePath = JAR_FILE_MAP.get(propName+".properties"); 
				if (null == filePath && null == jarFilePath) {
					initFileName();
					filePath = FILE_MAP.get(propName + ".properties");
				}
				if(null != filePath){
					cache = new PropertyConfigCache(filePath, propName, section,"file",exProp);
				}else if(null != jarFilePath){
					cache = new PropertyConfigCache(jarFilePath, propName, section,"jarfile",exProp);
				}
				RESOURCE_MAP.put(key, cache);
			}
		} catch (Exception e) {
			log.error("Cannot get Resource:" + propName + ".properties");
		}
		return cache;
	}
	
}
