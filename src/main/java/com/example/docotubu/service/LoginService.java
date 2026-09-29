package com.example.docotubu.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.docotubu.dao.UsersDAO;
import com.example.docotubu.model.User;

import lombok.RequiredArgsConstructor;

/**
 * ログイン認証に関する業務ロジック
 */
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UsersDAO usersDAO;
    private final PasswordEncoder passwordEncoder;

    /**
     * 入力されたユーザー名・パスワードに一致するユーザーがDBに存在するか確認する
     * @return 認証成功時はDBから取得したユーザー（パスワードは除く）、失敗時はnull
     */
    public User find(User user) {
        System.out.println("[LoginService] 認証処理を開始します: " + user.getName());

        User dbUser = usersDAO.findByName(user.getName());
        if (dbUser == null) {
            return null;
        }

        // 入力された平文パスワードとDBのハッシュ値を照合する
        if (!passwordEncoder.matches(user.getPass(), dbUser.getPass())) {
            System.out.println("[LoginService] パスワードが一致しません: " + user.getName());
            return null;
        }

        // セッションにハッシュ値を持ち回らないよう、パスワードを除いて返す
        return new User(dbUser.getId(), dbUser.getName(), null);
    }
}
