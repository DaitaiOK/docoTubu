package com.example.docotubu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import com.example.docotubu.model.User;
import com.example.docotubu.service.LoginService;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/Login")
    public String doPost(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "pass", required = false) String pass,
            HttpSession session,
            Model model) {

        System.out.println("[LoginController] ログインリクエストを受信しました: " + name);

        // 未入力チェック
        if (name == null || name.isBlank() || pass == null || pass.isBlank()) {
            System.out.println("[LoginController] 未入力項目があるためログイン画面へ戻ります");
            model.addAttribute("errorMsg", "ユーザー名とパスワードを入力してください");
            return "forward:/index.jsp";
        }

        try {
            // DBに一致するユーザーが存在するか確認
            User loginUser = loginService.find(new User(name, pass));

            if (loginUser != null) {
                // 認証成功：セッションスコープに保存してメイン画面へ
                System.out.println("[LoginController] ログイン成功。メイン画面へ遷移します");
                session.setAttribute("loginUser", loginUser);
                return "redirect:/Main";
            }

            // 認証失敗：エラーメッセージを持ってログイン画面へ戻る
            System.out.println("[LoginController] ログイン失敗。ログイン画面へ戻ります");
            model.addAttribute("errorMsg", "ユーザー名またはパスワードが間違っています");
        } catch (Exception e) {
            System.out.println("[LoginController] ログイン処理中に予期しないエラーが発生しました");
            e.printStackTrace();
            model.addAttribute("errorMsg", "システムエラーが発生しました。時間をおいて再度お試しください");
        }
        return "forward:/index.jsp";
    }
}
