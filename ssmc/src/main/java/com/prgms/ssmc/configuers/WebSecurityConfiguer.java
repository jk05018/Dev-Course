package com.prgms.ssmc.configuers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AliasFor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguer extends WebSecurityConfigurerAdapter {

	private final Logger log = LoggerFactory.getLogger(WebSecurityConfiguer.class);

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser(
				User.builder()
					.username("user")
					.password(passwordEncoder().encode("user123"))
					.roles("USER")
					.build()
			).withUser(
				User.builder()
					.username("admin01")
					.password(passwordEncoder().encode("admin123"))
					.roles("ADMIN")
					.build()
			).withUser(
			User.builder()
				.username("admin02")
				.password(passwordEncoder().encode("admin123"))
				.roles("ADMIN")
				.build()
		);
	}


	@Bean
	PasswordEncoder passwordEncoder(){
		return NoOpPasswordEncoder.getInstance();
	}

	@Bean
	SecurityExpressionHandler<FilterInvocation> securityExpressionHandler(){
		return new CustomWebSecurityExpressionHandler(
			new AuthenticationTrustResolverImpl()
			,"ROLE_"
		);
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/assets/**"); // 필터를 그냥 통화 주로 정적 리소스에 사용
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers("/me").hasAnyRole("USER", "ADMIN")
			.antMatchers("/admin").access("hasRole('ADMIN') and isFullyAuthenticated() and oddAdmin")
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
			.authorities("ROLE_ANONYMOUS","ROLE_UNKNOWN")
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
	public AccessDeniedHandler accessDeniedHandler(){
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
