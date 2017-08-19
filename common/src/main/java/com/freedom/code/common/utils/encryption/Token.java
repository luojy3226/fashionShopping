package com.freedom.code.common.utils.encryption;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * 身份认证令牌类
 * @author luo
 *
 */
public class Token {
	private String accessToken;//安全令牌
	private Long expiredAt;//超期链接时间
	
	/**
	 * 构造方法
	 * @param accessToken
	 * @param expiredAt
	 */
	public Token(String accessToken, Long expiredAt) {
		this.accessToken = accessToken;
		this.expiredAt = expiredAt;
	}
	
	/**
	 * 设置令牌到请求头中
	 * @param httpMethodEntity
	 * @param credentail
	 */
	public static void applyAuthentication(HttpEntityEnclosingRequestBase httpMethodEntity, Credentail credentail) {
		applyAuthentication(httpMethodEntity, credentail.getToken());
	}
	public static void applyAuthentication(HttpEntityEnclosingRequestBase httpMethodEntity, Token token) {
		httpMethodEntity.addHeader("Authorization", "Bearer " + token.toString());
	}

	public static void applyAuthentication(HttpRequestBase httpMethodEntity, Credentail credentail) {
		applyAuthentication(httpMethodEntity, credentail.getToken());
	}

	public static void applyAuthentication(HttpRequestBase httpMethodEntity, Token token) {
		httpMethodEntity.addHeader("Authorization", "Bearer " + token.toString());
	}
	//判断是否超时
	public boolean isExpired() {
		return System.currentTimeMillis() > expiredAt;
	}
	
	/**
	 * 重写方法
	 */
	@Override
	public String toString() {
		return accessToken;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessToken == null) ? 0 : accessToken.hashCode());
		result = prime * result + ((expiredAt == null) ? 0 : expiredAt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token other = (Token) obj;
		if (accessToken == null) {
			if (other.accessToken != null)
				return false;
		} else if (!accessToken.equals(other.accessToken))
			return false;
		if (expiredAt == null) {
			if (other.expiredAt != null)
				return false;
		} else if (!expiredAt.equals(other.expiredAt))
			return false;
		return true;
	}
}
