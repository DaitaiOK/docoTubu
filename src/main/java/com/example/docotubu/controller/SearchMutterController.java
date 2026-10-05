package com.example.docotubu.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.docotubu.model.Mutter;
import com.example.docotubu.model.User;
import com.example.docotubu.service.SearchMutterService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SearchMutterController {

    private final SearchMutterService searchMutterService;

    /**
     * つぶやきをキーワードで検索し、結果をメイン画面に表示する
     */
    @GetMapping("/searchMutter")
    public String doGet(
            @RequestParam(name = "keyword", required = false) String keyword,
            HttpSession session,
            Model model) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/index.jsp";
        }

        List<Mutter> mutterList = searchMutterService.execute(keyword);
        model.addAttribute("mutterList", mutterList);
        model.addAttribute("keyword", keyword);

        return "main";
    }
}
