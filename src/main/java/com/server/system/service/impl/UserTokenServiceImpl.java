package com.server.system.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.server.system.dao.UserTokenDao;
import com.server.system.model.UserToken;
import com.server.system.service.UserTokenService;

@Service("userTokenService")
public class UserTokenServiceImpl implements UserTokenService {

	@Resource
	private UserTokenDao userTokenDao;

	@Override
	public void save(UserToken userToken) {
		userTokenDao.save(userToken);
	}

	@Override
	public UserToken findByUserIdAndCreateTime(Long userId, long createTime) {
		return userTokenDao.findByUserIdAndCreateTime(userId, createTime);
	}

	@Override
	public void deleteByUserIdAndCreateTime(Long userId, long createTime) {
		userTokenDao.deleteByUserIdAndCreateTime(userId, createTime);
	}

	@Override
	public void deleteByUserId(Long userId) {
		userTokenDao.deleteByUserId(userId);
	}

}
