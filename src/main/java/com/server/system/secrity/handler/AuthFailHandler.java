package com.server.system.secrity.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import net.sf.json.JSONObject;

/**
 * 登录认证失败 处理
 * 
 * @author lixiaodong
 *
 */
public class AuthFailHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/json;charset=utf-8");
			JSONObject json = new JSONObject();
			json.put("error_code", 10001);
			json.put("message", exception.getMessage());
			response.getWriter().print(json.toString());
			response.getWriter().flush();
	}

	public static boolean isAjaxRequest(HttpServletRequest request) {
		String ajaxFlag = request.getHeader("X-Requested-With");
		return ajaxFlag != null && "XMLHttpRequest".equals(ajaxFlag);
	}
}
