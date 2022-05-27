package com.example.jwt.jwt;

import static com.google.common.base.Preconditions.*;
import static org.apache.commons.lang3.StringUtils.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/* 토큰 용도 */
public class JwtAuthentication {
	// JWTAuthentication 객체는 불면 객체로 만들 것이기 떄문에 필드 접근을 public으로 한다.
	public final String token;

	public final String username;

	JwtAuthentication(String token, String username) {
		checkArgument(isNotEmpty(token), "token must be provided.");
		checkArgument(isNotEmpty(username), "username must be provided.");

		this.token = token;
		this.username = username;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("token", token)
			.append("username", username)
			.toString();
	}
}
