package com.server.system.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 调试信息输出工具类
 * 
 * @author LILJ
 * 
 */
public class LoggerUtil {
	
	private final static Logger logger = LoggerFactory
			.getLogger(LoggerUtil.class);
	

	
	public final static boolean isDebugEnabled(){
		return logger.isDebugEnabled();
	}
	
	public final static void debug(String message, Throwable e){
		logger.debug(message, e);
	}
	
	public final static void debug(String message){
		logger.debug(message);
	}

	public final static void info(String message){
		logger.info(message);
	}
	
	public final static void warn(String message, Throwable e){
		logger.warn(message, e);
	}
	
	public final static void warn(String message){
		logger.warn(message);
	}
	
	public final static void error(String mssage, Throwable e){
		logger.error(mssage, e);
	}

	public final static void error(String mssage){
		logger.error(mssage);
	}
	
}
