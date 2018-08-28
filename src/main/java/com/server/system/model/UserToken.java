package com.server.system.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * user token表
 * 
 * @author lixiaodong
 *
 */
@Entity
@Table(name = "user_token")
public class UserToken implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@NotNull
	@Column(name = "user_id")
	private Long userId;

	@NotNull
	@Column(name = "token",length=500)
	private String token;

	@NotNull
	@Column(name = "create_time")
	private Long createTime;

	@NotNull
	@Column(name = "expir_time")
	private Long expirTime;

	public Long getExpirTime() {
		return expirTime;
	}

	public void setExpirTime(Long expirTime) {
		this.expirTime = expirTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

}
