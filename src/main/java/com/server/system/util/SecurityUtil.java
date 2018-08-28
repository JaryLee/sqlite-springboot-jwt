package com.server.system.util;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.server.system.model.User;

public class SecurityUtil {

	public static SecurityContext getContext() {
		return SecurityContextHolder.getContext();
	}

	/**
	 * 判断是否登录
	 * 
	 * @return
	 */
	public static Boolean isLogin() {
		return getContext().getAuthentication() != null && getContext().getAuthentication().isAuthenticated()
				&& !getContext().getAuthentication().getName().equals("anonymousUser");
	}

	/**
	 * 获取前台用户信息
	 * 
	 * @return
	 */
	public static User getFontUserInfo() {
		User user = (User) getContext().getAuthentication().getPrincipal();
		return user;
	}

}
