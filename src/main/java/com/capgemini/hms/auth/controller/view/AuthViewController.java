package com.capgemini.hms.auth.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * View controller for the Login page.
 * Authentication is done client-side via fetch → POST /api/v1/auth/signin
 * JWT token is stored in localStorage after successful login.
 */
@Controller
public class AuthViewController {

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }
}
