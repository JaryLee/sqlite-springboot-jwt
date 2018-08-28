package com.server.system.secrity.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import net.sf.json.JSONObject;

/**
 * token认证失败 处理
 * 
 * @author lixiaodong
 *
 */
public class AuthEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/json;charset=utf-8");
		JSONObject json = new JSONObject();
		json.put("error_code", 10001);
		json.put("message", "token 认证失败： "+authException.getMessage());
		response.getWriter().print(json.toString());
		response.getWriter().flush();
	}

}
