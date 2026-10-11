package com.example.docotubu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class LogoutController {

    // 他サイトから勝手にログアウトさせられないよう、POST（CSRFトークン付き）で受け付ける
    @PostMapping("/Logout")
    public String doPost(HttpSession session) {
        session.invalidate();
        return "logout";
    }
}
