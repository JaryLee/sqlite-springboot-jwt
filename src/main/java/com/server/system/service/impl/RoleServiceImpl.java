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

import com.server.system.dao.RoleDao;
import com.server.system.model.Role;
import com.server.system.service.RoleService;
import com.server.system.util.StaticValues;
import com.server.system.util.StringUtils;

@Service("roleService")
public class RoleServiceImpl implements RoleService {

	@Resource
	private RoleDao roleDao;

	@Override
	public Page<Role> findPage(Role role, Integer pageNo, Integer pageSize, Order... orders) {
		Sort sort = null;
		if (orders != null && orders.length > 0) {
			sort = Sort.by(orders);
		} else {
			sort = Sort.by(new Order[] { new Order(Direction.DESC, "id") });
		}
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return roleDao.findAll(getWhereClause(role), pageable);
	}

	private Specification<Role> getWhereClause(final Role role) {
		return new Specification<Role>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				if (role == null) {
					return null;
				}
				List<Predicate> list = new ArrayList<Predicate>();
				if (StringUtils.isNotEmpty(role.getName())) {
					list.add(cb.like(root.get("name").as(String.class), "%" + role.getName().trim() + "%"));
				}
				if (role.getState() != null) {
					list.add(cb.equal(root.get("state"), role.getState()));
				}
				if (list.size() == 0) {
					return null;
				} else {
					Predicate[] p = new Predicate[list.size()];
					return cb.or(list.toArray(p));
				}

			}
		};
	}

	@Override
	public boolean delete(Long id) {
		boolean b = true;
		try {
			roleDao.deleteById(id);
			return true;
		} catch (Exception e) {
			b = false;
			e.printStackTrace();
		}
		return b;
	}

	@Override
	public Role findById(Long id) {
		Optional<Role> optional = roleDao.findById(id);

		if (!optional.isPresent()) {
			return null;
		}
		return optional.get();
	}

	@Override
	public boolean edit(Role role) {
		boolean b = true;
		try {
			if (role.getId() == null) {
				roleDao.save(role);
			} else {
				Role oldRole = findById(role.getId());
				if (oldRole == null) {
					return false;
				}

				if (StringUtils.isNotEmpty(role.getName())) {
					oldRole.setName(role.getName());
				}

				roleDao.save(oldRole);
			}
		} catch (Exception e) {
			b = false;
			e.printStackTrace();
		}
		return b;
	}

	@Override
	public List<Role> findAll(Role role) {
		if (role == null) {
			roleDao.findAll();
		}
		return roleDao.findAll(getWhereClause(role));
	}

	@Override
	public boolean checkRole(Role role) {
		if (role == null || StringUtils.isEmpty(role.getName())) {
			return false;
		}
		Role newRole = roleDao.findByName(role.getName());
		if (newRole == null) {
			return true;
		}
		if (role.getId() != null && newRole.getId().equals(role.getId())) {
			return true;
		}
		return false;
	}

	@Override
	public Role save(Role admin) {
		return roleDao.save(admin);
	}

	@Override
	public void initRole() {
		List<Role> list = roleDao.findAll();
		if (list != null && list.size() > 0) {
			return;
		}
		list = new ArrayList<Role>();
		buildRoleList(list);
		roleDao.saveAll(list);
	}

	private void buildRoleList(List<Role> list) {
		Role role = new Role();
		role.setName(StaticValues.ADMIN_ROLE_NAME);
		role.setState(0);
		list.add(role);
		role = new Role();
		role.setName(StaticValues.USER_ROLE_NAME);
		role.setState(0);
		list.add(role);
	}
}
