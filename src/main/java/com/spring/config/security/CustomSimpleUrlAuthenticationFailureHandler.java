package com.spring.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomSimpleUrlAuthenticationFailureHandler  extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        setDefaultFailureUrl("/login?error");

        if(exception.getClass().isAssignableFrom(BadCredentialsException.class)) {
            setDefaultFailureUrl("/login?error=1");
        }
        else if (exception.getClass().isAssignableFrom(DisabledException.class)) {
            setDefaultFailureUrl("/login?error=2");
        }
        else if (exception.getClass().isAssignableFrom(SessionAuthenticationException.class)) {
            setDefaultFailureUrl("/login?error=3"); //if use .maxSessionsPreventsLogin(true), throw exception when user is already logged in another browser
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}