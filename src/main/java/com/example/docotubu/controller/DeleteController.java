package com.example.docotubu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import com.example.docotubu.dao.MuttersDAO;
import com.example.docotubu.model.User;

@Controller
@RequiredArgsConstructor
public class DeleteController {

    private final MuttersDAO muttersDAO;

    /**
     * つぶやきを削除する（投稿者本人のみ）
     */
    @GetMapping("/Delete")
    public String doGet(
            @RequestParam(name = "id", required = false) String idStr,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/index.jsp";
        }

        boolean deleted = false;
        try {
            int id = Integer.parseInt(idStr);
            // 本人以外のつぶやき・存在しないIDの場合はfalseになる
            deleted = muttersDAO.delete(id, loginUser.getId());
        } catch (NumberFormatException e) {
            System.out.println("[DeleteController] つぶやきのIDが不正です: " + idStr);
        }

        if (!deleted) {
            System.out.println("[DeleteController] つぶやきを削除できませんでした: id=" + idStr);
            redirectAttributes.addFlashAttribute("errorMsg", "このつぶやきは削除できません");
        }
        return "redirect:/Main";
    }
}
