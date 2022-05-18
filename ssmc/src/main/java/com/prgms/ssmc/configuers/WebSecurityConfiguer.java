package com.prgms.ssmc.configuers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguer extends WebSecurityConfigurerAdapter {

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
					.username("admin")
					.password(passwordEncoder().encode("admin123"))
					.roles("ADMIN")
					.build()
			);
	}


	@Bean
	PasswordEncoder passwordEncoder(){
		return NoOpPasswordEncoder.getInstance();
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
		;
	}
}
