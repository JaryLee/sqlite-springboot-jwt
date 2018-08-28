package com.server.system.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.server.system.model.Role;

/**
 * 角色管理Dao
 *
 * @Title
 * @TODO TODO
 * @author lixiaodong
 * @DATE 2018年3月20日 上午10:04:43
 * @Version v1.0
 *
 */
@Repository("roleDao")
public interface RoleDao extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
	
	/**
	 * 根据角色名查询
	 *
	 * @param name
	 * @return
	 * @time 2018年7月4日 上午10:43:52
	 * @author lixiaodong
	 */
	public Role findByName(String name);

}
