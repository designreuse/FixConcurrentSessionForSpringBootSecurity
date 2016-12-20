package com.spring.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.http.HttpSessionListener;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;

    private static String LOGIN = "/login";
    private static String LOGIN_INVALID_SESSION_URL = "/login?invalidSessionUrl";
    private static String LOGIN_EXPIRED_URL = "/login?expiredUrl";
    private static String ACCESS_DENIED = "/403";

    private static final String[] UNSECURED_RESOURCE_LIST = new String[]{
            LOGIN,
            LOGIN_INVALID_SESSION_URL,
            LOGIN_EXPIRED_URL,
            ACCESS_DENIED
    };

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("admin").password("admin").roles("ADMIN")
                    .and()
                .withUser("user").password("user").roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(customConcurrentSessionFilter(), ConcurrentSessionFilter.class)
            .csrf()
                .and()
            .headers()
                .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
                .xssProtection();

        http.authorizeRequests()
                .antMatchers(UNSECURED_RESOURCE_LIST).permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage(LOGIN)
                .failureHandler(customSimpleUrlAuthenticationFailureHandler())
                .permitAll()
                .and()
            .logout()
                .invalidateHttpSession(false)
                .permitAll()
                .and()
            .sessionManagement()
                .sessionFixation().migrateSession()
                .invalidSessionUrl(LOGIN_INVALID_SESSION_URL)

                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
//                .expiredUrl(LOGIN_EXPIRED_URL) //this line is removed because have a bug, expiredUrl is fixed it with configuration .addFilterBefore(customConcurrentSessionFilter(), ConcurrentSessionFilter.class)
                .sessionRegistry(sessionRegistry());

        http.exceptionHandling()
                .accessDeniedPage(ACCESS_DENIED);
    }

    @Bean
    public CustomConcurrentSessionFilter customConcurrentSessionFilter(){
        return new CustomConcurrentSessionFilter(sessionRegistry(), LOGIN_EXPIRED_URL);
    }

    @Bean
    public CustomSimpleUrlAuthenticationFailureHandler customSimpleUrlAuthenticationFailureHandler(){
        return new CustomSimpleUrlAuthenticationFailureHandler();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public ServletListenerRegistrationBean servletListenerRegistrationBean() {
        return new ServletListenerRegistrationBean(httpSessionEventPublisher());
    }

    @Bean
    public HttpSessionListener httpSessionEventPublisher(){
        //fix for set sessionTimeout, because not load automatically
        int sessionTimeout = env.getProperty("server.session-timeout", Integer.class);

        CustomHttpSessionEventPublisher customHttpSessionEventPublisher = new CustomHttpSessionEventPublisher();
        customHttpSessionEventPublisher.setSessionTimeout(sessionTimeout);
        return customHttpSessionEventPublisher;
    }

}