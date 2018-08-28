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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.server.system.model.Role;
import com.server.system.model.User;
import com.server.system.service.RoleService;
import com.server.system.service.UserService;
import com.server.system.util.LoggerUtil;
import com.server.system.util.StringUtils;

/**
 * 用户组
 * 
 * @author lixiaodong
 *
 */
@Controller
@RequestMapping("/group")
public class RoleControlller {

	@Resource
	private RoleService roleService;
	
	@Resource
	private UserService userService;
	
	/**
	 * 查询全部角色
	 *
	 * @param request
	 * @return
	 * @time 2018年7月4日 下午12:04:17
	 * @author lixiaodong
	 */
	@RequestMapping(value = "/search/all", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> searchAll(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			List<Role> list = roleService.findAll(null);
			result.put("error_code", 0);
			result.put("data", list);
			result.put("message", "查询成功");
		} catch (Exception e) {
			LoggerUtil.error(e.getMessage(), e);
			result.put("error_code", 1001);
			result.put("message", "查询失败");
		}
		return result;
	}

	/**
	 * 角色信息分页查询
	 *
	 * @param request
	 * @param roleName
	 * @param pageNo
	 * @param pageSize
	 * @param sort
	 * @param sortField
	 * @return
	 * @time 2018年7月3日 下午3:47:10
	 * @author lixiaodong
	 */
	@RequestMapping(value = "/search/page", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> searchPage(HttpServletRequest request,
			@RequestParam(value = "roleName", required = false) String roleName,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
			@RequestParam(value = "sort", defaultValue = "desc") String sort,
			@RequestParam(value = "sortField", defaultValue = "id") String sortField) {
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

		Role role = new Role();
		role.setName(roleName);

		Page<Role> page = roleService.findPage(role, pageNo, pageSize, orders);

		if (page == null) {
			result.put("data", new ArrayList<Role>());
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
	 * 角色删除
	 *
	 * @param request
	 * @param id
	 * @return
	 * @time 2018年7月3日 下午3:53:53
	 * @author lixiaodong
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Map<String, Object> del(HttpServletRequest request, @PathVariable Long id) {
		Map<String, Object> result = new HashMap<String, Object>();
		User user = new User();
		Role role = new Role();
		role.setId(id);

		List<User> userList = userService.findAll(user);

		if (userList != null && userList.size() > 0) {
			result.put("error_code", 500);
			result.put("message", "删除失败！有引用的用户！");
			return result;
		}

		try {
			roleService.delete(id);
			result.put("error_code", 0);
			result.put("message", "删除成功！");
		} catch (Exception e) {
			result.put("error_code", 500);
			result.put("message", "删除失败！" + e.getMessage());
		}
		return result;
	}

	/**
	 * 修改角色
	 *
	 * @param request
	 * @param newRole
	 * @return
	 * @time 2018年7月3日 下午3:54:09
	 * @author lixiaodong
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody Map<String, Object> edit(HttpServletRequest request, @RequestBody Role role) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (role == null) {
			result.put("error_code", 1003);
			result.put("message", "没有新增或修改对象！");
			return result;
		}
		if (StringUtils.isEmpty(role.getName())) {
			result.put("error_code", 1003);
			result.put("message", "角色名称或code为空！");
			return result;
		}

		boolean b = roleService.checkRole(role);
		if (!b) {
			result.put("error_code", 1001);
			result.put("message", "添加角色已存在！");
			return result;
		}

		if (!roleService.edit(role)) {
			result.put("error_code", 1001);
			result.put("message", "角色编辑失败！");
		} else {
			result.put("error_code", 0);
			result.put("message", "角色编辑成功！");
		}

		return result;
	}

	/**
	 * 根据ID查询角色
	 *
	 * @param request
	 * @param id
	 * @return
	 * @time 2018年7月3日 下午4:04:23
	 * @author lixiaodong
	 */
	@RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> findById(HttpServletRequest request, @PathVariable Long id) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (id == null) {
			result.put("error_code", 1003);
			result.put("message", "参数错误");
			return result;
		}

		try {
			Role role = roleService.findById(id);
			result.put("error_code", 0);
			result.put("data", role);
			result.put("message", "查询成功");
		} catch (Exception e) {
			LoggerUtil.error(e.getMessage(), e);
			result.put("error_code", 1001);
			result.put("message", "查询失败");
		}
		return result;
	}
}
