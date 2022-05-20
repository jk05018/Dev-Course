package com.prgms.ssmc.configuers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEventHandler {

	private final Logger log = LoggerFactory.getLogger(CustomAuthenticationEventHandler.class);

	@EventListener
	public void handleAuthenticationSuccessEvent(AuthenticationSuccessEvent event){
		final Authentication authentication = event.getAuthentication();
		log.info("Successful authentication result : {}", authentication.getPrincipal());
	}

	@EventListener
	public void handleAuthenticationFailureEvent(AbstractAuthenticationFailureEvent event){
		final Exception exception = event.getException();
		final Authentication authentication = event.getAuthentication();
		log.warn("Unsuccessful authentication result : {}", authentication, exception);

	}
}
