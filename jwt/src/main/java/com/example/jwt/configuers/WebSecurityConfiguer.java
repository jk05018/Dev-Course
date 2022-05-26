package com.example.jwt.configuers;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.jwt.jwt.Jwt;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguer {

	private final Logger log = LoggerFactory.getLogger(WebSecurityConfiguer.class);

	private UserDetailsService userDetailsService;
	private JwtConfigure jwtConfigure;

	// Setter로 왜??
	@Autowired
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Autowired
	public void setJwtConfigure(JwtConfigure jwtConfigure) {
		this.jwtConfigure = jwtConfigure;
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
					.antMatchers("/api/user/me").hasAnyRole("USER", "ADMIN")
					.anyRequest().permitAll()
			)
			.csrf().disable()
			.headers().disable()
			.formLogin().disable()
			.httpBasic().disable()
			.rememberMe().disable()
			.logout().disable()
			.sessionManagement((sessionMgmt) ->
				sessionMgmt.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			);

		return http.build();
	}

	@Bean
	public Jwt jwt(){
		return new Jwt(
			jwtConfigure.getIssuer(),
			jwtConfigure.getClientSecret(),
			jwtConfigure.getExpirySeconds()
		);
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
