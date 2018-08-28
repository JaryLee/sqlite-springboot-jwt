package com.server.system.secrity.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

import com.server.system.util.LoggerUtil;

import net.sf.json.JSONObject;

public class LogoutSuccessHandler
		implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		JSONObject json = new JSONObject();
		try {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/json;charset=utf-8");
			json.put("error_code", 0);
			json.put("message", "退出成功");

		} catch (Exception e) {
			LoggerUtil.error(e.getMessage(), e);
			json.put("error_code", 500);
			json.put("message", "退出失败");
		}

		response.getWriter().print(json.toString());
		response.getWriter().flush();
	}

}
