package com.spring.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Copy class by ConcurrentSessionFilter, but I add line 30,83-86,132-134
 */
public class CustomConcurrentSessionFilter extends GenericFilterBean {


    private boolean createNewSession = true;

    // ~ Instance fields
    // ================================================================================================

    private SessionRegistry sessionRegistry;
    private String expiredUrl;
    private LogoutHandler[] handlers = new LogoutHandler[] { new SecurityContextLogoutHandler() };
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    // ~ Methods
    // ========================================================================================================

    public CustomConcurrentSessionFilter(SessionRegistry sessionRegistry) {
        Assert.notNull(sessionRegistry, "SessionRegistry required");
        this.sessionRegistry = sessionRegistry;
    }

    public CustomConcurrentSessionFilter(SessionRegistry sessionRegistry, String expiredUrl) {
        Assert.notNull(sessionRegistry, "SessionRegistry required");
        Assert.isTrue(expiredUrl == null || UrlUtils.isValidRedirectUrl(expiredUrl),
                expiredUrl + " isn't a valid redirect URL");
        this.sessionRegistry = sessionRegistry;
        this.expiredUrl = expiredUrl;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(sessionRegistry, "SessionRegistry required");
        Assert.isTrue(expiredUrl == null || UrlUtils.isValidRedirectUrl(expiredUrl),
                expiredUrl + " isn't a valid redirect URL");
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);

        if (session != null) {
            SessionInformation info = sessionRegistry.getSessionInformation(session.getId());

            if (info != null) {
                if (info.isExpired()) {
                    // Expired - abort processing
                    doLogout(request, response);

                    String targetUrl = determineExpiredUrl(request, info);

                    if (targetUrl != null) {
                        // FIX for generate session when concurrent session go to expiredUrl, else go to next Filters
                        // and when meet SessionManagementFilter find session invalid and redirect to InvalidSessionUrl.
                        // This line copied by SimpleRedirectInvalidSessionStrategy.java used by SessionManagementFilter.java when invalidSessionUrl != null
                        logger.debug("Starting new session (if required) and redirecting to '" + targetUrl + "'");
                        if (createNewSession) {
                            request.getSession();
                        }
                        //end fix

                        redirectStrategy.sendRedirect(request, response, targetUrl);

                        return;
                    }
                    else {
                        response.getWriter().print("This session has been expired (possibly due to multiple concurrent logins being attempted as the same user).");
                        response.flushBuffer();
                    }

                    return;
                }
                else {
                    // Non-expired - update last request date/time
                    sessionRegistry.refreshLastRequest(info.getSessionId());
                }
            }
        }

        chain.doFilter(request, response);
    }

    protected String determineExpiredUrl(HttpServletRequest request, SessionInformation info) {
        return expiredUrl;
    }

    private void doLogout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        for (LogoutHandler handler : handlers) {
            handler.logout(request, response, auth);
        }
    }

    public void setLogoutHandlers(LogoutHandler[] handlers) {
        Assert.notNull(handlers);
        this.handlers = handlers;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    public void setCreateNewSession(boolean createNewSession) {
        this.createNewSession = createNewSession;
    }
}
