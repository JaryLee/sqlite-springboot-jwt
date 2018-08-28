package com.server.system.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Order;

import com.server.system.model.Role;

/**
 * 角色管理Service
 * 
 * @author lixiaodong
 *
 */
public interface RoleService {
	
	/**
	 * 分页查询
	 *
	 * @param role
	 * @param pageNo
	 * @param pageSize
	 * @param orders
	 * @return
	 * @time 2018年7月3日 下午3:19:53
	 * @author lixiaodong
	 */
	public Page<Role> findPage(Role role, Integer pageNo, Integer pageSize, Order ... orders);
	
	/**
	 * 根据ID删除
	 *
	 * @param id
	 * @return
	 * @time 2018年7月3日 下午4:07:02
	 * @author lixiaodong
	 */
	public boolean delete(Long id);
	
	/**
	 * 根据ID查询
	 *
	 * @param id
	 * @return
	 * @time 2018年7月3日 下午4:06:54
	 * @author lixiaodong
	 */
	public Role findById(Long id);
	
	/**
	 * 编辑角色
	 *
	 * @param role
	 * @return
	 * @time 2018年7月3日 下午4:06:48
	 * @author lixiaodong
	 */
	public boolean edit(Role role);
	
	
	/**
	 * 查询全部
	 *
	 * @param role
	 * @return
	 * @time 2018年7月3日 下午4:06:28
	 * @author lixiaodong
	 */
	public List<Role> findAll(Role role);
	
	
	/**
	 * 验证role是否存在
	 *
	 * @param role
	 * @return
	 * @time 2018年7月3日 下午4:06:36
	 * @author lixiaodong
	 */
	public boolean checkRole(Role role);
	
	/**
	 * 保存
	 *
	 * @param admin
	 * @return
	 * @time 2018年7月3日 下午4:06:42
	 * @author lixiaodong
	 */
	public Role save(Role admin);
	
	/**
	 * 初始化role
	 *
	 * @time 2018年7月6日 上午11:06:58
	 * @author lixiaodong
	 */
	public void initRole();
	
}
