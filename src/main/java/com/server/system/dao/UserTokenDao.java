package com.server.system.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.server.system.model.UserToken;

/**
 * userToken
 * 
 * @author lixiaodong
 *
 */
@Repository("userTokenDao")
public interface UserTokenDao extends JpaRepository<UserToken, Long>, JpaSpecificationExecutor<UserToken> {

	UserToken findByUserIdAndCreateTime(Long userId, long createTime);

	@Modifying
	@Transactional
	void deleteByUserIdAndCreateTime(Long userId, long createTime);

	@Modifying
	@Transactional
	void deleteByUserId(Long userId);

}
