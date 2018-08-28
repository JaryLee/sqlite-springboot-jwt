package com.server.system.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.server.system.model.User;

/**
 * 用户管理Dao
 *
 * @Title
 * @TODO TODO
 * @author lixiaodong
 * @DATE 2018年3月20日 上午10:04:22
 * @Version v1.0
 *
 */
@Repository("userDao")
public interface UserDao extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

	/**
	 * 根据用户名查询
	 * 
	 * @Title: findByUsername
	 * @Description: TODO
	 * @throws @author
	 *             lixiaodong
	 * @Version v1.0
	 * @DATE 2018年3月20日 上午10:04:27
	 */
	public User findByUsername(String username);

	@Modifying
	@Transactional
	@Query("update User set state = 0")
	public void updateUserLoginState();

}
