package com.autentia.app.domain.web.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value = "/login")
public class LoginController {

    @RequestMapping(method = GET)
    public String goToLoginPage() {
        return "login";
    }

}
