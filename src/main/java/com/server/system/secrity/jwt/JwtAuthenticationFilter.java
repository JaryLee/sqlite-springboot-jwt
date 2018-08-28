package com.server.system.secrity.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.server.system.model.User;
import com.server.system.model.UserToken;
import com.server.system.secrity.CustomUserDetailsService;
import com.server.system.service.UserTokenService;
import com.server.system.util.LoggerUtil;

import net.sf.json.JSONObject;

/**
 * 过滤请求 获取token
 * 
 * @author lixiaodong
 *
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private UserTokenService userTokenService;

	@Autowired
	private JwtConfig jwtConfig;

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader(jwtConfig.getJwtHeader());
		if (authHeader != null && authHeader.startsWith(jwtConfig.getTokenHead())) {
			final String authToken = authHeader.substring(jwtConfig.getTokenHead().length());
			String username = jwtUtil.getUsernameFromToken(authToken);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				User user = (User) userDetailsService.loadUserByUsername(username);

				if (jwtUtil.validateToken(authToken, user)) {
					validateUserToken(response, authToken, user.getId());
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,
							null, user.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	/**
	 * 验证 user token是否存在
	 *
	 * @param response
	 * @param token
	 * @param userId
	 * @time 2018年7月13日 上午11:07:07
	 * @author lixiaodong
	 */
	private void validateUserToken(HttpServletResponse response, String token, Long userId) {
		try {
			long createTime = jwtUtil.getCreatedDateFromToken(token).getTime();
			UserToken userToken = userTokenService.findByUserIdAndCreateTime(userId, createTime);
			if (userToken == null) {
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("text/json;charset=utf-8");
				JSONObject json = new JSONObject();
				json.put("error_code", 10001);
				json.put("message", "Token 已改变请重新登录");
				response.getWriter().print(json.toString());
				response.getWriter().flush();
			}
		} catch (Exception e) {
			LoggerUtil.error(e.getMessage(), e);
		}

	}
}
