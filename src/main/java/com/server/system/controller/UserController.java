package com.server.system.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.server.system.model.User;
import com.server.system.service.UserService;
import com.server.system.service.UserTokenService;
import com.server.system.util.LoggerUtil;
import com.server.system.util.SecurityUtil;
import com.server.system.util.StringUtils;

import net.sf.json.JSONObject;

@RequestMapping("/user")
@Controller
public class UserController {

	@Resource
	private UserService userService;

	@Resource
	private UserTokenService userTokenService;

	/**
	 * 分页查询
	 *
	 * @param request
	 * @param username
	 * @param pageNo
	 * @param pageSize
	 * @param sort
	 * @param sortField
	 * @return
	 * @time 2018年7月3日 下午3:36:49
	 * @author lixiaodong
	 */
	@RequestMapping(value = "/search/page", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> searchPage(HttpServletRequest request,
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "sort", defaultValue = "desc") String sort,
			@RequestParam(value = "sortField", defaultValue = "createTime") String sortField) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("error_code", 0);
		result.put("pageNo", pageNo);
		result.put("pageSize", pageSize);
		Order[] orders = null;
		if (StringUtils.isNotEmpty(sort) && StringUtils.isNotEmpty(sortField) && !"0".equals(sortField)) {
			if ("asc".equals(sort)) {
				orders = new Order[] { new Order(Direction.ASC, sortField) };
			} else {
				orders = new Order[] { new Order(Direction.DESC, sortField) };
			}
		}
		User user = new User();
		user.setUsername(username);
		user.setFlag(0);
		Page<User> page = userService.findPage(user, pageNo, pageSize, orders);

		if (page == null) {
			result.put("data", new ArrayList<User>());
			result.put("total", 0);
			result.put("message", "查询数据为空");
			return result;
		}
		result.put("data", page.getContent());
		result.put("total", page.getTotalElements());
		result.put("message", "查询数据成功");
		return result;
	}

	/**
	 * 用户注册
	 *
	 * @param request
	 * @param user
	 * @return
	 * @time 2018年7月13日 上午9:46:10
	 * @author lixiaodong
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody Map<String, Object> register(HttpServletRequest request, @RequestBody User user) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (user == null) {
			result.put("error_code", 1003);
			result.put("message", "参数错误！");
			return result;
		}

		if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getName())
				|| StringUtils.isEmpty(user.getPassword()) || user.getRoleCode() == null) {
			result.put("error_code", 1003);
			result.put("message", "参数错误！");
			return result;
		}
		boolean exists = userService.checkUser(user);
		if (!exists) {
			result.put("error_code", 1001);
			result.put("message", "添加对象已存在！");
			return result;
		}
		user.setCreateTime(System.currentTimeMillis());
		user.setState(0);
		user.setFlag(0);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String password = passwordEncoder.encode(user.getPassword());
		user.setPassword(password);

		if (userService.edit(user)) {
			JSONObject josn = JSONObject.fromObject(user);
			josn.remove("password");
			result.put("error_code", 0);
			result.put("data", josn);
			result.put("message", "注册成功！");
		} else {
			result.put("error_code", 1001);
			result.put("message", "注册失败！");
		}
		return result;
	}

	/**
	 * 用户删除
	 *
	 * @param request
	 * @param id
	 * @return
	 * @time 2018年7月3日 下午3:43:51
	 * @author lixiaodong
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Map<String, Object> delete(HttpServletRequest request, @PathVariable Long id) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			User user = SecurityUtil.getFontUserInfo();
			if (user == null) {
				result.put("error_code", 1002);
				result.put("message", "请重新未登录！");
				return result;
			}
			if (user.getId().equals(id)) {
				result.put("error_code", 1001);
				result.put("message", "不能删除本身账户！");
				return result;
			}
			User targetUser = userService.findById(id);
			if (targetUser == null) {
				result.put("error_code", 1003);
				result.put("message", "账户不存在！");
				return result;
			}
			if (targetUser.getState() != null && targetUser.getState() == 1) {
				result.put("error_code", 1001);
				result.put("message", "不能删除正在登陆的账户！");
				return result;
			}

			// 删除user 下的所有token
			userTokenService.deleteByUserId(user.getId());

			userService.delete(targetUser);// 逻辑删除
			result.put("error_code", 0);
			result.put("message", "删除成功！");
		} catch (Exception e) {
			result.put("error_code", 1001);
			result.put("message", "删除失败！");
		}
		return result;
	}

	/**
	 * 根据 ID 查询用户
	 *
	 * @param request
	 * @param id
	 * @return
	 * @time 2018年7月3日 下午3:42:13
	 * @author lixiaodong
	 */
	@RequestMapping(value = "/find/user/{id}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> findUserById(HttpServletRequest request, @PathVariable Long id) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (id == null) {
			result.put("error_code", 1003);
			result.put("message", "参数错误");
			return result;
		}

		try {
			User user = userService.findById(id);
			result.put("error_code", 0);
			result.put("data", user);
			result.put("message", "查询成功");
		} catch (Exception e) {
			LoggerUtil.error(e.getMessage(), e);
			result.put("error_code", 1001);
			result.put("message", "查询失败");
		}
		return result;
	}

	/**
	 * 用户修改
	 *
	 * @param request
	 * @param newUser
	 * @return
	 * @time 2018年7月3日 下午3:39:42
	 * @author lixiaodong
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody Map<String, Object> edit(HttpServletRequest request, @RequestBody User user) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (user == null) {
			result.put("error_code", 1003);
			result.put("message", "添加或修改对象为空");
			return result;
		}

		if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getName())
				|| user.getRoleCode() == null) {
			result.put("error_code", 1003);
			result.put("message", "参数错误！");
			return result;
		}

		boolean exists = userService.checkUser(user);
		if (!exists) {
			result.put("error_code", 1001);
			result.put("message", "添加对象已存在！");
			return result;
		}
		if (user.getId() == null) {
			user.setCreateTime(System.currentTimeMillis());
			user.setState(0);
			user.setFlag(0);
		}

		if (StringUtils.isNotEmpty(user.getPassword())) {
			user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		}

		if (userService.edit(user)) {
			result.put("error_code", 0);
			result.put("message", "修改成功！");
		} else {
			result.put("error_code", 1001);
			result.put("message", "修改失败！");
		}
		return result;
	}

	/**
	 * 查询全部
	 *
	 * @param request
	 * @param user
	 * @return
	 * @time 2018年6月19日 下午4:00:53
	 * @author lixiaodong
	 */
	@RequestMapping(value = "/find/all", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody Map<String, Object> findAll(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List<User> list = userService.findAll(null);
			result.put("error_code", 0);
			result.put("data", list);
			result.put("message", "查询成功！");
		} catch (Exception e) {
			result.put("error_code", 500);
			result.put("message", "查询失败！");
		}
		return result;
	}

	/**
	 * 修改密码
	 *
	 * @param request
	 * @param id
	 * @param password
	 * @param newpassword
	 * @return
	 * @time 2018年7月3日 下午3:39:25
	 * @author lixiaodong
	 */
	@RequestMapping(value = "/password", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody Map<String, Object> updatePassword(HttpServletRequest request,
			@RequestBody Map<String, String> body) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("error_code", 0);
		try {
			String userId = body.get("id");
			String password = body.get("password");
			String newpassword = body.get("newpassword");
			if (newpassword == null || password == null || userId == null) {
				result.put("error_code", 1003);
				result.put("message", "参数不能为空！");
				return result;
			}

			User user = userService.findById(Long.parseLong(userId));
			if (user == null) {
				result.put("error_code", 1003);
				result.put("message", "没有修改对象！");
				return result;
			}

			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			boolean b = passwordEncoder.matches(password, user.getPassword());

			if (!b) {
				result.put("error_code", 1001);
				result.put("message", "旧密码输入不正确！");
				return result;
			}
			user.setPassword(passwordEncoder.encode(newpassword));
			userService.edit(user);

			// 删除user 下的所有token
			userTokenService.deleteByUserId(user.getId());

			result.put("message", "密码修改成功！");
		} catch (Exception e) {
			result.put("error_code", 1001);
			result.put("message", "密码修改失败！" + e.getMessage());
		}
		return result;
	}

	/**
	 * 获取当前登录用户
	 *
	 * @param request
	 * @return
	 * @time 2018年7月9日 下午4:26:36
	 * @author lixiaodong
	 */
	@RequestMapping(value = "/find/current/login", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody Map<String, Object> findLoginUser(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			User user = SecurityUtil.getFontUserInfo();
			if (user == null) {
				result.put("error_code", 500);
				result.put("message", "当前登录用户不存在！");
			} else {
				result.put("error_code", 0);
				result.put("data", user);
				result.put("message", "查询成功！");
			}
		} catch (Exception e) {
			result.put("error_code", 500);
			result.put("message", "查询失败！");
		}
		return result;
	}
}
