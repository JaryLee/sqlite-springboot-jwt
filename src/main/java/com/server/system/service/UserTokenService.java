package com.server.system.service;

import com.server.system.model.UserToken;

public interface UserTokenService {

	void save(UserToken userToken);
	
	UserToken findByUserIdAndCreateTime(Long userId, long createTime);

	void deleteByUserIdAndCreateTime(Long userId, long createTime);

	void deleteByUserId(Long id);

}
