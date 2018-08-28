package com.server.system.util;

import java.util.List;

/**
 * String 工具类
 * 
 * @Title StringUtils.java 
 * @description TODO 
 * @time 2017年10月30日 上午11:04:40 
 * @author lixiaodong 
 * @version 1.0
 */
public class StringUtils{
	
	/**
	 * 验证为空
	 * @param str
	 * @return boolean
	 * @time 2017年10月19日 上午11:55:45 
	 * @author lixiaodong
	 */
	public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
	
	/**
	 * 验证是非空
	 * @param str
	 * @return boolean
	 * @time 2017年10月19日 上午10:22:17 
	 * @author lixiaodong
	 */
	public static boolean isNotEmpty(String str) {
		return str != null && !"".equals(str.trim());
	}
	
	/**
	 * 去掉list中的空值
	 * @param list void
	 * @time 2017年10月19日 上午10:22:40 
	 * @author lixiaodong
	 */
	public static List<String> removeEmptyValue(List<String> list){
		
		if(list==null||list.size()==0){
			return list;
		}
		
		for (int i = 0; i < list.size(); i++) {
			// 去掉空值
			if (isEmpty(list.get(i))) {
				list.remove(i);
			}
		}
		
		return list;
	}
}
