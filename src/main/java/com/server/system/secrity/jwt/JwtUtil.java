package com.server.system.secrity.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.system.model.User;
import com.server.system.util.LoggerUtil;
import com.server.system.util.UuidUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.sf.json.JSONObject;

@Component
public class JwtUtil {

	public static final String ROLE_REFRESH_TOKEN = "ROLE_REFRESH_TOKEN";

	private static final String CLAIM_KEY_USER_ID = "user_id";
	private static final String CLAIM_KEY_AUTHORITIES = "scope";
	private static final String CLAIM_KEY_ACCOUNT_ENABLED = "enabled";
	private static final String CLAIM_KEY_ACCOUNT_NON_LOCKED = "non_locked";
	private static final String CLAIM_KEY_ACCOUNT_NON_EXPIRED = "non_expired";

	private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

	@Autowired
	private JwtConfig jwtConfig;

	/**
	 * 通过token 获取userId
	 *
	 * @param token
	 * @return
	 * @time 2018年7月12日 下午4:39:26
	 * @author lixiaodong
	 */
	public String getUserIdFromToken(String token) {
		String userId;
		try {
			final Claims claims = getClaimsFromToken(token);
			userId = claims.get(CLAIM_KEY_USER_ID).toString();
		} catch (Exception e) {
			userId = "";
		}
		return userId;
	}

	/**
	 * 通过token 获取username
	 *
	 * @param token
	 * @return
	 * @time 2018年7月12日 下午4:39:26
	 * @author lixiaodong
	 */
	public String getUsernameFromToken(String token) {
		String username;
		try {
			final Claims claims = getClaimsFromToken(token);
			username = claims.getSubject();
		} catch (Exception e) {
			username = null;
		}
		return username;
	}

	/**
	 * 获取token生成日期
	 *
	 * @param token
	 * @return
	 * @time 2018年7月12日 下午4:40:13
	 * @author lixiaodong
	 */
	public Date getCreatedDateFromToken(String token) {
		Date created;
		try {
			final Claims claims = getClaimsFromToken(token);
			created = claims.getIssuedAt();
		} catch (Exception e) {
			created = null;
		}
		return created;
	}

	/**
	 * 获取token失效日期
	 *
	 * @param token
	 * @return
	 * @time 2018年7月12日 下午4:41:06
	 * @author lixiaodong
	 */
	public Date getExpirationDateFromToken(String token) {
		Date expiration;
		try {
			final Claims claims = getClaimsFromToken(token);
			expiration = claims.getExpiration();
		} catch (Exception e) {
			expiration = null;
		}
		return expiration;
	}

	/**
	 * 获取token的claims
	 *
	 * @param token
	 * @return
	 * @time 2018年7月12日 下午4:41:30
	 * @author lixiaodong
	 */
	private Claims getClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(jwtConfig.getSecret()).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			LoggerUtil.error(e.getMessage(), e);
			claims = null;
		}
		return claims;
	}

	/**
	 * 生成 过期时间
	 *
	 * @param expiration
	 * @return
	 * @time 2018年7月12日 下午4:47:54
	 * @author lixiaodong
	 */
	private Date generateExpirationDate(long expiration) {
		return new Date(System.currentTimeMillis() + expiration);
	}

	/**
	 * token 是否过期
	 *
	 * @param token
	 * @return
	 * @time 2018年7月12日 下午4:47:21
	 * @author lixiaodong
	 */
	public Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
		return (lastPasswordReset != null && created.before(lastPasswordReset));
	}

	public String generateAccessToken(User user) {
		Map<String, Object> claims = generateClaims(user);
		return generateAccessToken(user.getUsername(), claims);
	}

	private Map<String, Object> generateClaims(User user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_KEY_USER_ID, user.getId());
		claims.put(CLAIM_KEY_ACCOUNT_ENABLED, user.isEnabled());
		claims.put(CLAIM_KEY_ACCOUNT_NON_LOCKED, false);
		claims.put(CLAIM_KEY_ACCOUNT_NON_EXPIRED, false);
		return claims;
	}

	/**
	 * 生成
	 *
	 * @param subject
	 * @param claims
	 * @return
	 * @time 2018年7月12日 下午4:39:00
	 * @author lixiaodong
	 */
	private String generateAccessToken(String subject, Map<String, Object> claims) {
		return generateToken(subject, claims, jwtConfig.getExpiration());
	}

	public String generateRefreshToken(User user) {
		Map<String, Object> claims = generateClaims(user);
		// 只授于更新 token 的权限
		String roles[] = new String[] { ROLE_REFRESH_TOKEN };
		claims.put(CLAIM_KEY_AUTHORITIES, JSONObject.fromObject(roles));
		return generateRefreshToken(user.getUsername(), claims);
	}

	private String generateRefreshToken(String subject, Map<String, Object> claims) {
		return generateToken(subject, claims, jwtConfig.getRefreshExpiration());
	}

	public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
		final Date created = getCreatedDateFromToken(token);
		return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset) && (!isTokenExpired(token));
	}

	/**
	 * 刷新token
	 *
	 * @param token
	 * @return
	 * @time 2018年7月12日 下午4:38:33
	 * @author lixiaodong
	 */
	public String refreshToken(String token) {
		String refreshedToken;
		try {
			final Claims claims = getClaimsFromToken(token);
			refreshedToken = generateAccessToken(claims.getSubject(), claims);
		} catch (Exception e) {
			refreshedToken = null;
		}
		return refreshedToken;
	}

	/**
	 * 生成token
	 *
	 * @param subject
	 * @param claims
	 * @param expiration
	 * @return
	 * @time 2018年7月12日 下午4:38:17
	 * @author lixiaodong
	 */
	private String generateToken(String subject, Map<String, Object> claims, long expiration) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setId(UuidUtil.getUUID()).setIssuedAt(new Date())
				.setExpiration(generateExpirationDate(expiration)).compressWith(CompressionCodecs.DEFLATE)
				.signWith(SIGNATURE_ALGORITHM, jwtConfig.getSecret()).compact();
	}

	/**
	 * 验证token
	 *
	 * @param token
	 * @param user
	 * @return
	 * @time 2018年7月12日 下午4:37:20
	 * @author lixiaodong
	 */
	public Boolean validateToken(String token, User user) {
		final String userId = getUserIdFromToken(token);
		final String userName = getUsernameFromToken(token);
		return (userId.equals(String.valueOf(user.getId())) && userName.equals(user.getUsername())
				&& !isTokenExpired(token));
	}

}
