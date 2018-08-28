package com.server.system.secrity.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class JwtConfig {

	@Value("${jwt.header}")
	private String jwtHeader;//token 的 httpheader key
	
	@Value("${jwt.expiration}")
	private Long expiration; //token 过期时间 （毫秒）
	
	@Value("${jwt.refresh_expiration}")
	private Long refreshExpiration;//refresh token 过期时间 （毫秒）
	
	@Value("${jwt.token.head}")
	private String tokenHead;//token 的头部分
	
	@Value("${jwt.secret}")
	private String secret;//token 密钥

	public String getJwtHeader() {
		return jwtHeader;
	}

	public void setJwtHeader(String jwtHeader) {
		this.jwtHeader = jwtHeader;
	}

	public Long getExpiration() {
		return expiration * 1000;
	}

	public void setExpiration(Long expiration) {
		this.expiration = expiration;
	}

	public Long getRefreshExpiration() {
		return refreshExpiration;
	}

	public void setRefreshExpiration(Long refreshExpiration) {
		this.refreshExpiration = refreshExpiration;
	}

	public String getTokenHead() {
		return tokenHead;
	}

	public void setTokenHead(String tokenHead) {
		this.tokenHead = tokenHead;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}
