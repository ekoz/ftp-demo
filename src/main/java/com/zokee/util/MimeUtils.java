/*
 * Power by www.xiaoi.com
 */
package com.zokee.util;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
/**
 * @author eko.zhan
 * @date Nov 14, 2014 2:35:56 PM
 * @version 1.0
 */
public class MimeUtils {
	private static PropertiesConfiguration config;
	static{
		try {
			config = new PropertiesConfiguration("mime.properties");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 如果没有找到配置则返回空字符串
	 * @param key
	 * @return
	 */
	public static String getString(String key){
		if (config.getString(key)==null){
			return "";
		}
		return config.getString(key);
	}
	
	/**
	 * ,分隔获取为List<String>
	 * @param key
	 * @return
	 */
	public static List<String> getList(String key){
		if (config.getString(key)==null){
			return null;
		}
		return config.getList(key);
	}
}
