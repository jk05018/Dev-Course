package com.example.oauth2.oauth2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.example.oauth2.jwt.Jwt;
import com.example.oauth2.user.User;
import com.example.oauth2.user.UserService;

/* 인증이 완료 되었을 때 호출되는 Handler */
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Jwt jwt;

	private final UserService userService;

	public OAuth2AuthenticationSuccessHandler(Jwt jwt, UserService userService) {
		this.jwt = jwt;
		this.userService = userService;
	}


	/**
	 * JWT 토큰을 만ㄷ르어서 응답
	 * 사용자를 가입시키는 처리( 이미 가입되었다면 아무 처리하지 않음)
	 *
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
		if (authentication instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
			OAuth2User principal = oauth2Token.getPrincipal();
			String registrationId = oauth2Token.getAuthorizedClientRegistrationId();

			User user = processUserOAuth2UserJoin(principal, registrationId);

			// 참고로 브라우저에 json 포맷 형태로 토큰값을 눔겨주는데 학습용 프로젝트이기 때문에 이렇게 처리
	 		// 실제 클라이언트가 모바일 앱, IOS 등이라면 앱 전용 schema를 설계하고 schema에 맞춰서 정보를 전달해야 한다.
	 		// 어디까지나 참고로 사용
			String loginSuccessJson = generateLoginSuccessJson(user);
			response.setContentType("application/json;charset=UTF-8");
			response.setContentLength(loginSuccessJson.getBytes(StandardCharsets.UTF_8).length);
			response.getWriter().write(loginSuccessJson);
		} else {
			super.onAuthenticationSuccess(request, response, authentication);
		}
	}

	/* 이미 가입 되었다면 User 아니라면 가입시킨다. */
	private User processUserOAuth2UserJoin(OAuth2User oAuth2User, String registrationId) {
		return userService.join(oAuth2User, registrationId);
	}

	private String generateLoginSuccessJson(User user) {
		String token = generateToken(user);
		log.debug("Jwt({}) created for oauth2 login user {}", token, user.getUsername());
		return "{\"token\":\"" + token + "\", \"username\":\"" + user.getUsername() + "\", \"group\":\"" + user.getGroup().getName() + "\"}";
	}

	private String generateToken(User user) {
		return jwt.sign(Jwt.Claims.from(user.getUsername(), new String[]{"ROLE_USER"}));
	}


}
