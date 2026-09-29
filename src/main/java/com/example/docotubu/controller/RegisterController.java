package com.example.docotubu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.RequiredArgsConstructor;
import com.example.docotubu.model.User;
import com.example.docotubu.service.AIPostService;
import com.example.docotubu.service.RegisterUserService;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterUserService registerUserService;

    /**
     * ユーザー登録画面を表示する
     */
    @GetMapping("/register")
    public String doGet() {
        System.out.println("[RegisterController] ユーザー登録画面を表示します");
        return "register";
    }

    /**
     * ユーザー登録処理
     */
    @PostMapping("/register")
    public String doPost(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "pass", required = false) String pass,
            Model model) {

        System.out.println("[RegisterController] ユーザー登録リクエストを受信しました: " + name);

        // 未入力チェック
        if (name == null || name.isBlank() || pass == null || pass.isBlank()) {
            System.out.println("[RegisterController] 未入力項目があるため登録画面へ戻ります");
            model.addAttribute("errorMsg", "ユーザー名とパスワードを入力してください");
            return "register";
        }

        // 予約名チェック（AIの投稿者名での登録を禁止し、なりすましを防ぐ）
        if (AIPostService.AI_NAME.equals(name.strip())) {
            System.out.println("[RegisterController] 予約済みのユーザー名のため登録画面へ戻ります: " + name);
            model.addAttribute("errorMsg", "そのユーザー名は使用できません");
            return "register";
        }

        try {
            boolean isRegistered = registerUserService.execute(new User(name, pass));

            if (isRegistered) {
                // 登録成功：完了画面へ
                System.out.println("[RegisterController] 登録成功。完了画面へ遷移します");
                return "registerResult";
            }

            // 登録失敗（ユーザー名重複等）：エラーメッセージを持って登録画面へ戻る
            System.out.println("[RegisterController] 登録失敗。登録画面へ戻ります");
            model.addAttribute("errorMsg", "登録に失敗しました。そのユーザー名は既に使用されている可能性があります");
        } catch (Exception e) {
            System.out.println("[RegisterController] 登録処理中に予期しないエラーが発生しました");
            e.printStackTrace();
            model.addAttribute("errorMsg", "システムエラーが発生しました。時間をおいて再度お試しください");
        }
        return "register";
    }
}
