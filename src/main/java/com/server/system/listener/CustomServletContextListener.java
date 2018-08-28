package com.server.system.listener;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.server.system.model.User;
import com.server.system.service.RoleService;
import com.server.system.service.UserService;
import com.server.system.util.StaticValues;

/**
 * 自定义ServletContextListener监听
 * 
 * @author lixiaodong
 *
 */
@WebListener
public class CustomServletContextListener implements ServletContextListener {

	@Resource
	private UserService userService;

	@Resource
	private RoleService roleService;

	@Override
	public void contextDestroyed(ServletContextEvent servletContext) {

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContext) {
		try {
			/**
			 * 加载基础数据（没有的话加载）
			 */
			roleService.initRole();

			User user = new User();
			user.setUsername(StaticValues.ADMIN_USERNAME);
			if (userService.checkUser(user)) {
				user.setPassword(new BCryptPasswordEncoder().encode(StaticValues.ADMIN_PASSWORD));
				user.setRoleName(StaticValues.ADMIN_ROLE_NAME);
				user.setRoleCode(1L);
				user.setName("Admin");
				user.setState(0);
				userService.edit(user);
			}
			userService.updateUserLoginState();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
