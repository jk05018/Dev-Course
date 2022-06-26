package com.prgms.ssmc.configuers;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguer {

	private final Logger log = LoggerFactory.getLogger(WebSecurityConfiguer.class);

	private final UserDetailsService userDetailsService;

	public WebSecurityConfiguer(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	@Bean
	public WebSecurityCustomizer webSecurityCustomizer(){
		return (web) -> web.ignoring().antMatchers("/assets/**", "/h2-console/**"); // 필터를 그냥 통화 주로 정적 리소스에 사용
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.authorizeRequests((authz) ->
				authz
					.antMatchers("/me").hasAnyRole("USER", "ADMIN")
					.antMatchers("/admin").access("hasRole('ADMIN') and isFullyAuthenticated()")
					.anyRequest().permitAll()
			)
			.formLogin((formLogin) ->
				formLogin
					.defaultSuccessUrl("/")
					.permitAll()
			)
			.logout((logout) ->
				logout
					.logoutUrl("/logout")
					.logoutSuccessUrl("/")
					.invalidateHttpSession(true)
					.clearAuthentication(true)
			)
			.rememberMe((rememberMe) ->
				rememberMe
					.userDetailsService(userDetailsService)
					.rememberMeParameter("remember-me")
					.tokenValiditySeconds(300)
			)
			.requiresChannel((requiresChannel) ->
				requiresChannel
					.anyRequest().requiresSecure()
			)
			.anonymous((anonymous) ->
				anonymous
					.principal("thisIsAnonymousUser")
					.authorities("ROLE_ANONYMOUS", "ROLE_UNKNOWN")
			)
			.exceptionHandling((exhandling) ->
				exhandling
					.accessDeniedHandler(accessDeniedHandler())
			)
			.sessionManagement((sessionmgmt) ->
				sessionmgmt
					.sessionFixation().changeSessionId()
					.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
					.invalidSessionUrl("/")
					.maximumSessions(1)
					.maxSessionsPreventsLogin(false)
			);

		return http.build();
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return (request, response, accessDeniedException) -> {
			final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication != null ? authentication.getPrincipal() : null;
			log.warn("{} was denied", principal, accessDeniedException);
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("text/plain");
			response.getWriter().write("## ACCESS DENIED ##");
			response.getWriter().flush();
			response.getWriter().close();
		};

	}
}
