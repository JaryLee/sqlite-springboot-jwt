package com.server.system.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.server.system.model.User;
import com.server.system.model.UserToken;
import com.server.system.secrity.jwt.JwtConfig;
import com.server.system.secrity.jwt.JwtUtil;
import com.server.system.service.UserService;
import com.server.system.service.UserTokenService;
import com.server.system.util.LoggerUtil;
import com.server.system.util.SecurityUtil;

@RequestMapping("/")
@Controller
public class LoginController {

	@Resource
	private UserService userService;

	@Resource
	private UserTokenService userTokenService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private JwtConfig jwtConfig;

	@RequestMapping(value = "/login", produces = "application/json;charset=UTF-8")
	public @ResponseBody Map<String, Object> login(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();

		if (!SecurityUtil.isLogin()) {
			result.put("error_code", 1001);
			result.put("message", "登录失败！");
			return result;
		}

		try {
			User user = SecurityUtil.getFontUserInfo();
			// 已登录
			user.setState(1);
			user.setLastLoginTime(System.currentTimeMillis());
			user.setLoginCount((user.getLoginCount() == null ? 0 : user.getLoginCount()) + 1);
			userService.edit(user);

			String token = jwtUtil.generateAccessToken(user);
			long tokenCreateTime = jwtUtil.getCreatedDateFromToken(token).getTime();
			long expirTime = jwtUtil.getExpirationDateFromToken(token).getTime();

			// 存储 user token
			UserToken userToken = new UserToken();
			userToken.setUserId(user.getId());
			userToken.setCreateTime(tokenCreateTime);
			userToken.setToken(jwtConfig.getTokenHead() + token);
			userToken.setExpirTime(expirTime);
			userTokenService.save(userToken);

			result.put("error_code", 0);
			result.put("access_token", jwtConfig.getTokenHead() + token);
			result.put("message", "登录成功！");
		} catch (Exception e) {
			LoggerUtil.error(e.getMessage(), e);
			result.put("error_code", 1001);
			result.put("message", "登录失败！");
		}
		return result;
	}

	/**
	 * 退出
	 *
	 * @param request
	 * @return
	 * @time 2018年7月3日 下午3:39:15
	 * @author lixiaodong
	 */
	@RequestMapping(value = "/user/logout", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> logout(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();

		if (!SecurityUtil.isLogin()) {
			result.put("error_code", 1002);
			result.put("message", "注销失败!用户未登录");
			return result;
		}

		try {
			String token = request.getHeader(jwtConfig.getJwtHeader()).substring(jwtConfig.getTokenHead().length());
			userTokenService.deleteByUserIdAndCreateTime(Long.parseLong(jwtUtil.getUserIdFromToken(token)),
					jwtUtil.getCreatedDateFromToken(token).getTime());

			User user = SecurityUtil.getFontUserInfo();
			user.setState(0);
			userService.edit(user);
			result.put("error_code", 0);
			result.put("message", "注销成功!");
		} catch (Exception e) {
			LoggerUtil.error(e.getMessage(), e);
			result.put("error_code", 500);
			result.put("message", "注销失败!");
		}

		return result;
	}
}
