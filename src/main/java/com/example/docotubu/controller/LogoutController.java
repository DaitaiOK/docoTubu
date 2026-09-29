package com.example.docotubu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class LogoutController {

    @GetMapping("/Logout")
    public String doGet(HttpSession session) {
        session.invalidate();
        return "logout";
    }
}
