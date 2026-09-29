package com.example.docotubu.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.docotubu.dao.UsersDAO;
import com.example.docotubu.model.User;

import lombok.RequiredArgsConstructor;

/**
 * ユーザー登録に関する業務ロジック
 */
@Service
@RequiredArgsConstructor
public class RegisterUserService {

    private final UsersDAO usersDAO;
    private final PasswordEncoder passwordEncoder;

    /**
     * ユーザーを登録する（パスワードはBCryptでハッシュ化して保存）
     * @return 登録成功時true、失敗時（ユーザー名重複等）false
     */
    public boolean execute(User user) {
        System.out.println("[RegisterUserService] ユーザー登録処理を開始します: " + user.getName());
        String hashedPass = passwordEncoder.encode(user.getPass());
        return usersDAO.registerUser(new User(user.getName(), hashedPass));
    }
}
