package com.example.docotubu.service;

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

    /**
     * 入力されたユーザー名・パスワードに一致するユーザーがDBに存在するか確認する
     * @return 存在する場合はDBから取得したユーザー、存在しない場合はnull
     */
    public User find(User user) {
        System.out.println("[LoginService] 認証処理を開始します: " + user.getName());
        return usersDAO.findUser(user);
    }
}
