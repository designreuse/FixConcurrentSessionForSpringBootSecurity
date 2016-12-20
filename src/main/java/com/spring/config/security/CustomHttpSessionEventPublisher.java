package com.spring.config.security;

import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.http.HttpSessionEvent;

public class CustomHttpSessionEventPublisher extends HttpSessionEventPublisher {

    private Integer sessionTimeout;

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        if(sessionTimeout != null){
            event.getSession().setMaxInactiveInterval(sessionTimeout);
        }
        super.sessionCreated(event);
    }

    public void setSessionTimeout(Integer sessionTimeout){
        this.sessionTimeout = sessionTimeout;
    }
}