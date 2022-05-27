package com.example.jwt.configuers;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import com.example.jwt.jwt.Jwt;
import com.example.jwt.jwt.JwtAuthenticationFilter;
import com.example.jwt.jwt.JwtAuthenticationProvider;
import com.example.jwt.user.UserService;

@Configuration
@EnableWebSecurity(debug = true)
public class WebSecurityConfiguer extends WebSecurityConfigurerAdapter {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final JwtConfigure jwtConfigure;

	public WebSecurityConfiguer(JwtConfigure jwtConfigure) {
		this.jwtConfigure = jwtConfigure;
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/assets/**", "/h2-console/**");
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return (request, response, e) -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication != null ? authentication.getPrincipal() : null;
			log.warn("{} is denied", principal, e);
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("text/plain;charset=UTF-8");
			response.getWriter().write("ACCESS DENIED");
			response.getWriter().flush();
			response.getWriter().close();
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public Jwt jwt() {
		return new Jwt(
			jwtConfigure.getIssuer(),
			jwtConfigure.getClientSecret(),
			jwtConfigure.getExpirySeconds()
		);
	}

	@Bean
	public JwtAuthenticationProvider jwtAuthenticationProvider(UserService userService, Jwt jwt) {
		return new JwtAuthenticationProvider(jwt, userService);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Autowired
	public void configureAuthentication(AuthenticationManagerBuilder builder, JwtAuthenticationProvider authenticationProvider) {
		builder.authenticationProvider(authenticationProvider);
	}

	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		Jwt jwt = getApplicationContext().getBean(Jwt.class);
		return new JwtAuthenticationFilter(jwtConfigure.getHeader(), jwt);
	}

	// public SecurityContextRepository securityContextRepository() {
	// 	Jwt jwt = getApplicationContext().getBean(Jwt.class);
	// 	return new JwtSecurityContextRepository(jwtConfigure.getHeader(), jwt);
	// }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers("/api/user/me").hasAnyRole("USER", "ADMIN")
			.anyRequest().permitAll()
			.and()
			/**
			 * formLogin, csrf, headers, http-basic, rememberMe, logout filter 비활성화
			 */
			.formLogin()
			.disable()
			.csrf()
			.disable()
			.headers()
			.disable()
			.httpBasic()
			.disable()
			.rememberMe()
			.disable()
			.logout()
			.disable()
			/**
			 * Session 사용하지 않음
			 */
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			/**
			 * 예외처리 핸들러
			 */
			.exceptionHandling()
			.accessDeniedHandler(accessDeniedHandler())
			.and()
			/**
			 * JwtSecurityContextRepository 설정
			 */
			// .securityContext()
			// .securityContextRepository(securityContextRepository())
			// .and()
		/**
		 * jwtAuthenticationFilter 추가
		 */
		.addFilterAfter(jwtAuthenticationFilter(), SecurityContextPersistenceFilter.class)
		;
	}
}
