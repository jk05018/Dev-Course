package com.example.oauth2.configuers;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import com.example.oauth2.jwt.Jwt;
import com.example.oauth2.jwt.JwtAuthenticationFilter;
import com.example.oauth2.oauth2.OAuth2AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
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
	public Jwt jwt() {
		return new Jwt(
			jwtConfigure.getIssuer(),
			jwtConfigure.getClientSecret(),
			jwtConfigure.getExpirySeconds()
		);
	}

	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		Jwt jwt = getApplicationContext().getBean(Jwt.class);
		return new JwtAuthenticationFilter(jwtConfigure.getHeader(), jwt);
	}

	// @Bean
	// public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
	// 	return new HttpCookieOAuth2AuthorizationRequestRepository();
	// }

	// @Bean
	// public OAuth2AuthorizedClientService authorizedClientService(
	// 	JdbcOperations jdbcOperations,
	// 	ClientRegistrationRepository clientRegistrationRepository
	// ) {
	// 	return new JdbcOAuth2AuthorizedClientService(jdbcOperations, clientRegistrationRepository);
	// }
	//
	// @Bean
	// public OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
	// 	return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
	// }

	// @Bean
	// public OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler(Jwt jwt, UserService userService) {
	// 	return new OAuth2AuthenticationSuccessHandler(jwt, userService);
	// }

	@Bean
	public OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler() {
		return new OAuth2AuthenticationSuccessHandler();
	}



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
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.oauth2Login()
			.successHandler(oauth2AuthenticationSuccessHandler())
			// .authorizationEndpoint()
			// .authorizationRequestRepository(authorizationRequestRepository())
			// .and()
			// .successHandler(getApplicationContext().getBean(OAuth2AuthenticationSuccessHandler.class))
			// .authorizedClientRepository(getApplicationContext().getBean(AuthenticatedPrincipalOAuth2AuthorizedClientRepository.class))
			.and()
			.exceptionHandling()
			.accessDeniedHandler(accessDeniedHandler())
			.and()
			/**
			 * Jwt 필터
			 */
			.addFilterAfter(jwtAuthenticationFilter(), SecurityContextPersistenceFilter.class)
		;
	}
}
