package com.example.docotubu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import java.util.List;
import com.example.docotubu.model.Mutter;
import com.example.docotubu.model.User;
import com.example.docotubu.dao.MuttersDAO;
import com.example.docotubu.service.AIPostService;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MuttersDAO muttersDAO;
    private final AIPostService aiPostService;

    @GetMapping("/Main")
    public String doGet(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/index.jsp";
        }

        List<Mutter> mutterList = muttersDAO.findAll();
        model.addAttribute("mutterList", mutterList);
        
        return "main";
    }

    @PostMapping("/Main")
    public String doPost(
            @RequestParam(name = "text", required = false) String text,
            HttpSession session,
            Model model) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/index.jsp";
        }

        if (text != null && text.trim().length() != 0) {
            Mutter mutter = new Mutter(loginUser.getName(), text);
            muttersDAO.create(mutter);
            
            // AI連携（非同期呼び出し）
            aiPostService.execute(mutter);

            return "redirect:/Main";
        } else {
            model.addAttribute("errorMsg", "つぶやきが入力されていません");
            List<Mutter> mutterList = muttersDAO.findAll();
            model.addAttribute("mutterList", mutterList);
            return "main";
        }
    }
}
