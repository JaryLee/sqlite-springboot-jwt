package com.server.system.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Order;

import com.server.system.model.User;

/**
 * 用户管理Service
 * 
 * @author lixiaodong
 *
 */
public interface UserService {

	/**
	 * 所有用户登录状态修改改成未登录
	 *
	 * @time 2018年7月3日 下午4:07:54
	 * @author lixiaodong
	 */
	public void updateUserLoginState();

	/**
	 * 分页查询
	 *
	 * @param user
	 * @param pageNo
	 * @param pageSize
	 * @param orders
	 * @return
	 * @time 2018年7月3日 下午4:07:48
	 * @author lixiaodong
	 */
	public Page<User> findPage(User user, Integer pageNo, Integer pageSize, Order... orders);

	/**
	 * 根据Id查询
	 *
	 * @param id
	 * @return
	 * @time 2018年7月3日 下午4:07:41
	 * @author lixiaodong
	 */
	public User findById(Long id);

	/**
	 * 编辑
	 *
	 * @param user
	 * @return
	 * @time 2018年7月3日 下午4:07:36
	 * @author lixiaodong
	 */
	public boolean edit(User user);

	/**
	 * 根据单个Id 逻辑删除
	 *
	 * @param user
	 * @return
	 * @time 2018年7月3日 下午4:07:29
	 * @author lixiaodong
	 */
	public boolean delete(User user);

	/**
	 * 根据用户名 密码登录
	 *
	 * @param username
	 * @param password
	 * @return
	 * @time 2018年7月3日 下午4:07:24
	 * @author lixiaodong
	 */
	public User login(String username, String password);

	/**
	 * 查看用户是否存在
	 *
	 * @param user
	 * @return
	 * @time 2018年7月3日 下午4:07:17
	 * @author lixiaodong
	 */
	public boolean checkUser(User user);
	
	/**
	 * 查询全部
	 *
	 * @param user
	 * @return
	 * @time 2018年7月3日 下午3:49:21
	 * @author lixiaodong
	 */
	public List<User> findAll(User user);
	
	/**
	 * 根据用户名精确查询用户
	 *
	 * @param username
	 * @return
	 * @time 2018年7月6日 下午4:27:01
	 * @author lixiaodong
	 */
	public User findByUsername(String username);

}
