/**
 * kevin 2015年7月19日
 */
package com.drive.cool.prop.util.property;

import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.collections.MapUtils;

import com.drive.cool.prop.util.StringUtils;

/**
 * @author kevin
 *
 */
public class UIExtProperties implements ExProperties {

	private static final String ALL_DISPLAYS = "displays";
	private static final String ALL_FIELDS = "fields";
	private static final String ALL_REQUIRED_FIELDS = "requiredFields";
	private static final String ALL_READONLY_FIELDS = "readOnlyFields";
	private static final String ALL_REQUIRED_EDITORS = "requiredEditors";
	private static final String ALL_DICTS = "dicts";
	private static final String ALL_USERS = "users";
	private static final String ALL_BRANCHS = "branches";
	public static final String FIELD_TYPE_DICT = "dict";
	public static final String FIELD_TYPE_USER = "user";
	public static final String FIELD_TYPE_BRANCH = "branch";
	/* (non-Javadoc)
	 * @see com.drive.cool.prop.util.property.ExProperties#setExtProp(java.util.Map, java.lang.String, java.lang.String)
	 */
	@Override
	public void setExtProp(Map resource, String propName, String section) {
		UIProperties prop = new UIProperties(resource);
		prop.setExtProp();
		removeOrignalProp(resource);
	}
	
	/**
	 * 移除原始属性，包括：<br>
	 * fields/displays
	 * @param resource
	 */
	private void removeOrignalProp(Map resource){
		resource.remove(ALL_FIELDS);
		resource.remove(ALL_DISPLAYS);
		resource.remove(ALL_REQUIRED_FIELDS);
		resource.remove(ALL_REQUIRED_EDITORS);
	}

	private class UIProperties{
		
		private String fields;
		private String displays;
		private Map resource;
		public UIProperties(Map resource) {
			init(resource);
		}

		/**
		 * @param resource
		 */
		private void init(Map resource) {
			this.resource = resource;
			fields = MapUtils.getString(resource, ALL_FIELDS,"");
			displays = MapUtils.getString(resource, ALL_DISPLAYS);
		}
		
		public void setExtProp(){
			if(null == fields || null == displays) return;
			setFieldDict();
			setFieldUser();
			setFieldBranch();
			setFieldTitle();
			setGridTitles();
			setFieldRequired();
			setReadOnly();
		}
		
		/**
		 * 根据fileds和displays生成title
		 * @param resource
		 */
		private void setFieldTitle(){
			String[] filedTitle = displays.split(",");
			StringTokenizer tk = new StringTokenizer(fields, ",", false);
			int i = 0;
			while (tk.hasMoreTokens()){
				String field = tk.nextToken();
				String key = field + ".display";
				String title = MapUtils.getString(resource,key);
				if (null == title) {
					if(i < filedTitle.length) {
						title = filedTitle[i];
					}else{
						title = field;
					}
					resource.put(key, title);
				}
				key = field + ".type";
				String type = MapUtils.getString(resource, key);
				if(FIELD_TYPE_DICT.equals(type) 
						|| FIELD_TYPE_USER.equals(type)
						|| FIELD_TYPE_BRANCH.equals(type)){
					resource.put(field + "_.display", title);
				}
				key = field + ".name";
				resource.put(key, field);
				i = i + 1;
			}
		}
		
		/**
		 * 设置grid列名称<br>
		 */
		private void setGridTitles() {
			String gridHeaders = MapUtils.getString(resource, "gridHeaders");
			if (StringUtils.isNotEmpty(gridHeaders) && !resource.containsKey("gridTitles")) {
				StringBuffer gridTitles = new StringBuffer();
				StringTokenizer tk = new StringTokenizer(gridHeaders, ",", false);
				int i = 0;
				while (tk.hasMoreTokens()) {
					String field = tk.nextToken();
					String title = MapUtils.getString(resource, field + ".display");
					gridTitles.append(title).append(",");
				}
				resource.put("gridTitles",gridTitles.substring(0, gridTitles.length() - 1));
			}
		}
		
		/**
		 * 设置数据字典相关属性
		 */
		private void setFieldDict(){
			String dicts = MapUtils.getString(resource, ALL_DICTS);
			if(null == dicts) return;
			for(String oneDict : dicts.split(",")){
				if(StringUtils.isEmpty(oneDict)) continue;
				String[] dictInfo = oneDict.split(":");
				if(dictInfo.length < 2) continue;
				String field = dictInfo[0];
				String dictId = dictInfo[1];
				resource.put(field+".dict", dictId);
				if(!resource.containsKey(field + ".type")){
					resource.put(field + ".type", FIELD_TYPE_DICT);
				}
			}
		}
		/**
		 * 设置用户相关属性
		 */
		private void setFieldUser(){
			String users = MapUtils.getString(resource, ALL_USERS);
			if(null == users) return;
			for(String oneUser : users.split(",")){
				if(StringUtils.isEmpty(oneUser)) continue;
				String userType = oneUser+".type";
				if(!resource.containsKey(userType)) resource.put(userType, FIELD_TYPE_USER);
			}
		}
		/**
		 * 设置必填属性
		 */
		private void setFieldRequired(){
			String fields = MapUtils.getString(resource, ALL_REQUIRED_FIELDS);
			if(null != fields){
				for(String field : fields.split(",")){
					if(StringUtils.isEmpty(field)) continue;
					resource.put(field + ".required", "true");
				}
			}
			fields = MapUtils.getString(resource, ALL_REQUIRED_EDITORS);
			if(null != fields) {
				for(String field : fields.split(",")){
					if(StringUtils.isEmpty(field)) continue;
					resource.put(field + ".editorRequired", "true");
				}
			}
		}
		private void setReadOnly(){
			String fields = MapUtils.getString(resource, ALL_READONLY_FIELDS);
			if(null != fields){
				for(String field : fields.split(",")){
					if(StringUtils.isEmpty(field)) continue;
					resource.put(field + ".readOnly", "true");
				}
			}
		}
		/**
		 * 设置营业部相关属性
		 */
		private void setFieldBranch(){
			String branches = MapUtils.getString(resource, ALL_BRANCHS);
			if(null == branches) return;
			for(String oneBranch : branches.split(",")){
				if(StringUtils.isEmpty(oneBranch)) continue;
				String branchType = oneBranch+".type";
				if(!resource.containsKey(branchType)) resource.put(branchType, FIELD_TYPE_BRANCH);
			}
		}
	}
}
