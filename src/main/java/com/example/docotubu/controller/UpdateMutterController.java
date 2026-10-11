package com.example.docotubu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.docotubu.model.Mutter;
import com.example.docotubu.model.User;
import com.example.docotubu.service.UpdateMutterService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UpdateMutterController {

    private final UpdateMutterService updateMutterService;

    /**
     * つぶやきの編集画面を表示する（投稿者本人のみ）
     */
    @GetMapping("/updateMutter")
    public String doGet(
            @RequestParam(name = "id", required = false) String idStr,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/index.jsp";
        }

        Mutter mutter = updateMutterService.findOwnMutter(idStr, loginUser.getId());
        if (mutter == null) {
            System.out.println("[UpdateMutterController] 編集できないつぶやきです: id=" + idStr);
            redirectAttributes.addFlashAttribute("errorMsg", "このつぶやきは編集できません");
            return "redirect:/Main";
        }

        model.addAttribute("id", mutter.getId());
        model.addAttribute("text", mutter.getText());
        return "update";
    }

    /**
     * つぶやきの本文を更新する
     */
    @PostMapping("/updateMutter")
    public String doPost(
            @RequestParam(name = "id", required = false) String idStr,
            @RequestParam(name = "text", required = false) String text,
            HttpSession session,
            Model model) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/index.jsp";
        }

        // 未入力チェック
        if (text == null || text.trim().isEmpty()) {
            model.addAttribute("errorMsg", "つぶやき内容を入力してください");
            model.addAttribute("id", idStr);
            model.addAttribute("text", text);
            return "update";
        }

        // 文字数チェック（DBのカラム長を超えると保存できないため）
        if (text.length() > Mutter.MAX_TEXT_LENGTH) {
            model.addAttribute("errorMsg", "つぶやきは" + Mutter.MAX_TEXT_LENGTH + "文字以内で入力してください");
            model.addAttribute("id", idStr);
            model.addAttribute("text", text);
            return "update";
        }

        // 更新処理（本人以外のつぶやき・存在しないIDの場合もfalseになる）
        if (!updateMutterService.execute(idStr, loginUser.getId(), text)) {
            System.out.println("[UpdateMutterController] つぶやきの更新に失敗しました: id=" + idStr);
            model.addAttribute("errorMsg", "更新処理に失敗しました");
            model.addAttribute("id", idStr);
            model.addAttribute("text", text);
            return "update";
        }

        return "redirect:/Main";
    }
}
