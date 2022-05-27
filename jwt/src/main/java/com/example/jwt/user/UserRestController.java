package com.example.jwt.user;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwt.jwt.JwtAuthentication;
import com.example.jwt.jwt.JwtAuthenticationToken;

@RestController
@RequestMapping("/api")
public class UserRestController {

	private final UserService userService;

	private final AuthenticationManager authenticationManager;

	public UserRestController(UserService userService, AuthenticationManager authenticationManager) {
		this.userService = userService;
		this.authenticationManager = authenticationManager;
	}

	@PostMapping("/user/login")
	public UserDto login(@RequestBody LoginRequest request){
		JwtAuthenticationToken authToken = new JwtAuthenticationToken(request.getPrincipal(), request.getCredentials());
		final Authentication resultToken = authenticationManager.authenticate(authToken);
		JwtAuthenticationToken authenticated = (JwtAuthenticationToken)resultToken;
		JwtAuthentication principal = (JwtAuthentication)authenticated.getPrincipal();
		final User user = (User)authenticated.getDetails();ㅁ
		return new UserDto(principal.token, principal.username, user.getGroup().getName());
	}

	@GetMapping("/user/me")
	public UserDto me(@AuthenticationPrincipal JwtAuthentication authentication){
		// @AuthenticationPrincipal 어노테이셔을 붙이면 SecurityContext 에 저장되어 있던 AUTHENTIcation에서 Principal을 가져온다?

	}


}
