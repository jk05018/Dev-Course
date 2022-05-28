package com.example.oauth2.user;

import static com.google.common.base.Preconditions.*;
import static org.apache.logging.log4j.util.Strings.*;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// @Transactional(readOnly = true)
	// public User login(String principal, String credentials) {
	// 	checkArgument(isNotEmpty(principal), "principal must be provided.");
	// 	checkArgument(isNotEmpty(credentials), "credentials must be provided.");
	//
	// 	User user = userRepository.findByLoginId(principal)
	// 		.orElseThrow(() -> new UsernameNotFoundException("Could not found user for " + principal));
	// 	user.checkPassword(passwordEncoder, credentials);
	// 	return user;
	// }

	@Transactional(readOnly = true)
	public Optional<User> findByLoginId(String loginId) {
		checkArgument(isNotEmpty(loginId), "loginId must be provided.");
		return userRepository.findByLoginId(loginId);
	}

}
