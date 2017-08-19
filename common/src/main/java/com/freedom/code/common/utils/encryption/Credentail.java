package com.freedom.code.common.utils.encryption;

import java.net.URL;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 * 凭证类，用于token授权
 * @author luo
 *
 */
public abstract class Credentail {
	protected String grantType;//授权类型
	protected String tokenKey1;//令牌1
	protected String tokenKey2;//令牌2
	//jsonNode工厂类
	protected JsonNodeFactory factory = new JsonNodeFactory(false);
	protected Token token;

	/**
	 * 授权类型泛型
	 * @author luo
	 *
	 */
	public static enum GrantType {
		CLIENT_CREDENTIALS, PASSWORD
	}
	
	/**
	 * 构造函数
	 */
	public Credentail() {
	}
	
	public Credentail(String tokenKey1, String tokenKey2) {
		this.tokenKey1 = tokenKey1;
		this.tokenKey2 = tokenKey2;
	}

	public Credentail(Token token) {
		this.token = token;
	}
	
	public abstract Token getToken();
	protected abstract URL getUrl();
	protected abstract GrantType getGrantType();
	
	/**
	 * 重写的tostring,hashcode,equals方法
	 */
	@Override
	public String toString() {
		return "Credentail [grantType=" + grantType + ", tokenKey1=" + tokenKey1 + ", tokenKey2=" + tokenKey2
				+ ", token=" + token + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((grantType == null) ? 0 : grantType.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		result = prime * result + ((tokenKey1 == null) ? 0 : tokenKey1.hashCode());
		result = prime * result + ((tokenKey2 == null) ? 0 : tokenKey2.hashCode());
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
		Credentail other = (Credentail) obj;
		if (grantType == null) {
			if (other.grantType != null)
				return false;
		} else if (!grantType.equals(other.grantType))
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		if (tokenKey1 == null) {
			if (other.tokenKey1 != null)
				return false;
		} else if (!tokenKey1.equals(other.tokenKey1))
			return false;
		if (tokenKey2 == null) {
			if (other.tokenKey2 != null)
				return false;
		} else if (!tokenKey2.equals(other.tokenKey2))
			return false;
		return true;
	}
}
