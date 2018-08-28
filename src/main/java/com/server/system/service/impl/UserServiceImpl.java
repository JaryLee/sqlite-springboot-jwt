package com.server.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.server.system.dao.UserDao;
import com.server.system.model.User;
import com.server.system.service.UserService;
import com.server.system.util.LoggerUtil;
import com.server.system.util.StringUtils;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Resource
	private UserDao userDao;

	@Override
	public void updateUserLoginState() {
		userDao.updateUserLoginState();
	}

	@Override
	public Page<User> findPage(User user, Integer pageNo, Integer pageSize, Order... orders) {
		Sort sort = null;
		if (orders != null && orders.length > 0) {
			sort = Sort.by(orders);
		} else {
			sort = Sort.by(new Order[] { new Order(Direction.DESC, "id") });
		}
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return userDao.findAll(getWhereClause(user), pageable);
	}

	private Specification<User> getWhereClause(final User user) {
		return new Specification<User>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				if (user == null) {
					return null;
				}
				List<Predicate> list = new ArrayList<Predicate>();
				if (StringUtils.isNotEmpty(user.getUsername())) {
					list.add(cb.like(root.get("username").as(String.class), "%" + user.getUsername().trim() + "%"));
				}
				if (user.getFlag() != null) {
					list.add(cb.equal(root.get("flag"), user.getFlag()));
				}
				if (user.getRoleCode() != null) {
					list.add(cb.equal(root.get("roleCode"), user.getRoleCode()));
				}
				if (list.size() == 0) {
					return null;
				} else {
					Predicate[] p = new Predicate[list.size()];
					return cb.and(list.toArray(p));
				}

			}
		};
	}

	@Override
	public User findById(Long id) {
		Optional<User> optional = userDao.findById(id);

		if (!optional.isPresent()) {
			return null;
		}
		return optional.get();
	}

	@Override
	public boolean edit(User user) {
		boolean b = true;
		Long id = user.getId();
		try {
			if (id == null) {
				userDao.save(user);
			} else {
				User oldUser = findById(id);
				if (StringUtils.isNotEmpty(user.getUsername())) {
					oldUser.setUsername(user.getUsername());
				}

				if (StringUtils.isNotEmpty(user.getPassword())) {
					oldUser.setPassword(user.getPassword());
				}

				oldUser.setEmail(user.getEmail());

				if (StringUtils.isNotEmpty(user.getName())) {
					oldUser.setName(user.getName());
				}

				oldUser.setPhone(user.getPhone());

				if (user.getRoleCode() != null) {
					oldUser.setRoleCode(user.getRoleCode());
				}

				if (user.getState() != null) {
					oldUser.setState(user.getState());
				}

				if (user.getLastLoginTime() != null) {
					oldUser.setLastLoginTime(user.getLastLoginTime());
				}

				if (user.getLoginCount() != null) {
					oldUser.setLoginCount(user.getLoginCount());
				}

				userDao.save(oldUser);
			}
		} catch (Exception e) {
			b = false;
		}
		return b;
	}

	@Override
	public boolean delete(User user) {
		boolean b = true;
		try {
			user.setFlag(1);
			userDao.save(user);
		} catch (Exception e) {
			LoggerUtil.error(e.getMessage(), e);
			b = false;
		}
		return b;
	}

	@Override
	public User login(String username, String password) {
		if (username == null) {
			return null;
		}

		User user = userDao.findByUsername(username);
		if (user == null) {
			return null;
		}
		/*
		 * 判断用户密码是否正确
		 */
	/*	if (PasswordHash.validatePassword(password, user.getPassword())) {
			return user;
		}*/
		return null;
	}

	@Override
	public boolean checkUser(User user) {
		if (user == null || StringUtils.isEmpty(user.getUsername())) {
			return false;
		}
		User newUser = userDao.findByUsername(user.getUsername());
		if (newUser == null) {
			return true;
		}
		if (user.getId() != null && newUser.getId().equals(user.getId())) {
			return true;
		}
		return false;
	}

	@Override
	public List<User> findAll(User user) {
		if (user == null) {
			return userDao.findAll();
		}
		return userDao.findAll(getWhereClause(user));
	}

	@Override
	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}
}
