package com.prgms.ssmc.configuers;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import com.prgms.ssmc.user.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguer extends WebSecurityConfigurerAdapter {

	private final Logger log = LoggerFactory.getLogger(WebSecurityConfiguer.class);

	private UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	// @Bean
	// public UserDetailsService userDetailsService(DataSource dataSource) {
	// 	final JdbcDaoImpl jdbcDao = new JdbcDaoImpl();
	// 	jdbcDao.setDataSource(dataSource);
	// 	jdbcDao.setEnableAuthorities(false); // default true
	// 	jdbcDao.setEnableGroups(true); // default false
	// 	jdbcDao.setUsersByUsernameQuery("SELECT " +
	// 		"login_id, passwd, true " +
	// 		"FROM " +
	// 		"USERS " +
	// 		"WHERE " +
	// 		"login_id = ?");
	// 	jdbcDao.setGroupAuthoritiesByUsernameQuery("SELECT " +
	// 		"u.login_id, g.name, p.name " +
	// 		"FROM " +
	// 		"users u JOIN groups g ON u.group_id = g.id " +
	// 		"LEFT JOIN group_permission gp ON g.id = gp.group_id " +
	// 		"JOIN permissions p ON p.id = gp.permission_id " +
	// 		"WHERE " +
	// 		"u.login_id = ?");
	//
	// 	return jdbcDao;
	// }

	// @Bean
	// public UserDetailsManager users(DataSource dataSource) {
	// 	JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
	// 	users.setUsersByUsernameQuery("SELECT " +
	// 		"login_id, passwd, true " +
	// 		"FROM " +
	// 		"USERS " +
	// 		"WHERE " +
	// 		"login_id = ?");
	// 	users.setGroupAuthoritiesByUsernameQuery("SELECT " +
	// 		"u.login_id, g.name, p.name " +
	// 		"FROM " +
	// 		"users u JOIN groups g ON u.group_id = g.id " +
	// 		"LEFT JOIN group_permission gp ON g.id = gp.group_id " +
	// 		"JOIN permissions p ON p.id = gp.permission_id " +
	// 		"WHERE " +
	// 		"u.login_id = ?");
	// 	users.setEnableAuthorities(false); // default true
	// 	users.setEnableGroups(true); // default false
	//
	// 	return users;
	// }

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/assets/**", "/h2-console/**"); // 필터를 그냥 통화 주로 정적 리소스에 사용
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers("/me").hasAnyRole("USER", "ADMIN")
			.antMatchers("/admin").access("hasRole('ADMIN') and isFullyAuthenticated()")
			.anyRequest().permitAll()
			.and()
			.formLogin()
			.defaultSuccessUrl("/")
			.permitAll()
			.and()
			.logout()
			.logoutUrl("/logout")
			.logoutSuccessUrl("/")
			.invalidateHttpSession(true)
			.clearAuthentication(true)
			.and()
			.rememberMe()
			.rememberMeParameter("remember-me")
			.tokenValiditySeconds(300)
			.and()
			.requiresChannel()
			.anyRequest().requiresSecure()
			.and()
			.anonymous()
			.principal("thisIsAnonymousUser")
			.authorities("ROLE_ANONYMOUS", "ROLE_UNKNOWN")
			.and()
			.exceptionHandling()
			.accessDeniedHandler(accessDeniedHandler())
			.and()
			.sessionManagement()
			.sessionFixation().changeSessionId()
			.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
			.invalidSessionUrl("/")
			.maximumSessions(1)
			.maxSessionsPreventsLogin(false)
		;
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
