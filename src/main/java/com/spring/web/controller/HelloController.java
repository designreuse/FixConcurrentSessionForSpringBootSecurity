package com.spring.web.controller;

import com.spring.web.util.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class HelloController {

    @Value("${prop.hello}")
    private String hello;

    @Autowired
    private SessionUtils sessionUtils;

    @RequestMapping({"/","/hello"})
    public String home(Model model, Principal principal) {

//        sessionUtils.expireUserSessions(principal.getName());
        sessionUtils.printSession();

        return "hello";
    }

    @RequestMapping("/admin")
    @Secured({"ROLE_ADMIN"})
    public String adminPage(Model model, Principal principal) {
        return "admin";
    }


}
