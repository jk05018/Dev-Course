package com.example.oauth2.user;

import static com.google.common.base.Preconditions.*;
import static org.apache.logging.log4j.util.Strings.*;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final UserRepository userRepository;
	private final GroupRepository groupRepository;

	public UserService(UserRepository userRepository, GroupRepository groupRepository) {
		this.userRepository = userRepository;
		this.groupRepository = groupRepository;
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
	public Optional<User> findByUsername(String username) {
		checkArgument(isNotEmpty(username), "username must be provided.");

		return userRepository.findByUsername(username);
	}

	@Transactional(readOnly = true)
	public Optional<User> findByProviderAndProviderId(String provider, String providerId) {
		checkArgument(isNotEmpty(provider), "provider must be provided.");
		checkArgument(isNotEmpty(providerId), "providerId must be provided.");

		return userRepository.findByProviderAndProviderId(provider, providerId);
	}

	/**
	 * username - 카카오 닉네임
	 * provider - provider 파라미터
	 * providerId -  oAuth2User.getName()
	 * profileImage - 카카오 인증된 사용자의 프로필 이미지를 사용
	 * group - USER_GROUP Group
	 */
	/* 사용자를 가입 시키는 메서드 */
	@Transactional
	public User join(OAuth2User oauth2User, String authorizedClientRegistrationId) {
		checkArgument(oauth2User != null, "oauth2User must be provided.");
		checkArgument(isNotEmpty(authorizedClientRegistrationId), "authorizedClientRegistrationId must be provided.");

		String providerId = oauth2User.getName();
		return findByProviderAndProviderId(authorizedClientRegistrationId, providerId)
			.map(user -> {
				log.warn("Already exists: {} for (provider: {}, providerId: {})", user, authorizedClientRegistrationId,
					providerId);
				return user;
			})
			.orElseGet(() -> {
				Map<String, Object> attributes = oauth2User.getAttributes();
				@SuppressWarnings("unchecked")
				Map<String, Object> properties = (Map<String, Object>)attributes.get("properties");
				checkArgument(properties != null, "OAuth2User properties is empty");

				String nickname = (String)properties.get("nickname");
				String profileImage = (String)properties.get("profile_image");
				Group group = groupRepository.findByName("USER_GROUP")
					.orElseThrow(() -> new IllegalStateException("Could not found group for USER_GROUP"));
				return userRepository.save(
					new User(nickname, authorizedClientRegistrationId, providerId, profileImage, group)
				);
			});

	}

}
